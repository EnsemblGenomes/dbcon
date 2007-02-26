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

import uk.ac.sanger.cgp.dbcon.core.AbstractDbConDataSource;
import uk.ac.sanger.cgp.dbcon.core.AbstractPools;


/**
 * An implementation of the abstract dbcon datasource programmed specifically
 * to interact with the {@link Pools} object.
 *
 * @author Andy Yates
 * @author $Author: aj5 $
 * @version $Revision: 1.6 $
 * @see <a href="http://jakarta.apache.org/commons/dbcp/apidocs/org/apache/commons/dbcp/package-summary.html">DBCP DataSources</a>
 */
public class PooledDataSource extends AbstractDbConDataSource {

  public PooledDataSource(String name) {
    super(name);
  }

  protected AbstractPools getBackingAbstractPoolsInstance() {
    return Pools.getInstance();
  }
}
