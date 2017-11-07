package era.server.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ConfigOptsTest {
    @Test
    public void parseArgs() throws Exception {
        String[] args = new String[] {
                "--host",
                "localhost",
                "--port",
                "3001",
                "--user",
                "root",
        };

        Map<ConfigOpts, String> configOptsMap = ConfigOpts.parseArgs(args);

        Assert.assertNotNull(configOptsMap.get(ConfigOpts.HOST));
        Assert.assertEquals(
                "localhost",
                configOptsMap.get(ConfigOpts.HOST)
        );
        Assert.assertNotNull(configOptsMap.get(ConfigOpts.PORT));
        Assert.assertEquals(
                "3001",
                configOptsMap.get(ConfigOpts.PORT)
        );
        Assert.assertNotNull(configOptsMap.get(ConfigOpts.USER));
        Assert.assertEquals(
                "root",
                configOptsMap.get(ConfigOpts.USER)
        );
        Assert.assertNull(configOptsMap.get(ConfigOpts.PASSWORD));
        Assert.assertEquals(
                "fakepassword",
                configOptsMap.getOrDefault(ConfigOpts.PASSWORD, "fakepassword")
        );
    }

}