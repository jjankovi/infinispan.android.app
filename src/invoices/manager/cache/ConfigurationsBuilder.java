package invoices.manager.cache;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;


public class ConfigurationsBuilder {

	public static GlobalConfiguration globalConfiguration() {
		return new GlobalConfigurationBuilder().transport().
//				defaultTransport().addProperty("configurationFile", 
//				"jgroups-tcp.xml").addProperty("jgroups.tcp.port=1234", "10.0.2.2").build();
				defaultTransport().addProperty("configurationFile", 
						"jgroups.xml").build();
	}
	
	public static Configuration localConfiguration(CacheMode clusterMode) {
		return new ConfigurationBuilder().clustering()
				.cacheMode(clusterMode).build();
	}
	
}
