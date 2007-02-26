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
 * DO NOT USE THIS CLASS!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * @author $Author: ady $
 * @version $Revision: 1.6 $
 * @deprecated DO NOT USE THIS CLASS
 */
public class GenericPoolingRuntimeException extends DbConRuntimeException {

  /**
	 * 
	 */
	private static final long serialVersionUID = 382801037101389917L;

	public GenericPoolingRuntimeException (String input) {
    super(input);
  }
  
  public GenericPoolingRuntimeException () {
    super();
  }

	public GenericPoolingRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenericPoolingRuntimeException(Throwable cause) {
		super(cause);
	}
	
}
