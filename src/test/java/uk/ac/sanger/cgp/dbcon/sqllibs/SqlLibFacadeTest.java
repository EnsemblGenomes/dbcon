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

package uk.ac.sanger.cgp.dbcon.sqllibs;

import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * Runs tests which ensure the correct operation of the SqlLib facade object
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.1 $
 */
public class SqlLibFacadeTest extends TestCase {
	
	private static final String LIB = Constants.SQL_LIB_RESOURCE;
	
	public SqlLibFacadeTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		SqlLibraries.reloadLibrary();
	}
	
	/**
	 * This method attempts to retrieve the SqlLibrary from the SqlLibraries object
	 * and from SqlLibFacade and tests the backing object and queries for
	 * equality.
	 */
	public void testLibraryEquality() {
		//First get the two libraries out
		SqlLib sqlLibraries = SqlLibraries.getInstance().getLib(LIB);
		SqlLibFacade sqlFacade = new SqlLibFacade(LIB);
		
		assertEquals("SqlLibFacade does not use the same backing structure as SqlLibraries", 
			sqlLibraries, sqlFacade.loadLibarayFromSqlLibraries());
		
		assertEquals("Queries do not parse to the same contents", 
			sqlLibraries.getQuery(Constants.STANDARD_QUERY_TEST_NAME), 
			sqlFacade.getQuery(Constants.STANDARD_QUERY_TEST_NAME));
		
		assertEquals("Query "+Constants.STANDARD_QUERY_TEST_NAME+" does not " +
			"parse to "+Constants.STANDARD_QUERY_TEST_OUTPUT, 
			Constants.STANDARD_QUERY_TEST_OUTPUT,
			sqlFacade.getQuery(Constants.STANDARD_QUERY_TEST_NAME));
	}
}
