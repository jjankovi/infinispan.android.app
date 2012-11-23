package org.infinispan.android.app.cache;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.android.app.logger.LoggerFactory;
import org.infinispan.android.app.model.CacheElement;
import org.infinispan.container.DataContainer;
import org.infinispan.manager.DefaultCacheManager;

public class LocalCacheManager implements Serializable{
	
	private static final long serialVersionUID = 4062592999323781354L;

	private static final Logger logger = LoggerFactory.getLogger(LocalCacheManager.class);

	private Cache<Integer, CacheElement> cache;

	private DefaultCacheManager cacheManager;

//	private String configFile = "gui-demo-cache-config.xml";

	
	public void startCache() {
		try {
			if (cacheManager == null) {
				cacheManager = new DefaultCacheManager();
				cache = cacheManager.getCache();
				cache.start();				
				logger.info("Cache was started");
			}
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
	
	public void put(Integer key, CacheElement value) {
		if (isCacheStarted()) {
			cache.put(key, value);
		}
	}
	
	public CacheElement get(Integer key) {
		if (isCacheStarted()) {
			return cache.get(key);
		}
		return null;
	}
	
	public DataContainer getAll() {
		if (isCacheStarted()) {
			return cache.getAdvancedCache().getDataContainer();
		}
		return null;
	}
	
	public CacheElement remove(Integer key) {
		if (isCacheStarted()) {
			return cache.remove(key);
		}
		return null;
		
	}
	
	public boolean isCacheStarted() {
		return cacheManager != null && cache != null;
	}

}
