package invoices.manager.controller;

import java.util.Random;

import invoices.manager.logger.LoggerFactory;
import invoices.manager.model.Invoice;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.container.DataContainer;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;

/**
 * 
 * @author jjankovi
 *
 */
public class CacheManager {

	private static final Logger logger = LoggerFactory
			.getLogger(CacheManager.class);

	private Cache<Integer, Invoice> cache;

	private DefaultCacheManager cacheManager;

	private CacheMode cacheMode;
	
	private boolean l1Cache;
	private int numOwners;

	/**
	 * Parameter-less constructor defaultly call Local mode  
	 */
	public CacheManager() {
		this(CacheMode.LOCAL);
	}

	public CacheManager(CacheMode cacheMode) {
		super();
		this.cacheMode = cacheMode;
		this.l1Cache = false;
		this.numOwners = 1;
		cacheInitialization();
	}
	
	public void cacheInitialization() {
		try {
			cacheManager = new DefaultCacheManager(
					globalConfiguration(),
					localConfiguration(),
					true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private GlobalConfiguration globalConfiguration() {
		return new GlobalConfigurationBuilder()
				.transport()
				.defaultTransport()
				.addProperty("configurationFile", "jgroups.xml")
				.build();
	}
	
	private Configuration localConfiguration() {
		return new ConfigurationBuilder()
				.transaction() 										/** enter to transaction-specific options**/
					.lockingMode(LockingMode.OPTIMISTIC)			/** set optimistic transaction locking mode **/
					.transactionMode(TransactionMode.TRANSACTIONAL) /** set transactional model **/
				.clustering() 										/** enter to cluster-specific options **/
					.cacheMode(getCacheMode()) 						/** set clustering mode **/
					.l1().enabled(l1Cache()) 						/** enable/disable l1 cache **/
					.hash().numOwners(getNumOwners()) 				/** set num owners **/
				.build();
	}
 
	public void startCache() {
		try {
			if (cacheManager == null || 
				cacheManager.getStatus() != ComponentStatus.RUNNING) {
				cacheInitialization();
			}
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
	
	public Integer getNextKey() {
		Integer random = null;
		do {
			random = new Random().nextInt(Integer.MAX_VALUE) + 1;
		} while (cache.containsKey(random));
		
		return random;
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
	
	public boolean l1Cache() {
		return l1Cache;
	}
	
	public void setL1Cache(boolean set) {
		l1Cache = set;
	}

	public int getNumOwners() {
		return numOwners;
	}

	public void setNumOwners(int numOwners) {
		this.numOwners = numOwners;
	}
	
}