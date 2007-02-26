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

import uk.ac.sanger.cgp.dbcon.config.Config;
import uk.ac.sanger.cgp.dbcon.core.AbstractPools;

/**
 * This is a subclass of the pooling system used in the main program. The only
 * difference is that it is not linked with the Pools instance in any way once
 * initalised. The SingletonPools should be used during testing only and are configured
 * to <strong>FAIL INSTANTLY</strong> when more than one connection request comes out.
 *
 * @author Andy Yates
 * @author $Author$
 * @version $Revision$
 */
public class SingletonPools extends AbstractPools {
  
  private static final SingletonPools INSTANCE = new SingletonPools(); 
  
  private SingletonPools() {
    super();
  }
  
  public static SingletonPools getInstance() {
    return INSTANCE;
  }
  
  /**
   * Overrriden version of the Pools getConfig method to configure the
   * application for Singleton work. This is defined as
   *
   * <ul>
   * <li>Exhasted - Fail</li>
   * <li>Max Active - 1</li>
   * <li>Max Idle - 1</li>
   * <li>Max Wait - 1</li>
   * <li>Tests per eviction run - 1</li>
   * <li>Test on borrow - false</li>
   * <li>Test on return - false</li>
   * <li>Test while idle - true</li>
   * <li>Time between eviction runs - 600000</li>
   * </ui>
   *
   * All other options are left as deinfed in the dbcon.xml config file
   * used to configure the main pool.
   *
   * @see uk.ac.sanger.cgp.dbcon.pooling.Pools#getConfig(String)
   */
  public Config getConfig(String name) {
    Config config = super.getConfig(name);

    //Now modifying for Singleton work
    config.setExhausted(Config.FAIL);
    config.setMaxActive(1);
    config.setMaxIdle(1);
    config.setMaxWait(1);
    config.setNumTestsPerEvictionRun(1);
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);
    config.setTestWhileIdle(true);
    config.setTimeBetweenEvictRun(600000L);

    return config;
  }
}
