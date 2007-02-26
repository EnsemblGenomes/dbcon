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
import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.exceptions.ConnectionNotAvailableException;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * This attempts to test the correct usage of the Singletons and if the
 * class is working as advertised (i.e. as a Singleton connection pool with
 * a size of 1).
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.3 $
 */
public class SingletonsTest extends TestCase {

	boolean setupAlreadyRun = false;

	public void setUp() {
		if(!setupAlreadyRun) {
			setUpOnce();
		}
	}

	/**
	 * Some bits of code I just want to run once
	 */
	protected void setUpOnce() {

		Connection conn = null;

		try {
			conn = Singletons.getConnection(Constants.DB_SYNONYM);
			setupAlreadyRun = true;
		}
		catch(Exception e) {
			//Just want to initalise the pool
		}
		finally {
			Singletons.returnConnection(conn);
		}
	}

	/** Creates a new instance of SingletonsTest */
	public SingletonsTest(String name) {
		super(name);
	}

	/**
	 * Takes the configuration for the test database pool and returns the
	 * configuration as what will be used in the Singleton.
	 */
	public void testSingletonConfiguration() {
		Config config = SingletonPools.getInstance().getConfig(Constants.DB_SYNONYM);

		//Consistent pool configuration
		assertEquals("URL was not the expected URL", Constants.DB_URL, config.getUrl());
		assertEquals("Username was not the expected value", "sa", config.getUsername());
		assertEquals("Password was not the expected value", "", config.getPassword());
		assertEquals("Backup URL was not the expected URL of size 0", "", config.getBackupUrl());
		assertEquals("Driver was not the expected driver", "org.hsqldb.jdbcDriver", config.getDriver());
		assertEquals("Name was not the expected name testDb", "testDb", config.getName());
		assertEquals("Query was not the expected validation query", "select * from person", config.getValidationQuery());
		assertEquals("The number of cached prepared statements allowed was not 1", 5, config.getCachedPreparedStatements());
		assertEquals("The minimum evicition time was 1000", 1000, config.getMinEvictTime());

		//Now for the singleton configured ones
		assertEquals("Pool exhasted action was not set to fail", Config.FAIL, config.getExhausted());
		assertEquals("The maximum active connections was not 1", 1, config.getMaxActive());
		assertEquals("The maximum idle connections was not 1", 1, config.getMaxIdle());
		assertEquals("The maximum wait time was not 1", 1, config.getMaxWait());
		assertEquals("Number of tests per eviction run was not 1", 1, config.getNumTestsPerEvictionRun());
		assertEquals("The time between evicition runs was not set to 10 minutes (600000 ms)", 600000L, config.getTimeBetweenEvictRun());

		//And the boolean ones
		assertFalse("Pool was attempting to test the connection on borrow", config.isTestOnBorrow());
		assertFalse("Pool was attempting to test the connection on return", config.isTestOnReturn());
		assertTrue("Pool was not testing the connection when it was idle", config.isTestWhileIdle());
	}

	/**
	 * This attempts to test how many connections we can hold out to a database
	 */
	public void testSingletonPoolAction() {

		Connection conn1 = null;
		Connection conn2 = null;

		try {
			conn1 = Singletons.getConnection(Constants.DB_SYNONYM);
			conn2 = Singletons.getConnection(Constants.DB_SYNONYM);
			fail("Singleton pool returned more than one connection");
		}
		catch(ConnectionNotAvailableException e) {
			//If this is here then we did succeed in getting out the right number of
			 //connections
		}
		finally {
			Singletons.returnConnection(conn1);
			Singletons.returnConnection(conn2);
		}
	}
}
