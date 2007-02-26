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

package uk.ac.sanger.cgp.dbcon.pooling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.exceptions.ConnectionNotAvailableException;
import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.exceptions.PoolingException;
import uk.ac.sanger.cgp.dbcon.util.DatabaseUtils;

/**
 * Main class controlling the lifecycle of the pool. This object gives access to
 * the basic pools, data sources and the ability to reset the contents.
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public class Pool {

  private Config config = null;

  private DataSource dataSource = null;

  private GenericObjectPool underlyingConnectionPool = null;

  private Log log = LogFactory.getLog(this.getClass());

  private Pool() {
    super();
  }

  public Pool(Config config) {
    setConfig(config);
  }

  private void setConfig(Config config) {
    this.config = config;
  }

  public Config getConfig() {
    return config;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  private void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private GenericObjectPool getUnderlyingConnectionPool() {
    return underlyingConnectionPool;
  }

  private void setUnderlyingConnectionPool(
      GenericObjectPool underlyingConnectionPool) {
    this.underlyingConnectionPool = underlyingConnectionPool;
  }

  public Log getLog() {
    return log;
  }

  /**
   * Performs the creation of the Pool via internal delegate methods
   */
  public void init() {
    if (!isPoolInitalised()) {
      assertSetupOkay();

      initaliseJdbcDriver();

      setupConnection();

      createPool();

      checkVendorSpecificBugs();
    }
  }

  /**
   * Asserts that initalisation can proceed
   */
  public boolean isPoolInitalised() {
    return (getUnderlyingConnectionPool() != null);
  }

  /**
   * Ensures that pool creation can go ahead
   */
  private void assertSetupOkay() {
    if (getConfig() == null) {
      throw new PoolingException("Configuration was null. Cannot create "
          + "a pool from null config");
    }
  }

  /**
   * Used to create an instace of the configured JDBC driver to ensure that it
   * is available for creating a pool from.
   */
  private void initaliseJdbcDriver() {
    if (StringUtils.isEmpty(getConfig().getDriver())) {
      throw new PoolingException("Detected driver config has resulted "
          + "in a driver String of null or zero length. Cannot create a "
          + "database pool from this");
    }

    Class driver = null;
    try {
      driver = Class.forName(getConfig().getDriver());
    }
    catch (ClassNotFoundException e) {
      throw new PoolingException("Class could not be found for driver "
          + getConfig().getDriver());
    }

    if (driver == null) {
      throw new PoolingException("Driver null detected. Check that you "
          + "have the requested JDBC driver " + getConfig().getDriver()
          + " in " + "your classpath");
    }

    Object driverInstance = null;
    try {
      driverInstance = driver.newInstance();
    }
    catch (InstantiationException e) {
      throw new PoolingException("Could not initalise driver", e);
    }
    catch (IllegalAccessException e) {
      throw new PoolingException("Could not initalise driver", e);
    }

    if (driverInstance == null) {
      throw new PoolingException(
          "Could not create a JDBC driver Object instance");
    }

    if (getLog().isInfoEnabled())
      getLog().info("Driver " + getConfig().getDriver() + " found");
  }

  private void setupConnection() {
    try {
      DatabaseUtils.getValidConnectionURL(config);
    }
    catch (DbConException e) {
      throw new PoolingException(
          "No valid URL could be found. Aborting pool formation", e);
    }
  }

  /**
   * Creates the pool and the data source which provides the pooling ability
   */
  private void createPool() {
    PoolableObjectFactory pof = null;

    // Creation of the generic pool and linking a factory to it
    GenericObjectPool underlyingConnectionPool = new GenericObjectPool(pof,
        config.getMaxActive(), config.getExhausted(), config.getMaxWait(),
        config.getMaxIdle(), config.isTestOnBorrow(), config.isTestOnReturn(),
        config.getTimeBetweenEvictRun(), config.getNumTestsPerEvictionRun(),
        config.getMinEvictTime(), config.isTestWhileIdle());

    setUnderlyingConnectionPool(underlyingConnectionPool);

    // This section allows for PreparedStatements to be used
    GenericKeyedObjectPoolFactory kopf = null;

    if (config.getCachedPreparedStatements() != 0) {
      kopf = new GenericKeyedObjectPoolFactory(null, -1, // unlimited maxActive
          // (per key)
          GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL, 0, // maxWait
          1, // maxIdle (per key)
          config.getCachedPreparedStatements());
    }

    // Creating the correct connection factory
    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
        config.getWorkingUrl(), config.getUsername(), config.getPassword());

    // Final construction of the poolable connection factory from:
    // PoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool,
    // KeyedObjectPoolFactory stmtPoolFactory, String validationQuery,
    // boolean defaultReadOnly, boolean defaultAutoCommit)

    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
        connectionFactory, getUnderlyingConnectionPool(), kopf, null, false,
        false);

    // Setting the validation query
    poolableConnectionFactory.setValidationQuery(config.getValidationQuery());

    // Creating the pooled datasource
    setDataSource(new PoolingDataSource(getUnderlyingConnectionPool()));
  }

  /**
   * This method uses a new blank connection and not the pool to see if there
   * are any issues with the Driver and if any fixes need to be implemented. If
   * we used a Pool Connection then any problems with the Connection/Driver
   * would be kept in the pool. This way we check a potential Connection and
   * modify settings accordingly.
   */
  private void checkVendorSpecificBugs() {

    Connection conn = null;

    try {
      conn = DriverManager.getConnection(config.getWorkingUrl(), config
          .getUsername(), config.getPassword());
      DatabaseUtils.setOraclePropertyForBugFix(conn);
    }
    catch (SQLException e) {
      if (getLog().isErrorEnabled())
        getLog().error("Error occured whilst testing connection for settings", e);
    }
    finally {
      DatabaseUtils.closeDbObject(conn);
    }
  }
  
  public Connection getConnection() {
    try {
      Connection conn = getDataSource().getConnection();
      DatabaseUtils.connectionSettings(conn);
      return conn;
    }
    catch (SQLException e) {
      throw new ConnectionNotAvailableException("Detected SQLException when retriving connection", e);
    }
  }

  /**
   * Runs a close statement on the underlying connection pool object and nulls
   * the provided data source object.
   */
  public void destroyPool() {
    try {
      getUnderlyingConnectionPool().close();
    }
    catch (Exception e) {
      if (getLog().isErrorEnabled()) {
        getLog().error(
            "Detected exception whilst closing down connection pool", e);
      }
    }
    finally {
      setDataSource(null);
    }
  }
  
  /**
   * Returns the current number of active connections in the pool.
   */
  public int getActiveConnections() {
    return getUnderlyingConnectionPool().getNumActive();
  }

  /**
   * Returns the current number of idle connections in the pool.
   */
  public int getIdleConnections() {
    return getUnderlyingConnectionPool().getNumIdle();
  }

  /**
   * Returns the max number of idle connections in the pool.
   */
  public int getMaxIdleConnections() {
    return getUnderlyingConnectionPool().getMaxIdle();
  }

  /**
   * Returns the max number of idle connections in the pool.
   */
  public int getMaxActiveConnections() {
    return getUnderlyingConnectionPool().getMaxActive();
  }
  
  /**
   * Produces a string showing the current status of the pool
   */
  public String getStatus() {
    String nl = System.getProperty("line.separator");

    StringBuffer sb = new StringBuffer();

    sb.append(getConfig().getName());
    sb.append(" Pool Status");
    sb.append(nl);
    sb.append("====================");
    sb.append(nl);
    sb.append("Current number of active connections: ");
    sb.append(getUnderlyingConnectionPool().getNumActive());
    sb.append(nl);

    sb.append("Current number of idle connections: ");
    sb.append(getUnderlyingConnectionPool().getNumIdle());
    sb.append(nl);

    sb.append("Max number of active connections: ");
    sb.append(getUnderlyingConnectionPool().getMaxActive());
    sb.append(nl);

    sb.append("Max number of idle connections: ");
    sb.append(getUnderlyingConnectionPool().getMaxIdle());
    sb.append(nl);

    sb.append("Dump of current pool object: ");
    sb.append(nl);
    sb.append(getUnderlyingConnectionPool().toString());
    sb.append(nl);

    return sb.toString();
  }
}
