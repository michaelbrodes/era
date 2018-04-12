package era.uploader.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.Properties;

/**
 * Singleton holder class for every valid property in
 * <em>uploader.properties</em>. uploader.properties is the configuration file
 * for the uploader application. It tells the application how to operate
 * internally. Ideally the only people who should modify the configuration file
 * are either power users or developers but the option is there.
 */
public class UploaderProperties {
    private Boolean uploadingEnabled = null;
    private static final String UPLOADING_ENABLED = "uploading.enabled";
    private String dbUrl = null;
    private static final String DB_URL = "db.url";
    private String emailSuffix = null;
    private static final String EMAIL_SUFFIX = "uploading.email.suffix";
    private String serverHostname = null;
    private static final String SERVER_HOSTNAME = "uploading.server.hostname";
    private String serverPort = null;
    private static final String SERVER_PORT = "uploading.server.port";
    private String serverProtocol = null;
    private static final String SERVER_PROTOCOL = "uploading.server.protocol";
    private static final String PROP_FILE = "uploader.properties";
    private static UploaderProperties INSTANCE;

    public static UploaderProperties instance() {
        INSTANCE = Threads.doubleCheck(
                INSTANCE,
                UploaderProperties::new,
                UploaderProperties.class
        );

        return INSTANCE;
    }

    public Boolean isUploadingEnabled() {
        Optional<Boolean> ret = Optional.ofNullable(uploadingEnabled);
        if (!ret.isPresent()) {
            ret = getProperties()
                    .map((props) -> props.getProperty(
                            UPLOADING_ENABLED, Boolean.FALSE.toString()
                    ))
                    .map(Boolean::parseBoolean);
            uploadingEnabled = ret.orElse(null);
        }

        return uploadingEnabled;
    }

    public String getEmailSuffix() {
        Optional<String> ret = Optional.ofNullable(emailSuffix);
        if (!ret.isPresent()) {
            String defaultSuffix = "siue.edu";
            ret = getProperties()
                    .map((props) -> props.getProperty(
                            EMAIL_SUFFIX, defaultSuffix
                    ));
            dbUrl = ret.orElse(null);
        }

        return ret.orElse("");
    }

    public Optional<String> getDbUrl() {
        Optional<String> ret = Optional.ofNullable(dbUrl);
        if (!ret.isPresent()) {
            final String defaultPath = "jdbc:sqlite:"
                    + new File("uploader.db").getAbsolutePath();
            ret = getProperties()
                    .map((props) -> props.getProperty(
                            DB_URL, defaultPath
                    ));
            dbUrl = ret.orElse(null);
        }

        return ret;
    }

    public Optional<String> getServerURL () {
        Optional<String> ret = Optional.empty();
        if (serverHostname == null || serverPort == null || serverProtocol == null) {
            Optional<Properties> configProperties = getProperties();
            Optional<String> hostname = configProperties
                    .map(props -> props.getProperty(SERVER_HOSTNAME, "localhost"));
            Optional<String> port = configProperties
                    .map(properties -> properties.getProperty(SERVER_PORT));
            Optional<String> protocol = configProperties
                    .map(properties -> properties.getProperty(SERVER_PROTOCOL, "http"));
            if (hostname.isPresent()
                && port.isPresent()
                && protocol.isPresent()) {
                ret = Optional.of(protocol.get()
                        + "://"
                        + hostname.get()
                        + ":"
                        + port.get());
            } else if (hostname.isPresent() && protocol.isPresent()) {
                ret = Optional.of(protocol.get()
                        + "://"
                        + hostname.get());
            }
        }

        return ret;
    }

    private Optional<Properties> getProperties() {
        File propFile = new File(PROP_FILE);
        Optional<Properties> ret = Optional.empty();

        try (Reader propReader = new FileReader(propFile)) {
            Properties properties = new Properties();
            properties.load(propReader);
            ret = Optional.of(properties);
        } catch (IOException e) {
            // no op because we are returning Optional.empty() in this case
        }

        return ret;
    }

    public File getFile() {
        return new File(PROP_FILE);
    }

    public void setUploadingEnabled(Boolean uploadingEnabled) {
        this.uploadingEnabled = uploadingEnabled;
        Optional<Properties> properties = getProperties();
        if (properties.isPresent()) {
            properties.get().setProperty("uploading.enabled", uploadingEnabled.toString());
            storeProperties(properties.get());
        }
    }

    public void storeProperties(Properties properties){
        try {
            FileOutputStream out = new FileOutputStream("uploader.properties");
            properties.store(out, "uploader.properties updated");
        }
        catch (IOException e)
        {
            System.out.println("error when trying to update uploader.properties");
        }
    }
}
