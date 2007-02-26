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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.util.InputOutputUtils;

/**
 * Holds the base instance of a resource object which will locate
 * resources from multiple locations and provide them as local stores
 * of the given resource. All input streams which are wrapped in these
 * objects are steramed into a byte[] from which new input streams are
 * generated. This allows for resources to be read multiple times by different
 * parts of code without causing external resources to be swamped.
 * 
 * @author ayates
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractResource implements Resource {
  
  private Log log = LogFactory.getLog(this.getClass());

  private final String location;
  private byte[] output;

  public AbstractResource(String location) {
    super();
    this.location = location;
  }

  public String getLocation() {
    return location;
  }
  
  /**
   * Returns a new input stream from the given resource
   */
  public InputStream getInputStream() {
    return new ByteArrayInputStream(getByteArray());
  }
  
  protected Log getLog() {
    return log;
  }
  
  /**
   * Must be overriden to specify the method of how to return an input
   * stream from the given resource. Also any errors that can occur
   * should be delt with a thrown from here
   */
  protected abstract InputStream getActualInputStream();
  
  /**
   * Calls to {@link #getActualInputStream()} and converts/stores this in
   * a local byte[]. If already streamed then the byte[] is returned
   */
  private byte[] getByteArray() {
    if(output == null) {
      InputStream is = getActualInputStream();
      try {
        output = IOUtils.toByteArray(is);
      }
      catch (IOException e) {
        throw new DbConException("Could not covert resource "
            +getLocation()+" into intermediate byte[]", e);
      }
      finally {
        InputOutputUtils.closeQuietly(is);
      }
      if(output == null) {
        throw new DbConException("The streamed byte[] from resource "+getLocation()+" was null");
      }
    }
    return output;
  }
}