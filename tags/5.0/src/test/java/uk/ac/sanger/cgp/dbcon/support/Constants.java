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

package uk.ac.sanger.cgp.dbcon.support;

/**
 *
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.7 $
 */
public class Constants {
	
	/** Creates a new instance of TestConstants */
	private Constants() {
		super();
	}
	
	//All obsfucator test constants
	public static final String OBSFUCATOR_TEST_INPUT = "hellomum";
	public static final String OBSFUCATOR_TEST_OUTPUT = ".nkrrus{s";
	public static final String[] OBSFUCATOR_TEST_OUTPUT_ALL = 
		new String[]{
			")ifmmpnvn",
			"*jgnnqowo",
			"+khoorpxp",
			",lippsqyq",
			"-mjqqtrzr",
			".nkrrus{s",
			"/olssvt|t",
			"0pmttwu}u",
			"1qnuuxv~v",
			"2rovvyw!)w",
			"3spwwzx!*x",
			"4tqxx{y!+y",
			"5uryy|z!,z",
			"6vszz}{!-{"};
	
	public static final String XML_LIB_RESOURCE = "/testlibrary.xml";
	public static final String SQL_LIB_RESOURCE = "/testlibrary.sqllib";
	public static final String STANDARD_QUERY_TEST_NAME = "test";
	public static final String STANDARD_QUERY_TEST_OUTPUT = "select sysdate from dual ";
	
	public static final String DB_SYNONYM = "testDb";
  public static final String DB_URL = "jdbc:hsqldb:mem:test";
}
