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
 * Custom exception to report when a connection retrival has timed out
 * 
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class ConnectionNotAvailableException extends GenericPoolingException {

  /**
	 * 
	 */
	private static final long serialVersionUID = -4154537387439558937L;

	public ConnectionNotAvailableException (String input) {
    super(input);
  }
  
  public ConnectionNotAvailableException () {
    super();
  }
  
	public ConnectionNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ConnectionNotAvailableException(Throwable cause) {
		super(cause);
	}
}
