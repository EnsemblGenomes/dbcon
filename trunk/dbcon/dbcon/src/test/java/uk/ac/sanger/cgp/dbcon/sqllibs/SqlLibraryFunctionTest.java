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

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.support.ThreadHelpers;

/**
 * This is a set of tests which attempts to test the expected behaviour
 * of the SqlLibs framework. It is not implementation specific. This is also
 * dependant on the ability of the underlying map to fail fast when it comes
 * to concurrent modifications. If it does not fail fast then the
 * modification exception will not be thrown
 *
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class SqlLibraryFunctionTest extends TestCase {
	
	private static final Log LOG = LogFactory.getLog(SqlLibraryFunctionTest.class);

	public SqlLibraryFunctionTest(String testName) {
		super(testName);
	}

	private static final int THREADS = 20;

	/**
	 * This test produces multiple threads and runs the test code. The number of
	 * threads produced is dependant on the THREADS variable as defined in this
	 * test
	 */
	public void testSqlLibConcurrentAccess() {

		List threads = ThreadHelpers.createAndStartThreads(SqlLibThread.class, THREADS);
		ThreadHelpers.waitForAllThreadsToFinish(threads);

		//Now look for the errors to fail on
		for(int i=0; i<threads.size(); i++) {
			SqlLibThread thread = (SqlLibThread)threads.get(i);
			if(!thread.isTestWorked()) {
				if(LOG.isFatalEnabled()) {
					LOG.fatal("Failure was about to be called", thread.getE());
				}
				fail("Failure detected: "+thread.getError());
			}
		}
	}
}
