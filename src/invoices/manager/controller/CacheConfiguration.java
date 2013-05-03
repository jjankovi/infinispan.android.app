package invoices.manager.controller;

import org.infinispan.configuration.cache.CacheMode;

/**
 * Encapsulates all relevant cache configuration parameters
 * in one place
 * 
 * @author jjankovi
 *
 */
public class CacheConfiguration {

	private CacheMode cacheMode;
	
	private boolean l1Cache;
	private boolean cacheStore;
	private int numOwners;
	
	/**
	 * Returns Cache mode used in app
	 * 
	 * @return
	 */
	public CacheMode getCacheMode() {
		return cacheMode;
	}

	/**
	 * Set Cache mode which should be used in app
	 * 
	 * @param cacheMode
	 */
	public void setCacheMode(CacheMode cacheMode) {
		this.cacheMode = cacheMode;
	}
	
	/**
	 * Returns if l1 cache is enabled in app
	 * 
	 * @return
	 */
	public boolean l1Cache() {
		return l1Cache;
	}
	
	/**
	 * Enable or disable l1 cache in app
	 * 
	 * @param set
	 */
	public void setL1Cache(boolean set) {
		l1Cache = set;
	}

	/**
	 * Returns number of owners in app
	 * 
	 * @return
	 */
	public int getNumOwners() {
		return numOwners;
	}

	/**
	 * Sets number of owners which should be used in app
	 * 
	 * @param numOwners
	 */
	public void setNumOwners(int numOwners) {
		this.numOwners = numOwners;
	}

	/**
	 * Returns if cache store is used in app
	 * 
	 * @return
	 */
	public boolean isCacheStore() {
		return cacheStore;
	}

	/**
	 * Enable or disable cache store in app
	 * 
	 * @param cacheStore
	 */
	public void setCacheStore(boolean cacheStore) {
		this.cacheStore = cacheStore;
	}
	
}
