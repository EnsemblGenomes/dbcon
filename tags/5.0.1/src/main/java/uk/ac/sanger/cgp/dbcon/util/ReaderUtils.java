/*
 ##########################################################################
 #                          COPYRIGHT NOTICE                              #
 ##########################################################################
 #                                                                        #
 # Copyright (c) 2006 Genome Research Ltd.                                #
 # Author: The Cancer Genome Project IT group cancerit@sanger.ac.uk       #
 # Author: Andrew Yates andyyatz@gmail.com                                #
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.util.resources.Resource;
import uk.ac.sanger.cgp.dbcon.util.resources.ResourceBuilder;

/**
 * A collection of methods which can be used to help locating and reading
 * multiple resources in dbcon
 * 
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class ReaderUtils {

  private static Log getLog() {
    return LogFactory.getLog(ReaderUtils.class);
  }

  /** Creates a new instance of ReaderUtils */
  private ReaderUtils() {
    super();
  }

  /**
   * For the given resource this method will return a String which is the local
   * representation of the String input from the resource. Now delagates to
   * the {@link ResourceBuilder} object for the input stream
   * 
   * @param resource
   *          The resource which must be one of the following forms:
   *          <ul>
   *          <li>file: (for files)</li>
   *          <li>http:// (http)</li>
   *          <li>/ (classpath)</li>
   *          </ul>
   * @return The reader which underneath is a StringReader object. Will be null
   *         if it could not retrieve one
   */
  public static Reader returnReaderFromResource(final String resource) {

    InputStream mainStream = null;
    Reader output = null;

    Log log = getLog();
    
    try {
      Resource resourceObj = ResourceBuilder.getResource(resource);
      mainStream = resourceObj.getInputStream();
      String streamContent = IOUtils.toString(mainStream);
      output = new StringReader(streamContent);
    }
    catch(DbConException e) {
      if (log.isWarnEnabled()) {
        log.warn("No valid stream could be generated for resource "+ resource, e);
      }
    }
    catch(IOException e) {
      if (log.isWarnEnabled()) {
        log.warn("Error occured whilst converting InputStream to StringReader for resource " + resource, e);
      }
    }
    finally {
      InputOutputUtils.closeQuietly(mainStream);
    }
    
    return output;
  }
}
