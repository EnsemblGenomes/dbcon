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
import java.util.Iterator;

import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.exceptions.PoolingException;
import uk.ac.sanger.cgp.dbcon.util.DatabaseUtils;


/**
 * <p>
 * The main driver class for pooling in dbcon. The pooling is based upon code
 * developed for dbtools's pooling however this version allows for multiple
 * database types to be used (e.g. Oracle & MySQL) and multiple pools to be
 * maintained.
 * 
 * <code>
 * Connection conn = null;<br/>
 * <br/>
 * try {<br/>
 *   conn = Connections.getConnection("snp");<br/>
 * }<br/>
 * <br/>
 * catch (PoolingException e) {<br/>
 *  NORMAL ERROR HANDLING HERE;<br/>
 * }<br/>
 * <br/>
 * catch (ConnectionNotAvailableException e) {<br/>
 *   NORMAL ERROR HANDLING HERE;<br/>
 * }<br/>
 * <br/>
 * ---- DO YOUR JDBC/SQL QUERY ----<br/>
 * <br/>
 * Connections.returnConnection(conn);<br/>
 * </p>
 *
 * <p>
 * The above code indicates how to have done this previously. Now all exceptions
 * are Runtime and therefore unchecked. The above code can now be expressed as
 * the following.
 * 
 * <code>
 * Connection conn = Connections.getConnection("snp");<br/>
 *
 * try {<br/>
 *   ---- DO YOUR JDBC/SQL QUERY ----<br/>
 * }<br/>
 * finally {<br/>
 *   Connections.returnConnection(conn);<br/>
 * }<br/>
 * </code>
 * 
 * <p>
 * It is also recommended that connection handling is now dealt with using
 * the data source objects since they provide compatable javax.sql standard
 * methods of retriving connections.
 *
 * @author $Author: aj5 $
 * @version $Revision: 1.25 $
 */
public class Connections {

	/**
	 * Holds a static instance of the the class Connections as a singleton
	 * now for historical reasons and compatability.
   * @deprecated Just for the above reason
	 */
	private static final Connections INSTANCE = new Connections();

	/**
	 * Default constructor
	 */
	protected Connections() {
		super();
	}

	/**
	 * Returns instance of this singleton class
   * @deprecated No need to acces the singleton instance as this is handled
   * by the AbstractPools objects
	 */
	public static synchronized Connections getInstance() {
			return INSTANCE;
	}

	// ----- START OF SPECIFIC POOLING CONNECTION GETTING ... Yes my english sucks ----- //

	/**
	 * Returns a connection for a given database name specified in the
	 * pool's properties file with autoCommit turned off.
	 *
	 * @param name The name of the database you want a connection to
	 * @return The connection to the specified database
	 * @throws	PoolingException									Thrown when there is a problem with the pool
	 *																						itself
	 * @throws	ConnectionNotAvailableException 	Thrown when timeout occurs and no connection
	 *																						is available
	 */
	public static Connection getConnection(String name) {

		if(name == null){
			throw new PoolingException("The call to 'getConnection' did not specify a connection - it was passed 'null'");
		}

		//This throws a ConnectionNotAvailableException & pooling exception
    Pool pool = Pools.getInstance().getPool(name);
		Connection newConn = pool.getConnection();

		return newConn;
	}

	/**
	 * Delegates to {@link DatabaseUtils#closeDbObject(Connection)} and here for
   * compatability sake
	 */
	public static void returnConnection(Connection conn) {
		DatabaseUtils.closeDbObject(conn);
	}

	/**
	 * Returns an Iterator of dbnames defined in this pool and is now a wrapper
	 * for the correct method
	 *
	 * @deprecated Replaced by {@link Pools#getLoadedDbNames()}
	 */
	public static Iterator listDbNames() {
		return Pools.getInstance().getLoadedDbNames().iterator();
	}

	/**
	 * Returns a pool's current status and is now a wrapper for the correct method
	 *
	 * @deprecated Replaced by {@link uk.ac.sanger.cgp.dbcon.pooling.Pools#poolStatus(String) poolStatus}
	 */
	public static String poolStatus(String name) {
		return Pools.getInstance().poolStatus(name);
	}

	/**
	 * Returns an iterator containing strings refering to conifgured Databases
	 * and is now a wrapper for the correct method
	 *
	 * @deprecated Replaced by {@link Pools#getAvailableDbNames()}
	 */
	public static Iterator getDbNamesIterator() {
		return Pools.getInstance().getAvailableDbNames().iterator();
	}

	/**
	 * Returns the current number of active connections in the pool. Now use
	 * the Pools based method.
	 *
	 * @deprecated Replaced by {@link uk.ac.sanger.cgp.dbcon.pooling.Pools#getActiveConnections(String) getActiveConnections}
	 */
	public static int getActiveConnections(String name) {
		return Pools.getInstance().getActiveConnections(name);
	}

	/**
	 * Returns the current number of idle connections in the pool. Now use
	 * the Pools based method.
	 *
	 * @deprecated Replaced by {@link uk.ac.sanger.cgp.dbcon.pooling.Pools#getIdleConnections(String) getIdleConnections}
	 */
	public static int getIdleConnections(String name) {
		return Pools.getInstance().getIdleConnections(name);
	}

	/**
	 * Returns the max number of idle connections in the pool. Now use
	 * the Pools based method.
	 *
	 * @deprecated Replaced by {@link uk.ac.sanger.cgp.dbcon.pooling.Pools#getMaxIdleConnections(String) getMaxIdleConnections}
	 */
	public static int getMaxIdleConnections(String name) {
		return Pools.getInstance().getMaxIdleConnections(name);
	}

	/**
	 * Returns the max number of active connections in the pool. Now use
	 * the Pools based method.
	 *
	 * @deprecated Replaced by {@link uk.ac.sanger.cgp.dbcon.pooling.Pools#getMaxActiveConnections(String) getMaxActiveConnections}
	 */
	public static int getMaxActiveConnections(String name) {
		return Pools.getInstance().getMaxActiveConnections(name);
	}

	/**
	 * Destroys the pool in the background and removes the reference from the
	 * poolMap and destroys all old settings
   * 
   * @deprecated Use the specific version as held in {@link Pools} or any other
   * linked pool management code
	 */
	public void destroyAllPools() {
		Pools.getInstance().destroyAllPools();
	}

	/**
	 * This returns the configuration for a given initalised pool
   * @deprecated Use the specific implementation from the correct backing pool
   * such as {@link Pools}
	 */
	public static Config getPoolConfig(String name) {
		return Pools.getInstance().getPool(name).getConfig();
	}
}
