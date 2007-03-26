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

import java.util.ConcurrentModificationException;

import uk.ac.sanger.cgp.dbcon.support.AbstractThread;
import uk.ac.sanger.cgp.dbcon.support.Constants;

/**
 * Contains the tests which will attempt to address concurrent modification
 * issues which can happen
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.3 $
 */
public class SqlLibThread extends AbstractThread {

	private String error = "";
	private boolean testWorked = false;
	private Exception e = null;

	public SqlLibThread(String id) {
		super(id);
	}

	private static final int RUNS = 20;

	/**
	 * Main run method for the thread
	 */
	public void runCode() {
		String currentTest = "Unknown";
		String libTest = "getLib("+Constants.XML_LIB_RESOURCE+")";

		try {
			for (int i = 0; i<RUNS; i++) {
				currentTest = libTest;
				SqlLibraries.getInstance().getLib(Constants.XML_LIB_RESOURCE);

				currentTest = "avaialbleLibs()";
				SqlLibraries.getInstance().avaialbleLibs();

				currentTest = libTest;
				SqlLibraries.getInstance().getLib(Constants.XML_LIB_RESOURCE);

				currentTest = "reloadLibrary()";
				SqlLibraries.reloadLibrary();

				currentTest = libTest;
				SqlLibraries.getInstance().getLib(Constants.XML_LIB_RESOURCE);

				currentTest = "reloadLibrary(" + Constants.XML_LIB_RESOURCE + ")";
				SqlLibraries.getInstance().reloadLibrary(Constants.XML_LIB_RESOURCE);

				currentTest = libTest;
				SqlLibraries.getInstance().getLib(Constants.XML_LIB_RESOURCE);
			}

			testWorked = true;
		}
		catch (ConcurrentModificationException e) {
			this.e = e;
			error = "Detected a concurrent modification exception whilst running "+
				currentTest+" in thread "+this.getThreadId()+". Failing now: "+e.getMessage();
		}
	}

	protected void setError(String error) {
		this.error = error;
	}

	public String getError() {
		return this.error;
	}

	protected void setTestWorked(boolean testWorked) {
		this.testWorked = testWorked;
	}

	public boolean isTestWorked() {
		return this.testWorked;
	}

	public Exception getE() {
		return this.e;
	}
}