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

import java.util.Map;

/**
 * An implementation of the SqlLib format which relies on using the SqlLibraries
 * object and wraps all access to this through facade.
 * 
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.5 $
 * @since 4.0.2
 */
public class SqlLibFacade implements SqlLib {

  /**
   * 
   */
  private static final long serialVersionUID = 6226199794718350381L;
  
  private final String resource;

  /** Creates a new instance of SpringSqlLib */
  public SqlLibFacade(String resource) {
    super();
    this.resource = resource;
  }
  
  public SqlLibFacade(SqlLibLocation location) {
    super();
    this.resource = location.getLocation();
  }

  /**
   * Returns the Statement object for the specified name.
   * 
   * @return The statement object. Returns null if none can be found
   */
  public Statement getStatement(String queryName) {
    return loadLibarayFromSqlLibraries().getStatement(queryName);
  }

  /**
   * Wrapper for the method <CODE>getQuery</CODE> used only for backwards
   * compatability with saf's original SQLLibraryHolder classes.
   * 
   * @param key
   *          The key representation of a SQL query held in this library
   * @return The SQL statement as represented in the SQL file used to populate
   *         the library
   * @deprecated As of dbcon v.2.0. See {@link #getQuery(String)}
   */
  public String getSQLStatement(String key) {
    return loadLibarayFromSqlLibraries().getSQLStatement(key);
  }

  /**
   * Returns the String query for the specified name
   * 
   * @return The String query.
   */
  public String getQuery(String queryName) {
    return loadLibarayFromSqlLibraries().getQuery(queryName);
  }
  
  /**
   * {@inheritDoc}
   */
  public String getQuery(String queryName, Object[] args) {
    return loadLibarayFromSqlLibraries().getQuery(queryName, args);
  }

  /**
   * Gets the current value of statements
   * 
   * @return Current value of statements
   */
  public Map getStatements() {
    return loadLibarayFromSqlLibraries().getStatements();
  }

  /**
   * Gets the current value of libraryName
   * 
   * @return Current value of libraryName
   */
  public String getLibraryName() {
    return loadLibarayFromSqlLibraries().getLibraryName();
  }

  /**
   * Returns a String[] array of the names of all available queries in this lib
   */
  public String[] getAvailableQueries() {
    return loadLibarayFromSqlLibraries().getAvailableQueries();
  }

  /**
   * This method is a thin wrapper to the SqlLibraries object and runs the
   * getLib(String) method.
   */
  protected SqlLib loadLibarayFromSqlLibraries() {
    return SqlLibraries.getInstance().getLib(resource);
  }
}
