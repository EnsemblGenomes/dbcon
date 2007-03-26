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

import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.singletons.SingletonPools;
import uk.ac.sanger.cgp.dbcon.singletons.Singletons;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * Created to address the problems of race conditions which configuration
 * suffered from under ealier versions of the dbcon pooling code.
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public class RaceConditionTest extends TestCase {

  protected void tearDown() throws Exception {
    Pools.getInstance().destroyAllPools();
    SingletonPools.getInstance().destroyAllPools();
  }
  
  public void testRace() {
    
    try {
      Connections.getConnection(Constants.DB_SYNONYM);
      Singletons.getConnection(Constants.DB_SYNONYM);
    }
    catch(DbConException e) {
      e.printStackTrace();
      fail("Found exception; failing");
    }
    
    Config poolConfig = Pools.getInstance().getConfig(Constants.DB_SYNONYM);
    Config singletonConfig = SingletonPools.getInstance().getConfig(Constants.DB_SYNONYM);
    
    assertTrue("Race condition still applies since " +
        "singleton's config is not correct", poolConfig.getMaxActive() != singletonConfig.getMaxActive());
  }
  
}
