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

package uk.ac.sanger.cgp.dbcon.exceptions;

/**
 * Runtime exception used to denote when an error has occured whilst returning
 * the connection back to the object pool.
 *
 * @author $Author: ady $
 * @version $Revision: 1.5 $
 */
 
public class ConnectionNotReturnedException extends GenericPoolingException{
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 4191023907024295613L;

	public ConnectionNotReturnedException(String e) {
    super(e);
  }
  
	public ConnectionNotReturnedException() {
		super();
	}

	public ConnectionNotReturnedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConnectionNotReturnedException(Throwable cause) {
		super(cause);
	}
}