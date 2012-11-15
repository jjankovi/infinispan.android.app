package org.infinispan.android.app.cache;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.android.app.logger.LoggerFactory;
import org.infinispan.container.DataContainer;
import org.infinispan.manager.DefaultCacheManager;

public class LocalCacheManager {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalCacheManager.class);

	private Cache<String, String> cache;

	private DefaultCacheManager cacheManager;

//	private String configFile = "gui-demo-cache-config.xml";

	
	public void startCache() {
		try {
			if (cacheManager == null) {
				cacheManager = new DefaultCacheManager();
			}
			cache = cacheManager.getCache();
			cache.start();
			logger.info("Cache was started");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stopCache() {
		if (cache != null) {	
			cache.stop();
			logger.info("Cache was stopped");
			cache = null;
		}
		if (cacheManager != null) {
			cacheManager.stop();
			cacheManager = null;
		}
	}
	
	public void put(String key, String value) {
		handleStoppedCache();
		cache.put(key, value);
	}
	
	public String get(String key) {
		handleStoppedCache();
		return cache.get(key);
	}
	
	public DataContainer getAll() {
		handleStoppedCache();
		return cache.getAdvancedCache().getDataContainer();
	}
	
	public String remove(String key) {
		handleStoppedCache();
		return cache.remove(key);
	}
	
	private void handleStoppedCache() {
		if (cacheManager == null || cache == null) {
			startCache();
		}
	}

}
