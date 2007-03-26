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

/**
 * Used to hold the location of a SQL library. Useful for encapsulating this
 * concept rather than relying on Strings alone. This can be used in conjunction
 * with enums to create an immutable registry of library locations specific to
 * an application.
 * 
 * <p>
 * There is also the option of using a proxy backed instance of this interface
 * to provide a more runtime based library policy over the static version. e.g.
 * A proxy could be created which when initalised from an instance of {@link Class}
 * it would introspect the class and return a library of the same name (with
 * a .sqllib extension). This is only an idea however.
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public interface SqlLibLocation {

  String getLocation();
  
}
