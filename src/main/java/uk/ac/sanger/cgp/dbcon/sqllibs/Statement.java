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

import java.text.MessageFormat;

/**
 * Holds SQL Statements but this has been done this way to allow extensions to
 * the library language if needed.
 * 
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.4 $
 */
public class Statement implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3584131763937428771L;

  private String name = null;

  private String statement = null;

  /**
   * Creates a new instance of Statement
   */
  public Statement() {
    super();
  }

  public Statement(String name, String statement) {
    this();
    this.name = name;
    this.statement = statement;
  }

  /**
   * Gets the current value of name
   * 
   * @return Current value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          New value for name
   */
  protected void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the current value of statement
   * 
   * @return Current value of statement
   */
  public String getStatement() {
    return statement;
  }

  /**
   * Sets the value of statement
   * 
   * @param statement
   *          New value for statement
   */
  protected void setStatement(String statement) {
    this.statement = statement;
  }
  
  /**
   * This method assumes that the given statement has placeholders defined
   * by {0} holders. The SQL statement is passed through {@link MessageFormat}
   * and then passed back.
   * <p>
   * <strong>This feature should not be used to pass normal variables
   * into sql statements. This functionality is already covered by
   * the PreparedStatement interface. This should be used for statements
   * where it is not possible to use prepared statements for setting
   * values e.g. table creation scripts and programatically specifying 
   * table names</strong>
   * 
   * @param params Will be converted into a String[] to avoid localisation
   * problems which can be caused by using the {@link MessageFormat} class
   * @return The full sql statement
   */
  public String getStatement(Object[] params) {
    String rawStatement = getStatement();
    String[] stringParams = new String[params.length];
    for(int i=0; i<params.length; i++) {
      stringParams[i] = params[i].toString();
    }
    return MessageFormat.format(rawStatement, stringParams);
  }

  /**
   * Overides the toString() object method
   */
  public String toString() {

    String nl = System.getProperty("line.separator");

    StringBuffer sb = new StringBuffer();

    sb.append(this.getClass().getName());
    sb.append(':');
    sb.append(nl);

    if (name == null) {
      sb.append("name: null");
      sb.append(nl);
    }
    else {
      sb.append("name: ");
      sb.append(name);
      sb.append(nl);
    }

    if (statement == null) {
      sb.append("statement: null");
      sb.append(nl);
    }
    else {
      sb.append("statement: ");
      sb.append(statement);
      sb.append(nl);
    }

    return sb.toString();
  }
}