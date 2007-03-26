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

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.support.DatabaseSupport;
import uk.ac.sanger.cgp.dbcon.support.Constants;
import uk.ac.sanger.cgp.dbcon.support.ThreadHelpers;

/**
 * Contains tests which refer back to the Pools object
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class PoolsTest extends TestCase {

	private static final Log LOG = LogFactory.getLog(PoolsTest.class);

	/** Creates a new instance of PoolsTest */
	public PoolsTest(String name) {
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

	private static final int THREADS = 30;

	public void testPoolsConcurrency() {
		List threads = ThreadHelpers.createAndStartThreads(PoolsConcurrencyThread.class, THREADS);
		ThreadHelpers.waitForAllThreadsToFinish(threads);

		//Now look for the errors to fail on
		for(int i=0; i<threads.size(); i++) {
			PoolsConcurrencyThread thread = (PoolsConcurrencyThread)threads.get(i);
			if(!thread.isTestWorked()) {
				if(LOG.isFatalEnabled()) {
					LOG.fatal("Failure was about to be called", thread.getE());
				}
				fail("Failure detected: "+thread.getError());
			}
		}
	}

	/**
	 * Also covers the method {@link Pools#getDbNamesIterator()}
	 */
	public void testListDbNames() {
		String actual = getFirstStringFromGetLoadedDbNames();
		assertNull("We have not requested any DB pools yet so Pools should have" +
				"nothing in it yet", actual);

		//Now create the pool
		Pools.getInstance().getPool(Constants.DB_SYNONYM);
		String expected = Constants.DB_SYNONYM;
		actual = getFirstStringFromGetLoadedDbNames();
		assertEquals(expected, actual);
	}

	/**
	 * Checks that the default parser detected is actually searching through
	 * all potential names
	 */
	public void testGetAvailableDbNames() {
		Iterator iter = Pools.getInstance().getAvailableDbNames().iterator();
		boolean synonymAvailable = false;
		String searchSynonym = "anotherTestDb";
		while(iter.hasNext()) {
			String nextSynonym = getNextStringFromIterator(iter);
			synonymAvailable = searchSynonym.equals(nextSynonym);
			if(synonymAvailable) break;
		}
		assertTrue("Expected to find synonym "+searchSynonym+" but could " +
				"not", synonymAvailable);
	}

	/**
	 * Wrapper around {@link #getNextStringFromIterator(Iterator)} which works
	 * on {@link Pools#getLoadedDbNames()}
	 *
	 * @return The first String it encounters from {@link Pools#getLoadedDbNames()}
	 */
	private String getFirstStringFromGetLoadedDbNames() {
		return getNextStringFromIterator(Pools.getInstance().getLoadedDbNames().iterator());
	}

	/**
	 * Just calls next if hasNext returns true and casts the object to a String
	 *
	 * @param iter The iterator to call next on
	 * @return The next String object in this Iterator
	 */
	private String getNextStringFromIterator(Iterator iter) {
		String output = null;
		if(iter.hasNext()) {
			output = (String)iter.next();
		}
		return output;
	}

}
