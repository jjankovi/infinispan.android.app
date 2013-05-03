package invoices.manager.controller;

import invoices.manager.logger.LoggerFactory;
import invoices.manager.model.Invoice;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.HashConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.global.TransportConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;

/**
 * CacheManager is bridge between graphical interface and underlying 
 * layer - Infinispan. It is the main Controller class of MVC model in
 * this application
 * 
 * @author jjankovi
 *
 */
public class CacheManager {

	private static final Logger log = LoggerFactory
			.getLogger(CacheManager.class);

	private Cache<Integer, Invoice> cache;

	private DefaultCacheManager cacheManager;
	
	private CacheConfiguration cacheConfiguration = new CacheConfiguration();

	/**
	 * Creates default Cache Manager object with LOCAL cache mode  
	 */
	public CacheManager() {
		this(CacheMode.LOCAL);
	}

	/**
	 * Creates Cache Manager object with given cache mode 
	 * 
	 * @param 		cacheMode which should be used for rest of configuration
	 */
	public CacheManager(CacheMode cacheMode) {
		super();
		
		cacheConfiguration().setCacheMode(cacheMode);
		cacheConfiguration().setL1Cache(false);
		cacheConfiguration().setNumOwners(1);
		cacheConfiguration().setCacheStore(false);
		
		cacheInitialization();
	}
	
	/**
	 * Creates a new instance of Infinispan Cache Manager and
	 * invoke its global and local configuration process
	 */
	public void cacheInitialization() {
		try {
			log.info("Cache initization is starting");
			cacheManager = new DefaultCacheManager(
					globalConfiguration(),
					localConfiguration(),
					true);
			log.info("Cache initization was successfully performed");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private GlobalConfiguration globalConfiguration() {
		log.info("Global configuration is starting");
		TransportConfigurationBuilder globalBuilder = new GlobalConfigurationBuilder()
			.transport()
			.defaultTransport()
			.addProperty("configurationFile", "jgroups.xml");
		log.info("Global configuration was successfully performed");
		return globalBuilder.build();
	}
	
	private Configuration localConfiguration() {
		log.info("Local configuration is starting");
		HashConfigurationBuilder localConfiguration = new ConfigurationBuilder()
			.transaction() 											 /** enter to transaction-specific options **/
				.lockingMode(LockingMode.OPTIMISTIC)				 /** set optimistic transaction locking mode **/
				.transactionMode(TransactionMode.TRANSACTIONAL) 	 /** set transactional model **/
			.clustering() 											 /** enter to cluster-specific options **/
				.cacheMode(cacheConfiguration.getCacheMode()) 		 /** set clustering mode **/
				.l1().enabled(cacheConfiguration.l1Cache()) 		 /** enable/disable l1 cache **/
				.hash().numOwners(cacheConfiguration.getNumOwners());/** set num owners **/
			
		if (cacheConfiguration.isCacheStore()) {
			localConfiguration
				.loaders()											 /** enter to loaders-specific options **/
					.passivation(true)								 /** enable passivation **/
					.preload(true)									 /** enable preload **/
					.addFileCacheStore()							 /** default cache store is FileCacheStore **/
						.location(System.getProperty(				 /** set location of file **/
								"java.io.tmpdir"))
				.eviction()											 /** enter to eviction-specific options **/
					.strategy(EvictionStrategy.LIRS)				 /** set eviction strategy **/
					.maxEntries(100);								 /** set max entries **/
		}
		log.info("Local configuration was successfully performed");
		
		log.info("Local configuration: ");
		log.info("	Cache mode  	  ==>  " + cacheConfiguration.getCacheMode());
		log.info("	Number of owners  ==>  " + cacheConfiguration.getNumOwners());
		log.info("	L1  			  ==>  " + (cacheConfiguration.l1Cache()?"enabled":"disabled"));
		log.info("	Cache store  	  ==>  " + (cacheConfiguration.isCacheStore()?"enabled":"disabled"));
		
		return localConfiguration.build();
	}
 
	/**
	 * Starts a cache. If cache manager was not configured yet, cache initialization
	 * is performed and cache is started right after that
	 */
	public void startCache() {
		try {
			log.info("Cache is starting");
			if (cacheManager == null || 
				cacheManager.getStatus() != ComponentStatus.RUNNING) {
				cacheInitialization();
			}
			cache = cacheManager.getCache();
			log.info("Cache was started");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops a cache and cache manager as well
	 */
	public void stopCache() {
		log.info("Cache is stopping");
		if (cache != null) {
			cache.stop();
			log.info("Cache was stopped");
			cache = null;
		}
		if (cacheManager != null) {
			cacheManager.stop();
		}
	}
	
	/**
	 * Performs put operation over cache model object
	 * 
	 * @param 		key
	 * @param 		value
	 */
	public void put(Integer key, Invoice value) {
		if (isCacheStarted()) {
			cache.put(key, value);
			log.info("New Invoice object was put into a cache: ");
			log.info("	ID 			   => " + value.getId());
			log.info("	Account Number => " + value.getAccountNumber());
			log.info("	Bank Code	   => " + value.getBankCode());
			log.info("	Price 		   => " + value.getPrice());
			log.info("	Date of Issue  => " + value.getDateOfIssue().getFormattedDate());
			log.info("	Maturity Date  => " + value.getMaturityDate().getFormattedDate());
			log.info("	Notes 		   => " + value.getNotes());
		}
	}

	/**
	 * Returns Invoices object according to given key
	 * 
	 * @param 		key
	 * @return
	 */
	public Invoice get(Integer key) {
		if (isCacheStarted()) {
			return cache.get(key);
		}
		return null;
	}

	/**
	 * Returns collection of all cache elements (Invoices)
	 * 
	 * @return
	 */
	public Collection<Entry<Integer, Invoice>> getAll() {
		if (isCacheStarted()) {
			return cache.entrySet();
		}
		return null;
	}

	/**
	 * Remove specific Invoice from cache. Given key is used as
	 * selection rule
	 * 
	 * @param 		key
	 * @return
	 */
	public Invoice remove(Integer key) {
		if (isCacheStarted()) {
			Invoice removedElement = cache.remove(key);
			log.info("Invoice with key: " + key + " was removed from cache");
			return removedElement;
		}
		return null;

	}
	
	/**
	 * Constructs a new random integer value which is not already used in cache
	 * 
	 * @return
	 */
	public Integer getNextKey() {
		Integer random = null;
		do {
			random = new Random().nextInt(Integer.MAX_VALUE) + 1;
		} while (cache.containsKey(random));
		
		return random;
	}

	/**
	 * Determines if cache has been already started
	 * 
	 * @return
	 */
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

	/**
	 * Returns cache configuration
	 * 
	 * @return
	 */
	public CacheConfiguration cacheConfiguration() {
		return cacheConfiguration;
	}
	
}