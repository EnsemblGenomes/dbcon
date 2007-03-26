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

package uk.ac.sanger.cgp.dbcon.util;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

/**
 * Aset of tests for the config location tools
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public class SettingsTest extends TestCase {

  private String systemPropertyContent = null;
  
  protected void setUp() throws Exception {
    super.setUp();
    systemPropertyContent = System.getProperty(Settings.SYSTEM_PROPERTY, StringUtils.EMPTY);
    Settings.clearConfigLocations();
  }
  
  protected void tearDown() throws Exception {
    super.tearDown();
    System.setProperty(Settings.SYSTEM_PROPERTY, systemPropertyContent);
    Settings.clearConfigLocations();
    Settings.initaliseDefaults();
  }
  
  public void testGetConfigLocations() {
    System.setProperty(Settings.SYSTEM_PROPERTY, "/x.xml,/b,/c.xml");
    Settings.clearConfigLocations();
    Settings.initaliseDefaults();
    
    assertConfigContainsLocation("/dbcon.xml");
    assertConfigContainsLocation("/x.xml");
    assertConfigContainsLocation("http://a.webserver:8080/conf/dbcon.xml");
  }
  
  private void assertConfigContainsLocation(String location) {
    List configLocations = Settings.getConfigLocations();
    boolean found = configLocations.contains(location);
    assertTrue("Did not find "+location+" in config locations", found);
  }
  
}
