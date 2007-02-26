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

package uk.ac.sanger.cgp.dbcon.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;

/**
 * Set of utilities created to be used with the pool and singleton classes
 *
 * @author Andrew Yates
 * @author $Author: aj5 $
 * @version $Revision: 1.11 $
 */
public class DatabaseUtils {

	private static final Log LOG = LogFactory.getLog(DatabaseUtils.class);

  /**
   * A set of methods to run on a Connection when it is retrieved from a Pool.
   * Turns off autocommit, and rolls back the connection in order to 'clean' 
   * it and end the transaction.
   */
  public static void connectionSettings(Connection conn) {
    try {
      conn.setAutoCommit(false);
      conn.rollback();
    } 
    catch (SQLException e) {
      throw new DbConException("Error whilst setting autoCommit off and rollbacking the connection", e);
    }
  }
  
  /**
   * Provides the normal closing method with wrapping SQLException catches
   * with the ability to log any problems out to this class' logger. It also
   * performs a rollback on the given connection. Once close is called there
   * should be no need for any other transactional operations to be performed
   * on a connection.
   */
  public static void closeDbObject(Connection conn) {
    if(conn != null) {
      try {
        conn.rollback();
      }
      catch(SQLException e) {
        LOG.warn("Could not rollback java.sql.Connection", e);
      }
      finally{
        try {
          conn.close();
        }
        catch (SQLException e) {
          LOG.warn("Could not close java.sql.Connection", e);
        }
      }
    }
  }
  
  /**
   * Provides the normal closing method with wrapping SQLException catches
   * with the ability to log any problems out to this class' logger.
   */
  public static void closeDbObject(Statement st) {
    if(st != null) {
      try {
        st.close();
      }
      catch (SQLException e) {
        LOG.warn("Could not close java.sql.Statement", e);
      }
    }
  }
  
  /**
   * Provides the normal closing method with wrapping SQLException catches
   * with the ability to log any problems out to this class' logger
   */
  public static void closeDbObject(ResultSet rs) {
    if(rs != null) {
      try {
        rs.close();
      }
      catch (SQLException e) {
        LOG.warn("Could not close java.sql.ResultSet", e);
      }
    }
  }
  
	/**
	 * Returns true if the driver used to create this connection is an Oracle
	 * driver.
	 */
	public static boolean isOracle(Connection conn) throws SQLException {
		return conn.getMetaData().getDriverName().matches("^Oracle.+");
	}

	/**
	 * Determines if this JDBC driver needs V8 compatibility setting.
         * @return true If:
         * the driver is Oracle major version 10.
         * @throws DbConException If it is not possible to determine whether the fix is required,
         *                        i.e. if the driver version is > 10.
	 */
	public static boolean isFixRequiredForOracleVersion(Connection conn) throws SQLException {
                int majorVersion = conn.getMetaData().getDriverMajorVersion();
                if (majorVersion > 10) throw new DbConException("Unknown Oracle driver major version: cannot determine whether to apply V8 compatibility");
		return (majorVersion == 10);
	}

	/**
	 * This method amalgamates the other boolean methods in this class to see
	 * if it should implement the system property oracle.jdbc.V8Compatible for
	 * JDBC Oracle drivers.
         * @throws DbConException If it is not possible to determine whether the fix is required,
         *                        i.e. if the driver version is > 10.
	 */
	public static boolean setOraclePropertyForBugFix(Connection conn) throws SQLException {
		
                // See if compatibility hasn't already been set, and that it's oracle first.
                if (
                    !Boolean.getBoolean("oracle.jdbc.V8Compatible") &&
                    isOracle(conn) &&
                    isFixRequiredForOracleVersion(conn)
                    ) {
                    System.setProperty("oracle.jdbc.V8Compatible","true");
                    return true;
                }

		return false;
	}

	/**
	 * Checks for a config object for valid URLs and returns back the valid URL
	 * or if there is no backup URL it will return just the one URL and will store
	 * the working URL into the config object
	 */
	public static String getValidConnectionURL(Config config) throws DbConException {

		String validUrl = "";

		if(isUrlValid(config.getUrl(), config.getUsername(), config.getPassword(), config.getDriver())) {
			validUrl = config.getUrl();
		}
		else {
			if(config.getBackupUrl() != null && isUrlValid(config.getBackupUrl(), config.getUsername(), config.getPassword(), config.getDriver())) {
				validUrl = config.getBackupUrl();
			}
			else {
				throw new DbConException("No valid URL could be found for DB "+config.getName());
			}
		}

		config.setWorkingUrl(validUrl);

		return validUrl;
	}

	/**
	 * Attempts to connect to the URL using the given driver and then disconnects.
	 * It does not run any SQL, it just attempts a connection.
	 */
	public static boolean isUrlValid(String url, String username, String password, String driver) {

		boolean okay = false;
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			okay = true;
		}
		catch(ClassNotFoundException e) {
			if(LOG.isDebugEnabled()) LOG.debug("Cannot find class", e);
			okay = false;
		}
		catch(SQLException e) {
			if(LOG.isDebugEnabled()) LOG.debug("SQLException issue", e);
			okay = false;
		}
		finally {
			DatabaseUtils.closeDbObject(conn);
		}

		return okay;
	}

}
