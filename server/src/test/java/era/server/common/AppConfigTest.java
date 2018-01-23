package era.server.common;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class AppConfigTest {
    @Test
    @Ignore
    public void setConnectionString_integratesWithDb() throws Exception {
        String[] args = new String[] {
                "--host",
                "localhost",
                "--port",
                "3001",
                "--user",
                "root",
                "--password",
                "fakepassword"
        };

        Map<ConfigOpts, String> configOpts= ConfigOpts.parseArgs(args);
        AppConfig.instance()
                .setConnectionString(
                        configOpts.get(ConfigOpts.HOST),
                        configOpts.get(ConfigOpts.PORT),
                        configOpts.get(ConfigOpts.USER),
                        configOpts.get(ConfigOpts.PASSWORD)
                );

        Assert.assertEquals(
                "jdbc:mariadb://localhost:3001/era?user=root&password=fee3kie0paed0ahghe1iH8reol8aepaN",
                AppConfig.instance().getConnectionString()
        );

        Connection connection = DriverManager.getConnection(AppConfig.instance().getConnectionString());
        Assert.assertTrue(!connection.isClosed());
        connection.close();
    }

}