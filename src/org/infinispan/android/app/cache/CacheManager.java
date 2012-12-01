package org.infinispan.android.app.cache;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.android.app.logger.LoggerFactory;
import org.infinispan.android.app.model.ShopItem;
import org.infinispan.container.DataContainer;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;

public class CacheManager {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(CacheManager.class);

	private Cache<Integer, ShopItem> cache;

	private DefaultCacheManager cacheManager;
	
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
	
	public void put(Integer key, ShopItem value) {
		if (isCacheStarted()) {
			cache.put(key, value);
		}
	}
	
	public ShopItem get(Integer key) {
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
	
	public ShopItem remove(Integer key) {
		if (isCacheStarted()) {
			return cache.remove(key);
		}
		return null;
		
	}
	
	public boolean isCacheStarted() {
		boolean cacheManagerState;
		boolean cacheState = false;
		if (cacheManager == null) {
			cacheManagerState = false;
		} else {
			cacheManagerState = cacheManager.getStatus() == ComponentStatus.RUNNING;
		}
		if (cache == null) {
			cacheState = false;
		} else {
			cacheState = cache.getStatus() == ComponentStatus.RUNNING; 
		}
		return cacheManagerState && cacheState;			   
	}

}
