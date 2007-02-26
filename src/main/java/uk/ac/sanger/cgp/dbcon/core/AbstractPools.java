/*
##########################################################################
#                          COPYRIGHT NOTICE                              #
##########################################################################
#                                                                        #
# Copyright (c) 2006 Genome Research Ltd.                                #
# Author: The Cancer Genome Project IT group cancerit@sanger.ac.uk       #
#                                                                        #
# THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,       #
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF     #
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. #
# IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY   #
# CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,   #
# TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE      #
# SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                 #
#                                                                        #
# This code is free software; you can redistribute it and/or modify it   #
# under the terms of the BSD License.                                    #
#                                                                        #
# Any redistribution or derivation in whole or in part including any     #
# substantial portion of this code must include this copyright and       #
# permission notice.                                                     #
#                                                                        #
##########################################################################
*/

package uk.ac.sanger.cgp.dbcon.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.config.ConfigParser;
import uk.ac.sanger.cgp.dbcon.pooling.Pool;

/**
 * The base class for all pool management. Default implementations are available
 * but this version allows for specific subclassing of the pool providers. There
 * is already a singleton implementation which overrides the configuration of
 * each database to give a Pool of size 1. Other versions could be also be
 * generated if required with minimal overriding.
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractPools {

  private final Map pools;

  private final Log log;

  private ConfigParser configParser = null;

  public AbstractPools() {
    pools = Collections.synchronizedMap(new Hashtable());
    log = LogFactory.getLog(this.getClass());
    configParser = ConfigParser.getDefaultParser();
  }

  protected Map getPools() {
    return pools;
  }

  protected Log getLog() {
    return log;
  }

  protected ConfigParser getConfigParser() {
    if(configParser == null) {
      configParser = ConfigParser.getDefaultParser();
    }
    return configParser;
  }

  /**
   * Returns a pool object and will initalise a new pool object if it was not
   * found in the underlying map
   */
  public Pool getPool(String name) {
    Pool output = null;
    if (getPools().containsKey(name)) {
      output = (Pool) getPools().get(name);
    }
    else {
      output = createPool(name);
      getPools().put(name, output);
    }
    return output;
  }

  /**
   * Creates a pool from the configuration as held by this class
   */
  private Pool createPool(String name) {
    Pool output = null;
    Config config = getConfig(name);

    output = new Pool(config);
    output.init();

    return output;
  }

  /**
   * Consults the config parser to see if the synonym is known and therefore a
   * valid pool can be created from
   */
  public boolean isSynonymKnown(String name) {
    List availableDbs = getAvailableDbNames();
    return availableDbs.contains(name);
  }

  /**
   * Used as a wrapper for the getConfig method from the config parser objects
   * however this is a useful extension point to provide differing policies on
   * how to manage the pools e.g. Singletons, unknown databases pooling
   */
  public Config getConfig(String name) {
    return getConfigParser().getConfig(name);
  }

  /**
   * Returns if the pool as given by the synonym is initalised or not. This is
   * true if the pool object was returned from {@link #getPool(String)} and
   * {@link Pool#isPoolInitalised()} was true.
   */
  public boolean isPoolInitalised(String name) {
    boolean initalised = false;
    Pool pool = getPool(name);
    if (pool != null) {
      initalised = pool.isPoolInitalised();
    }
    return initalised;
  }

//  /**
//   * Returns an Iterator of the db synonyms which are available through the
//   * current default parser code. The results are therefore dependant on how
//   * {@link ConfigParser#getAvailableDBs()} returns
//   */
//  public Iterator getAvailableDbNamesIterator() {
//    return Arrays.asList(getConfigParser().getAvailableDBs()).iterator();
//  }
  

//  /**
//   * Returns an Iterator of dbnames defined in this pool
//   * 
//   * @see #getDbNamesIterator()
//   */
//  public Iterator listDbNames() {
//    return getDbNamesIterator();
//  }
  
//  /**
//   * Returns an iterator containing strings refering to configured and
//   * initalised Database pools. This iterator is backed by a copy of the dbnames
//   * and is therefore concurrent safe
//   */
//  public Iterator getDbNamesIterator() {
//    Set copy = new HashSet(getPools().keySet());
//    return copy.iterator();
//  }
  
  public List getAvailableDbNames() {
    return Arrays.asList(getConfigParser().getAvailableDBs());
  }
  
  public List getLoadedDbNames() {
    Set copy = new HashSet(getPools().keySet());
    return new ArrayList(copy);
  }

  /**
   * Loops through all entries in the given pools map, runs the destroy pool
   * method on it and then removes it from the iterating set. This should be
   * reflected in the underlying map.
   */
  public void destroyAllPools() {
    Iterator iter = getAvailableDbNames().iterator();
    while (iter.hasNext()) {
      destroyNamedPool((String)iter.next());
    }
  }
  
  private Object lockObject = new Object();

  public void destroyNamedPool(String name) {
    synchronized (lockObject) {
      Pool pool = getPool(name);
      if (pool != null) {
        pool.destroyPool();
        getPools().remove(name);
      }
    }
  }

  /**
   * Returns the current number of active connections in the pool.
   * 
   * @return Number of active connections. Returns -1 if the pool does not exist
   */
  public int getActiveConnections(String name) {
    int output = -1;
    if (getPool(name) != null) {
      output = getPool(name).getActiveConnections();
    }
    return output;
  }

  /**
   * Returns the current number of idle connections in the pool.
   * 
   * @return Number of idle connections. Returns -1 if the pool does not exist
   */
  public int getIdleConnections(String name) {
    int output = -1;
    if (getPool(name) != null) {
      output = getPool(name).getIdleConnections();
    }
    return output;
  }

  /**
   * Returns the max number of idle connections in the pool.
   * 
   * @return Max number of idle connections. Returns -1 if the pool does not
   *         exist
   */
  public int getMaxIdleConnections(String name) {
    int output = -1;
    if (getPool(name) != null) {
      output = getPool(name).getMaxIdleConnections();
    }
    return output;
  }

  /**
   * Returns the max number of active connections in the pool.
   * 
   * @return Max number of active connections. Returns -1 if the pool does not
   *         exist
   */
  public int getMaxActiveConnections(String name) {
    int output = -1;
    if (getPool(name) != null) {
      output = getPool(name).getMaxActiveConnections();
    }
    return output;
  }

  /**
   * Returns a pool's current status
   */
  public String poolStatus(String name) {

    String status = "The pool " + name + " has not been initalised";
    Pool pool = getPool(name);
    if (pool != null) {
      status = pool.getStatus();
    }

    return status;
  }
  
  /**
   * Method which destroys all known pools and resets the configuration
   * to allow for in VM resetting of pool information
   */
  public void reset() {
    destroyAllPools();
    configParser = null;
  }
}
