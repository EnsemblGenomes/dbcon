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

package uk.ac.sanger.cgp.dbcon.singletons;

import java.sql.Connection;

import uk.ac.sanger.cgp.dbcon.util.DatabaseUtils;

/**
 * A legacy class from older implementations of dbcon. This particular instance
 * wraps calls to {@link SingletonPools} for retriving connections. It is
 * now recommended that code goes via the DataSource objects
 *
 * @author Andrew Yates
 * @author $Author: aj5 $
 * @version $Revision: 1.13 $
 */
public class Singletons {

	/**
	 * Holds a static instance of the the class Connections as a singleton
	 * now for historical reasons and compatability.
   * @deprecated Removed for above reason
	 */
	private static final Singletons INSTANCE = new Singletons();

	/**
	 * Default constructor
	 */
	protected Singletons() {
		super();
	}

	/**
	 * Returns instance of this singleton class
   * @deprecated Use {@link SingletonPools} for pool information rather than
   * an instance of this dodgy class
	 */
	public static Singletons getInstance() {
		return INSTANCE;
	}

  /**
   * Returns the singleton connection for a given database name specified in the
   * properties file with autoCommit turned off. This is a size one pool which
	 * performs no eviction runs at all.
   *
   * @param dbName The name of the database you want a connection to
   * @return The connection to the specified database
   */
  public static Connection getConnection(String dbName) {
		Connection conn = SingletonPools.getInstance().getPool(dbName).getConnection();
		return conn;
  }
  
  public static void returnConnection(Connection conn) {
    DatabaseUtils.closeDbObject(conn);
  }
}
