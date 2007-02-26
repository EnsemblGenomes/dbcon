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

import java.io.Serializable;
import java.util.Map;

/**
 * A set of methods which define the access control of an SqlLib object
 * 
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.12 $
 */
public interface SqlLib extends Serializable {

  /**
   * Returns a String[] array of the names of all available queries in this lib
   */
  String[] getAvailableQueries();

  /**
   * Gets the current value of libraryName
   * 
   * @return Current value of libraryName
   */
  String getLibraryName();

  /**
   * Returns the String query for the specified name
   * 
   * @return The String query.
   */
  String getQuery(String queryName);
  
  /**
   * Uses the params array to bind {@link java.text.MessageFormat} compatable placeholders
   * to parameters. This is to be used in DDL statements and the alike where
   * it is not possible to specify a variable using a PreparedStatement ?
   * placeholder.
   */
  String getQuery(String queryName, Object[] params);

  /**
   * Wrapper for the method <CODE>getQuery</CODE> used only for backwards
   * compatability with saf's original SQLLibraryHolder classes.
   * 
   * 
   * @param key
   *          The key representation of a SQL query held in this library
   * @return The SQL statement as represented in the SQL file used to populate
   *         the library
   * @deprecated As of dbcon v.2.0. See {@link #getQuery(String)}
   */
  String getSQLStatement(String key);

  /**
   * Returns the Statement object for the specified name.
   * 
   * @return The statement object. Returns null if none can be found
   */
  Statement getStatement(String queryName);

  /**
   * Gets the current value of statements
   * 
   * @return Current value of statements
   */
  Map getStatements();

  /**
   * Overides the toString() object method
   */
  String toString();
}
