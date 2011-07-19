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
import uk.ac.sanger.cgp.dbcon.util.BasicTemplater;

/**
 * Tests used to test the functionality of BasicTemplater
 *
 * @author Andrew Yates
 * @author $Author$
 * @version $Revision$
 */
public class BasicTemplaterTest extends TestCase {
	

	public BasicTemplaterTest(String testName) {
		super(testName);
	}

	public void testFourParamTemplate() {
	  String template = "{0} {1} {2} {3}";
	  Object[] args = new Object[]{"hello", "there", "world", "!"};
    String expected = "hello there world !";
    String actual = BasicTemplater.template(template, args);
    assertEquals("Checking 4 param templates work", expected, actual);
	}
	
	public void testAddParamTemplate() {
	  String template = "{0} {1} {2} {3}";
	  Object[] args = new Object[]{"hello", "there"};
    String expected = "hello there world !";
    BasicTemplater t = new BasicTemplater(template);
    t.addParams(args);
    t.addParams(new Object[]{"world", "!"});
    assertEquals("Checking 4 param templates work when adding in two batches", expected, t.generate());
	}
}
