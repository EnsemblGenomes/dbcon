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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.pooling.Pool;
import uk.ac.sanger.cgp.dbcon.pooling.PooledDataSource;
import uk.ac.sanger.cgp.dbcon.util.DatabaseUtils;

/**
 * <p>
 * Used as a default implementation of the data source object which uses the
 * AbstractPools objects to retrieve pools and their connections from the given
 * synonym specified through construction.
 *
 * <p>
 * This is a thin wrapper around the DBCP DataSource object which is linked to
 * the shared connection pool. Be aware that <strong>ALL</strong> Connections
 * retrived from this object <strong>MUST</strong> be closed otherwise you will
 * have impacts on other systems with locking out connections and causing the pool
 * to be filled.
 *
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 * @see <a href="http://jakarta.apache.org/commons/dbcp/apidocs/org/apache/commons/dbcp/package-summary.html">DBCP DataSources</a>
 */
public abstract class AbstractDbConDataSource implements DataSource {

  private Log log = LogFactory.getLog(this.getClass());

  private final String name;

  /**
   * Creates a new instance of PooledDataSource
   */
  public AbstractDbConDataSource(String name) {
    super();
    this.name = name;
  }

  protected Log getLog() {
    return log;
  }

  /**
   * Throws UnsupportedOperationException.
   */
    @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    getDataSource().setLoginTimeout(seconds);
  }

  /**
   * Wrapper around the underlying DataSource's method
   */
  public void setLogWriter(PrintWriter out) throws SQLException {
    getDataSource().setLogWriter(out);
  }

  /**
   * Runs a basic toString
   */
  @Override
  public String toString() {
    String retValue;
    retValue = super.toString();
    retValue = retValue + "DB Name: " + getName();
    return retValue;
  }

  /**
   * Throws UnsupportedOperationException.
   */
  @Override
  public int getLoginTimeout() throws SQLException {
    return getDataSource().getLoginTimeout();
  }

  /**
   * Wrapper around the underlying DataSource's method
   */
    @Override
  public PrintWriter getLogWriter() throws SQLException {
    return getDataSource().getLogWriter();
  }

  /**
   * Gets a connection from the underling DataSource, using a given username and
   * password. Applies the settings specified by
   * {@link DatabaseUtils#connectionSettings(Connection)} Throws
   * UnsupportedOperationException.
   */
    @Override
  public Connection getConnection(String username, String password)
      throws SQLException {
    Connection conn = getDataSource().getConnection(username, password);
    DatabaseUtils.connectionSettings(conn);
    return conn;
  }

  /**
   * Gets a connection from the underling DataSource using the username and
   * password specified at creation. Applies the settings specified by
   * {@link DatabaseUtils#connectionSettings(Connection)}
   */
    @Override
  public Connection getConnection() throws SQLException {
    Connection conn = getDataSource().getConnection();
    DatabaseUtils.connectionSettings(conn);
    return conn;
  }

  /**
   * Returns true if the synonym/name used to create this class with has
   * resulted in a Pool being created.
   */
  public boolean isSynonymFound() throws SQLException {
    return getBackingAbstractPoolsInstance().isSynonymKnown(name);
  }

  /**
   * Wrapper to extract the DataSource for the specified named pool. This
   * happens every single time to maintain updates with the potentially ever
   * changing
   *
   */
  protected DataSource getDataSource() throws SQLException {
    return getUnderlyingPool().getDataSource();
  }

  /**
   * Used to provide the backing instance method which links pooling provided by
   * the AbstractPools classes to this datasource
   */
  protected abstract AbstractPools getBackingAbstractPoolsInstance();

  /**
   * Finds the pool which is represented by the synonym given by this Object
   *
   * @throws SQLException
   *           If any DbConException is detected. This is logged here and
   *           re-thrown with information but no nesting since SQLException does
   *           not support this
   */
  protected Pool getUnderlyingPool() throws SQLException {
    try {
      return getBackingAbstractPoolsInstance().getPool(getName());
    }
    catch (DbConException e) {
      if (getLog().isErrorEnabled()) {
        getLog().error("Could not get pool for " + getName(), e);
      }
      throw new SQLException("Detected DbCon exception whilst "
          + "trying to retriving underlying pool for " + getName());
    }
  }

  /**
   * Indicates the synonym used to connect to the specified pool
   */
  public String getName() {
    return name;
  }

  /**
   * Tests if the submitted DataSource is a PooledDataSource and if it is backed
   * by the same Pool. This is enough to test equality as we hold only one
   * instance underlying the pools here so two hashcodes from the same synonym
   * should be equal.
   */
    @Override
  public boolean equals(Object obj) {
    boolean equality = false;

    if (this.getClass().isInstance(obj)) {
      PooledDataSource pds = (PooledDataSource) obj;
      try {
        equality = pds.getUnderlyingPool().equals(this.getUnderlyingPool());
      }
      catch (SQLException e) {
        if (getLog().isErrorEnabled())
          getLog().error("Could not compare pools together. Equality is false",
              e);
      }
    }

    return equality;
  }

  /**
   * This is taken as being the underlying Pool's hashcode. This is enough to
   * get the hashcode as we hold only one instance underlying the pools here so
   * two hashcodes from the same synonym should be equal.
   */
    @Override
  public int hashCode() {

    int hashCodeReturn = 0;

    try {
      hashCodeReturn = this.getUnderlyingPool().hashCode();
    }
    catch (SQLException e) {
      if (getLog().isErrorEnabled())
        getLog()
            .error(
                "Could not retrieve underlying pool for hashcode. Hashcode is 0",
                e);
    }

    return hashCodeReturn;
  }
  
  /**
   * {@inheritDoc }
   * @return false always.
   */
  public boolean isWrapperFor(Class<?> iface) throws SQLException{
      return false;
  }
  
  /**
   * {@inheritDoc }
   * @throws {@link java.sql.SQLException} always.
   */
  public <T> T unwrap(Class<T> iface) throws SQLException{
      throw new SQLException("This is not a wrapper.");
  }

}
