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

import java.util.ConcurrentModificationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.support.AbstractThread;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * Runs tests over the Pools object to test the concurrency of it
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.2 $
 */
public class PoolsConcurrencyThread extends AbstractThread {
	
	private static final Log LOG = LogFactory.getLog(PoolsConcurrencyThread.class);
	
	private String error = "";
	private boolean testWorked = false;
	private Exception e = null;
	
	private static final int RUNS = 500;
  private static final int DESTROY_EVERY = 10;
	
	/** Creates a new instance of PoolsConcurrencyThread */
	public PoolsConcurrencyThread(String id) {
		super(id);
	}

	public void runCode() {
		
		String currentTest = "Unknown";
		String poolTest = "Pools.getInstance().getPool("+Constants.DB_SYNONYM+")";
		
		try {	
			for (int i = 0; i<RUNS; i++) {
				
				try {
					currentTest = poolTest;
					Pools.getInstance().getPool(Constants.DB_SYNONYM);

					currentTest = "listDbNames()";
          Pools.getInstance().getAvailableDbNames();

          if(i%DESTROY_EVERY == 0) {
  					currentTest = "destroyAllPools()";
  					Pools.getInstance().destroyAllPools();
          }

					currentTest = "getPool("+Constants.DB_SYNONYM+")";
					Pools.getInstance().getPool(Constants.DB_SYNONYM);

          if(i%DESTROY_EVERY == 0) {
  					currentTest = "destroyNamedPool("+Constants.DB_SYNONYM+")";
  					Pools.getInstance().destroyNamedPool(Constants.DB_SYNONYM);
          }
				}
				catch(DbConException e) {
					if(LOG.isInfoEnabled()) {
						LOG.info("Exception was detected but this is not a fatal " +
							"thing for this test as we expect a level of exception " +
							"handling to happen", e);
						continue;
					}
				}
			}
			
			setTestWorked(true);
		} 
		catch (ConcurrentModificationException e) {
			this.e = e;
      e.printStackTrace();
			error = "Detected a concurrent modification exception whilst running "+
				currentTest+" in thread "+this.getThreadId()+". Failing now: "+e;
      System.err.println(error);
		}
	}
	
	protected void setError(String error) {
		this.error = error;
	}

	public String getError() {
		return this.error;
	}

	protected void setTestWorked(boolean testWorked) {
		this.testWorked = testWorked;
	}

	public boolean isTestWorked() {
		return this.testWorked;
	}
	
	public Exception getE() {
		return this.e;
	}
}
