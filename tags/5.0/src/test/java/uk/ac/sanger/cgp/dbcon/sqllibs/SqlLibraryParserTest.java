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

package uk.ac.sanger.cgp.dbcon.sqllibs;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.sanger.cgp.dbcon.exceptions.SqlLibraryRuntimeException;
import uk.ac.sanger.cgp.dbcon.support.Constants;
import uk.ac.sanger.cgp.dbcon.testcases.LibraryAbstractTestCase;

/**
 * Runs sqllib tests
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.7 $
 */
public class SqlLibraryParserTest extends LibraryAbstractTestCase {

	public SqlLibraryParserTest(String testName) {
		super(testName, Constants.SQL_LIB_RESOURCE);
	}

	public void testLowerCase() {
		getLib().toString();
		assertEquals("Lower case setting was not parsed correctly",
			"allLowerCase ", getLib().getQuery("allLowerCase"));
	}

	public void testUpperCase() {
		assertEquals("Upper case setting was not parsed correctly",
			"allUpperCase ", getLib().getQuery("allUpperCase"));
	}

	public void testMixedCase() {
		assertEquals("Mixed case setting was not parsed correctly",
			"mixedCase ", getLib().getQuery("mixedCase"));
	}

	public void testCommentsSlash() {
		try {
			getLib().getQuery("comments.slash");
			fail("Commented out query was not taken out");
		}
		catch(SqlLibraryRuntimeException e) {
			//Normal function
		}
	}

	public void testInputStreamInput() {
		String nameToFail = "this will not work";
		try {
			SqlLibraries.getInstance().getLib(nameToFail);
			fail("Exception not thrown. Unexpected behaviour");
		}
		catch(SqlLibraryRuntimeException e) {
			try {
				InputStream is = this.getClass().getResourceAsStream(Constants.SQL_LIB_RESOURCE);
				SqlLib output = SqlLibraries.getInstance().getLib(nameToFail, is);
				is.close();
				assertNotNull("Library was retrieved as being null", output);
			}
			catch(IOException ioe) {
				fail("IO Errors should not happen here "+ioe.getMessage());
			}

		}
	}
}
