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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * Set of cases for testing the correct running of the Obsfucator class. 
 * 
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class ObsfucatorTest extends TestCase {
	
	public ObsfucatorTest(String testName) {
		super(testName);
	}
	
	/**
	 * Attempts known decoding of String
	 */
	public void testDecoding() {
		String decoded = Obsfucator.decode(Constants.OBSFUCATOR_TEST_OUTPUT);
		assertEquals(Constants.OBSFUCATOR_TEST_INPUT, decoded);
	}
	
	/**
	 * The Obsfucator currently does not decode a String directly and therefore means
	 * that it is very hard to test. This method attempts 100 times to get the "right"
	 * encoded String if not the test will fail
	 */
	public void testEncoding() {
		String encoded = Obsfucator.encode(Constants.OBSFUCATOR_TEST_INPUT);
		//Generating this object once since setUp and tearDown is run every time
		//we run a test in this class
		List outputValues = new ArrayList(Arrays.asList(Constants.OBSFUCATOR_TEST_OUTPUT_ALL));
		assertTrue("Obsfucator encoded input String to an unknown output", outputValues.contains(encoded));
	}
	
	/**
	 * Attempts a decode & recode
	 */
	public void testEncodingAndDecoding() {
		String encoded = Obsfucator.encode(Constants.OBSFUCATOR_TEST_INPUT);
		assertEquals(Constants.OBSFUCATOR_TEST_INPUT, Obsfucator.decode(encoded));
	}

	
	
}
