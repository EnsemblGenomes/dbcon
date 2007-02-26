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

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import org.apache.commons.io.IOUtils;
import uk.ac.sanger.cgp.dbcon.exceptions.SqlLibraryRuntimeException;

/**
 * Solid implementation of the library parsers class which parses the XML
 * SqlLibrary format as defined by Andrew Yates.
 * 
 * XML prevents the &lt;, &gt;, &amp;, ' and " to be used. However this can 
 * be overcome using CDATA blocks.
 * 
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.5 $
 */
public class XMLLibraryParser extends LibraryParser {

  /**
   * This is a XOM version of the parser and has replaced the old JDOM parser.
   * The library format of this also has changed to the format as shown below
   * where name is an attribute and the contents of the sql element being the
   * statement that will be slurped into the statement objects.
   * 
   * <pre>
   *  &lt;sqllib&gt;
   *    &lt;statement name=&quot;name&quot;&gt;
   *      select * from dual
   *    &lt;/statement&gt;
   *  &lt;/sqllib&gt;
   * </pre>
   */
  public SqlLib parseLibrary(Reader input, String libraryName) {
    SqlLibImpl lib = new SqlLibImpl();
    Map statements = new Hashtable();
    lib.setStatements(statements);

    Builder parser = new Builder();

    try {
      // Done to get around a problem with premature EOF errors
      String localCopy = IOUtils.toString(input);
      // Passing the string in
      Document doc = parser.build(localCopy, "");

      Nodes statementNodes = doc.getRootElement().query("//sql");
      for (int i = 0; i < statementNodes.size(); i++) {
        Statement statement = new Statement();
        Element element = (Element) statementNodes.get(i);
        // Getting the first attribute which must be the name of the SQL
        statement.setName(element.getAttribute(0).getValue());
        String sql = element.getValue().trim();
        // Removing the comments
        sql = multiLineComment(sql);
        sql = runQueryCleanup(sql);
        // Adding the final statement to the statement object
        statement.setStatement(sql);
        // Adding this to the map
        statements.put(statement.getName(), statement);
      }
    }
    catch (IOException e) {
      throw new SqlLibraryRuntimeException(
          "An error occured whilst trying to read the library " + libraryName,
          e);
    }
    catch (ParsingException e) {
      throw new SqlLibraryRuntimeException(
          "An error occured whilst parsing the library file " + libraryName, e);
    }

    return lib;
  }

  /**
   * Runs some post processing information over the query line
   */
  private String multiLineComment(String input) {

    String[] lines = input.split("\\n");

    StringBuffer query = new StringBuffer();
    String postComment = null;

    for (int i = 0; i < lines.length; i++) {
      postComment = searchAndCutComments(lines[i]);
      query.append(postComment.replaceAll("\\s+", " "));
    }

    return query.toString();
  }

}