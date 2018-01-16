package era.server.web;

import era.server.common.AppConfig;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;

public class CASAuth {

    public static Config initializeConfig() {
        final CasConfiguration casConfig = new CasConfiguration("https://cas.isg.siue.edu/itscas/login");
        //final CasConfiguration casConfig = new CasConfiguration("http://localhost:8080/");
        casConfig.setProtocol(CasProtocol.CAS20);

        final Client client = new CasClient(casConfig);
        AppConfig appConfig = AppConfig.instance();
        int port = appConfig.getPort();
        final Clients clients = new Clients("http://localhost:" + port + "/callback", client);
        final Config config = new Config(clients);
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        return config;
    }


}