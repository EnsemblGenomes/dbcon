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

import java.io.InputStream;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;

/**
 * Classpath based input stream. The resource location must be specified with
 * the leading /
 * 
 * @author ayates
 */
public class ClasspathResource extends AbstractResource {

  public ClasspathResource(String location) {
    super(location);
  }

  protected InputStream getActualInputStream() {
    InputStream is = ClasspathResource.class.getResourceAsStream(getLocation());
    if(is == null) {
      throw new DbConException("Could not find resource "+getLocation());
    }
    return is;
  }
}
