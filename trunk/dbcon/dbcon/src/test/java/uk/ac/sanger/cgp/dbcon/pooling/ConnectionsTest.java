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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.exceptions.ConnectionNotAvailableException;
import uk.ac.sanger.cgp.dbcon.support.DatabaseSupport;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * This is the main test class for seeing if the current pooling framework
 * can connect to a database
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class ConnectionsTest extends TestCase {
	
	/** Creates a new instance of ConnectionsTest */
	public ConnectionsTest(String name) {
		super(name);
	}
  
  protected void setUp() throws Exception {
    super.setUp();
    DatabaseSupport.createDatabase();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    DatabaseSupport.destroyDatabase();
  }
	
	public void testConnectionRetrieval() throws Exception {
		Connection conn = null;
		try {
			conn = Connections.getConnection(Constants.DB_SYNONYM);
			assertNotNull("Connection was null", conn);     
		}
		finally {
			try {
				if(conn != null) {
					conn.close();
				}
			}
			catch(SQLException e) {
				fail("Cannot close connection");
			}
		}
	}
	
	private static final int FAILING_CONN_LIMIT = 4;
	
	/**
	 * In this test we attempt to test what the pool currently does in terms
	 * of it's limits with connections.
	 */
	public void testPoolConnectionLimit() {
		List connectionList = new ArrayList();
		try {
			for(int i=0; i<FAILING_CONN_LIMIT; i++) {
				connectionList.add(Connections.getConnection(Constants.DB_SYNONYM));
			}
			fail("We were able to retrieve "+FAILING_CONN_LIMIT+" connections which is too many");
		}
		catch(ConnectionNotAvailableException e) {
			//Well this is right
		}
		finally {
			for(int i=0; i<connectionList.size(); i++) {
				Connections.returnConnection((Connection)connectionList.get(i));
			}
		}
	}
	
	/**
	 * In this test we attempt to grow the pool beyond a limit which we know
	 * is below the actual failing limit. This is to prove that the above test
	 * is working correctly
	 */
	public void testPoolConnectionLimitMockFail() {
		List connectionList = new ArrayList();
		try {
			for(int i=0; i<FAILING_CONN_LIMIT-2; i++) {
				connectionList.add(Connections.getConnection(Constants.DB_SYNONYM));
			}
		}
		catch(ConnectionNotAvailableException e) {
			fail("Pool size was not set to the expected size which was "+(FAILING_CONN_LIMIT-2));
		}
		finally {
			for(int i=0; i<connectionList.size(); i++) {
				Connections.returnConnection((Connection)connectionList.get(i));
			}
		}
	}
}
