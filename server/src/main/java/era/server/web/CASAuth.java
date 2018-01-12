package era.server.web;

import era.server.common.AppConfig;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.LogoutRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;

import java.util.Optional;

import static spark.Spark.*;

public class CASAuth {

Config config = initializeConfig();


    public Config initializeConfig() {
        final CasConfiguration casConfig = new CasConfiguration("https://cas.isg.siue.edu/itscas/login");
        casConfig.setProtocol(CasProtocol.CAS20);

        final Client client = new CasClient(casConfig);
        AppConfig appConfig = AppConfig.instance();
        int port = appConfig.getPort();
        final Clients clients = new Clients("http://localhost:" + port + "/callback", client);
        final Config config = new Config(clients);
        return config;
    }


}