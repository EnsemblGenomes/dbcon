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

package uk.ac.sanger.cgp.dbcon.util.resources;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;

/**
 * Builder class which for a given String location will correctly pass it
 * onto the correct resource handler. The resource which must be one of the 
 * following:
 * 
 * <ul>
 * <li>file:// : (for files)</li>
 * <li>http:// : (http)</li>
 * <li>https:// : (https)</li>
 * <li>/ : (classpath)</li>
 * </ul>
 * 
 * @author ayates
 * @author $Author$
 * @version $Revision$
 */
public class ResourceBuilder {

  public static Resource getResource(String location) {
    Resource resource = null;
    
    if(location.startsWith("file")) {
      resource = new FileResource(location);
    }
    else if(location.startsWith("http")){
      resource = new UrlResource(location);
    }
    else if(location.startsWith("https")) {
      resource = new UrlResource(location);
    }
    else if(location.startsWith("/")) {
      resource = new ClasspathResource(location);
    }
    else {
      throw new DbConException("Unknown resource protocol for "+location);
    }
    return resource;
  }
  
}
