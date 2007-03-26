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

package uk.ac.sanger.cgp.dbcon.testcases;

import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.sqllibs.SqlLib;
import uk.ac.sanger.cgp.dbcon.sqllibs.SqlLibraries;

/**
 * Holds the tests which are common for both XML, SqlLib and anyother
 * library formats which may be created
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class LibraryAbstractTestCase extends TestCase {
	
	private static final String BASIC_SQL_TEST = "select dbcon from other ";
	private String resource = null;
	private SqlLib lib = null;
	
	/** Creates a new instance of LibraryTestCase */
	public LibraryAbstractTestCase(String testName, String resource) {
		super(testName);
		this.resource = resource;
	}
	
	public String getResource() {
		return resource;
	}

	public SqlLib getLib() {
		return lib;
	}
	
	protected void setUp() throws Exception {
		SqlLibraries.reloadLibrary();
		lib = SqlLibraries.getInstance().getLib(getResource());
	}
	
	public void testCommentsDash() {
		assertEquals("Dash comment was not taken out", 
			BASIC_SQL_TEST, getLib().getQuery("comments.dash"));
	}
	
	public void testCommentsStarComment() {
		assertEquals("Star comment was not taken out", 
			BASIC_SQL_TEST, getLib().getQuery("comments.starComment"));
	}
	
	public void testCommentsRuleComment() {
		assertEquals("Star comment was not taken out", 
			"select /*+ rule*/ dbcon from other ", getLib().getQuery("comments.ruleComment"));
	}
  
  public void testPlaceholder() {
    String query = getLib().getQuery("placeholder");
    assertEquals("Placeholder was not present", "select {0} from dual ", query);
    
    String subsQuery = getLib().getQuery("placeholder", new Object[]{new Integer(99999)});
    assertEquals("Placeholder was not present", "select 99999 from dual ", subsQuery);
  }
  
  public void testAposStripping() {
  	String query = getLib().getQuery("aposStripping", new Object[0]);
  	assertEquals("Query was not as expected. ' were stripped", "select 'Y' from dual ", query);
  }
  
  public void testAposStrippingAndPlaceholder() {
  	String query = getLib().getQuery("aposStrippingAndPlaceholder", new Object[]{"1"});
  	assertEquals("Query was not templated as expected", "select 'Y', 1 from dual ", query);
  }
}
