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

package uk.ac.sanger.cgp.dbcon.config;

import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * The base unit test used to ensure the correct parsing of the test dbcon.xml
 * file used in unit testing.
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class XMLParserAbstractTestCase extends TestCase {

	private final Class configParserClass;
	private ConfigParser parser = null;

	/** Creates a new instance of XMLParserTestCase */
	public XMLParserAbstractTestCase(String name, Class configParserClass) {
		super(name);
		this.configParserClass = configParserClass;
	}

	public void setUp() {
		try {
			parser = (ConfigParser)configParserClass.newInstance();
		}
		catch(InstantiationException e) {
			fail("Could not create parser "+configParserClass.getName());
		}
		catch(IllegalAccessException e) {
			fail("Could not create parser "+configParserClass.getName());
		}
	}
	
	public void testAvailableSynonyms() {

		String[] dbs = parser.getAvailableDBs();
		boolean canFindTestDb = false;

		for(int i=0; i<dbs.length; i++) {
			if(dbs[i].equals(Constants.DB_SYNONYM)) {
				canFindTestDb = true;
				break;
			}
		}
		assertTrue("Extracted synonym was not equal to the expected " +
			"synonym name", canFindTestDb);
	}

	public void testConfigParsing() {
		Config config = parser.getConfig(Constants.DB_SYNONYM);
		assertEquals("Did not find the expect URL as basic test", Constants.DB_URL, config.getUrl());
		assertNotSame("Found a username which should not be set. Test pack is " +
			"corrupt", "", config.getUsername());
		
		//Consistent pool configuration
		assertEquals("Username was not the expected value", "sa", config.getUsername());
		assertEquals("Password was not the expected value", "", config.getPassword());
		assertEquals("Backup URL was not the expected URL of nothing", "", config.getBackupUrl());
		assertEquals("Driver was not the expected driver", "org.hsqldb.jdbcDriver", config.getDriver());
		assertEquals("Name was not the expected name testDb", "testDb", config.getName());
		assertEquals("Query was not the expected validation query", "select * from person", config.getValidationQuery());
		
		//Now for the singleton configured ones
		assertEquals("The number of cached prepared statements allowed was not 1", 5, config.getCachedPreparedStatements());
		assertEquals("Pool exhasted action was not set to fail", Config.FAIL, config.getExhausted());
		assertEquals("The maximum active connections was not 1", 3, config.getMaxActive());
		assertEquals("The maximum idle connections was not 1", 1, config.getMaxIdle());
		assertEquals("The maximum wait time was not 1", 100, config.getMaxWait());
		assertEquals("The minimum evicition time was 1", 1000, config.getMinEvictTime());
		assertEquals("Number of tests per eviction run was not 1", 1, config.getNumTestsPerEvictionRun());
		assertEquals("The time between evicition runs was not set to 1 second (1000 ms)", 1000L, config.getTimeBetweenEvictRun());
	
		//And the boolean ones
		assertFalse("Pool was attempting to test the connection on borrow", config.isTestOnBorrow());
		assertFalse("Pool was attempting to test the connection on return", config.isTestOnReturn());
		assertTrue("Pool was not testing the connection when it was idle", config.isTestWhileIdle());
	}
}
