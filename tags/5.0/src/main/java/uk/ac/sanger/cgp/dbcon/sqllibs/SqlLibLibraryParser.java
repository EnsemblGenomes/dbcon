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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.ac.sanger.cgp.dbcon.exceptions.SqlLibraryRuntimeException;

/**
 * Solid implementation of the library parsers class which parses the default
 * SqlLibrary format as defined by Dr. Simon Forbes.
 * 
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.7 $
 */
public class SqlLibLibraryParser extends LibraryParser {

  public static final String SQL_NAME_START = "<SQL-NAME>";

  public static final String SQL_NAME_STOP = "</SQL-NAME>";

  public static final String SQL_START = "<SQL>";

  public static final String SQL_STOP = "</SQL>";

  /**
   * Solid implementation of the parseLibrary InputStream method.
   */
  public SqlLib parseLibrary(Reader input, String libraryName)
      throws SqlLibraryRuntimeException {

    Map statements = new Hashtable();

    BufferedReader br = null;
    String line = null;
    String name = null;
    String query = null;
    Statement statement = null;

    try {
      br = new BufferedReader(input);
      boolean lookForStatement = false;

      while ((line = br.readLine()) != null) {
        // If this line contains an SQL-NAME tag
        if (matchesTag(line, SQL_NAME_START)) {
          name = parseName(br);
          lookForStatement = true;
        }
        else if (lookForStatement && matchesTag(line, SQL_START)) {
          query = parseQuery(br);
          query = query.trim();
          query = runQueryCleanup(query);
          statement = new Statement();
          statement.setName(name.trim());
          statement.setStatement(query);
          statements.put(statement.getName(), statement);
          name = null;
          query = null;
          lookForStatement = false;
        }
        else if (matchesTag(line, SLASH_COMMENT)) {
          continue;
        }
      }
    }
    catch (IOException e) {
      throw new SqlLibraryRuntimeException(
          "An error occured whilst parsing the input Sql library file", e);
    }

    SqlLibImpl output = new SqlLibImpl(statements);
    output.setLibraryName(libraryName);

    return output;
  }

  /**
   * Wrapper around parse element to return the SQL Name of an element
   */
  private String parseName(BufferedReader br) throws IOException {

    String name = parseElement(br, SQL_NAME_STOP);
    if (name != null) {
      name = name.trim();
    }

    return name;
  }

  /**
   * Wrapper around parse element to return the SQL of an element
   */
  private String parseQuery(BufferedReader br) throws IOException {
    return parseElement(br, SQL_STOP);
  }

  /**
   * Generic code which takes in an element's closing tag and runs anything it
   * has into the String output.
   */
  private String parseElement(BufferedReader br, String closingTag)
      throws IOException {

    StringBuffer element = new StringBuffer();
    String line = null;

    while ((line = br.readLine()) != null) {
      // Cleaning it up
      line = line.trim();
      if (matchesTag(line, closingTag)) {
        break;
      }
      else {
        // Each line because of the input stuff means that we do not have
        // a space between the parsed lines
        element.append(' ');
        // Run the cut comments script
        element.append(searchAndCutComments(line));
      }
    }

    return element.toString();
  }

  /**
   * Used as a convenience method to store matcher objects to help for quick
   * retrieval
   */
  private final Map matcherCache = new HashMap();

  /**
   * Holds the starting regex which is ^\s*
   */
  private static final String STARTING_REGEX = "^\\s*";

  /**
   * Holds the ending regex which is .*
   */
  private static final String ENDING_REGEX = ".*";

  /**
   * Uses the given search String, surrounds it with ^\s* and .* and returns
   * true if it can find this. The regex is case insensitive
   */
  private boolean matchesTag(String line, String searchString) {

    boolean matches = false;

    if (!matcherCache.containsKey(searchString)) {
      StringBuffer regex = new StringBuffer(STARTING_REGEX);
      regex.append(searchString);
      regex.append(ENDING_REGEX);
      // Making it case insensitive
      Pattern pattern = Pattern.compile(regex.toString(),
          Pattern.CASE_INSENSITIVE);
      // Put it int the cache
      matcherCache.put(searchString, pattern);
    }

    // Now grab it out and run it
    Pattern pattern = (Pattern) matcherCache.get(searchString);
    Matcher matcher = pattern.matcher(line);
    matches = matcher.matches();

    return matches;
  }

}
