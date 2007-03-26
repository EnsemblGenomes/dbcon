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
import uk.ac.sanger.cgp.dbcon.exceptions.SqlLibraryRuntimeException;

/**
 * Class which holds the SQL Statments in a Map and the corresponding utility
 * methods
 * 
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class SqlLibImpl implements SqlLib {

  /**
   * 
   */
  private static final long serialVersionUID = 1057992424234994403L;

  private Map statements = null;

  private String libraryName = null;

  /**
   * Creates a new instance of SqlLibrary
   */
  public SqlLibImpl() {
    super();
  }

  public SqlLibImpl(Map statements) {
    this();
    this.statements = statements;
  }

  /**
   * Gets the current value of statements
   * 
   * @return Current value of statements
   */
  public Map getStatements() {
    return statements;
  }

  /**
   * Sets the value of statements
   * 
   * @param statements
   *          New value for statements
   */
  public void setStatements(Map statements) {
    this.statements = statements;
  }

  /**
   * Gets the current value of libraryName
   * 
   * @return Current value of libraryName
   */
  public String getLibraryName() {
    return libraryName;
  }

  /**
   * Sets the value of libraryName
   * 
   * @param libraryName
   *          New value for libraryName
   */
  public void setLibraryName(String libraryName) {
    this.libraryName = libraryName;
  }

  /**
   * Returns the String query for the specified name
   * 
   * @return The String query.
   */
  public String getQuery(String queryName) {
    Statement statement = getStatement(queryName);
    return statement.getStatement();
  }
  
  /**
   * {@inheritDoc}
   */
  public String getQuery(String queryName, Object[] args) {
    Statement statement = getStatement(queryName);
    return statement.getStatement(args);
  }

  /**
   * Returns the Statement object for the specified name.
   * 
   * @return The statement object
   * @throws SqlLibraryRuntimeException Thrown if the statement cannot be found
   */
  public Statement getStatement(String queryName) {
    Statement statement = (Statement) statements.get(queryName);
    if (statement == null) {
      throw new SqlLibraryRuntimeException(
          "Cannot find specified statement for key " + queryName);
    }
    return statement;
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
    return getQuery(key);
  }

  /**
   * Returns a String[] array of the names of all available queries in this lib
   */
  public String[] getAvailableQueries() {
    return (String[]) statements.keySet().toArray(new String[0]);
  }

  /**
   * Overides the toString() object method
   */
  public String toString() {

    StringBuffer sb = new StringBuffer();

    sb.append(this.getClass().getName());
    sb.append(":\n");

    if (statements == null) {
      sb.append("statements: null\n");
    }
    else {
      sb.append("statements: ");
      sb.append(statements);
      sb.append("\n");
    }

    return sb.toString();
  }

}
