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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import uk.ac.sanger.cgp.dbcon.support.NanoHttpd;

/**
 * Collection of unit tests which attempts to test the ReaderUtils' ability to
 * parse from multiple locations such as http (uses google), file (creates a
 * test file) and classpath (dbcon.xml test file). If any of this section fails
 * then the parsing ability of dbcon is compromised severly.
 * 
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.2 $
 */
public class ReaderUtilsTest extends TestCase {

  public ReaderUtilsTest(String testName) {
    super(testName);
  }
  
  private NanoHttpd httpdServer = null;
  
  protected void setUp() throws Exception {
    super.setUp();
    httpdServer = new NanoHttpd(8099,true);
  }
  
  protected void tearDown() throws Exception {
    httpdServer.closeServer();
    super.tearDown();
  }
  
  /**
   * Test parsing ability against the custom version of nano httpd
   */
  public void testReturnReaderFromUrl() {    
    String urlResource = "http://127.0.0.1:8099";
    assertTrue("Cannot read from resource " + urlResource,
        canReadFromResource(urlResource));
  }

  /**
   * Test parsing ability against a temp file created for the purpose of this
   * test
   */
  public void testReturnReaderFromFile() {
    File testFile = null;

    try {
      testFile = File.createTempFile("test", "tmp");
      FileWriter writer = new FileWriter(testFile);
      IOUtils.write("Test", writer);
      IOUtils.closeQuietly(writer);
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    assertTrue("Cannot read from resource " + testFile.toURI().toString(),
        canReadFromResource(testFile.toURI().toString()));
  }

  /**
   * Test parsing ability against /dbcon.xml (the test dbcon settings file in
   * this test pack)
   */
  public void testReturnReaderFromClasspath() {
    String classpathResource = "/dbcon.xml";
    assertTrue("Cannot read from resource " + classpathResource,
        canReadFromResource(classpathResource));
  }

  /**
   * Simplifies the above tests by performing the retrival and test code
   * necessary to make the other tests in this TestCase operate correctly.
   */
  private boolean canReadFromResource(String resource) {
    boolean canRead = false;

    try {
      Reader reader = ReaderUtils.returnReaderFromResource(resource);
      if (reader != null) {
        String readString = IOUtils.toString(reader);
        canRead = (readString != null && !readString.equals(""));
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return canRead;
  }

}
