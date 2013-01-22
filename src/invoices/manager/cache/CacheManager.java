package invoices.manager.cache;

import invoices.manager.logger.LoggerFactory;
import invoices.manager.model.Invoice;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.container.DataContainer;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;

public class CacheManager {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheManager.class);

	private Cache<Integer, Invoice> cache;

	private DefaultCacheManager cacheManager;

	private CacheMode cacheMode;

	/**
	 * Parameter-less constructor defaultly call Local mode  
	 */
	public CacheManager() {
		this(CacheMode.LOCAL);
	}

	public CacheManager(CacheMode cacheMode) {
		super();
		this.cacheMode = cacheMode;
		cacheInitialization();
	}
	
	public void cacheInitialization() {
		try {
			cacheManager = new DefaultCacheManager(
					ConfigurationsBuilder.globalConfiguration(),
					ConfigurationsBuilder.localConfiguration(getCacheMode()), true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void startCache() {
		try {
			cache = cacheManager.getCache();
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

	public void put(Integer key, Invoice value) {
		if (isCacheStarted()) {
			cache.put(key, value);
		}
	}

	public Invoice get(Integer key) {
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

	public Invoice remove(Integer key) {
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
	
	public CacheMode getCacheMode() {
		return cacheMode;
	}

	public void setCacheMode(CacheMode cacheMode) {
		this.cacheMode = cacheMode;
	}

}