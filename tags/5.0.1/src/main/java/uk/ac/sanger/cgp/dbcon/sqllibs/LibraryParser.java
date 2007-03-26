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

import java.io.Reader;
import java.util.regex.Pattern;

import uk.ac.sanger.cgp.dbcon.exceptions.SqlLibraryRuntimeException;

/**
 * Abstract class defined to help with the tight casting of how to deal with
 * parsers of any current or possible future formats of holding SQL statements.
 * 
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.8 $
 */
public abstract class LibraryParser {

  /** Holds the instance of a // comment in library files */
  public static final String SLASH_COMMENT = "//";

  /** Holds the instance of a -- comment as used in oracle and most RDBMS */
  public static final String DASH_COMMENT = "--";

  private static final Pattern STAR_COMMENT_PATTERN;

  private static final String FULL_STAR_COMMENT = "/\\*[^\\+][^\\*/]*\\*/";

  public static final Pattern DASH_COMMENT_PATTERN;

  private static final String DASH_COMMENT_PRE = "--.*";

  public static final Pattern WHITESPACE_PATTERN;

  private static final String WHITESPACE_PRE = "\\s+";

  static {
    DASH_COMMENT_PATTERN = Pattern.compile(DASH_COMMENT_PRE,
        Pattern.CASE_INSENSITIVE);
    STAR_COMMENT_PATTERN = Pattern.compile(FULL_STAR_COMMENT,
        Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    WHITESPACE_PATTERN = Pattern.compile(WHITESPACE_PRE);
  }

  /**
   * The abstract method through which an InputStream is parsed to create the
   * SqLlLib objects.
   * 
   * @param input
   *          The <code>InputStream</code> to be parsed
   * @return The object representation of the SqlLib
   */
  public abstract SqlLib parseLibrary(Reader input, String libraryName)
      throws SqlLibraryRuntimeException;

  /**
   * Takes in a String and searches it for the potential comments allowed in
   * these files. The main function is browsing for in-line comments
   */
  protected String searchAndCutComments(String input) {
    return DASH_COMMENT_PATTERN.matcher(input).replaceFirst("");
  }

  /**
   * New version of the comment remover which uses the regular expression
   * objects to search for /\*[^\+][^\*\]*\*\/ which should remove any block
   * comment however it will not cope with open block comments.
   */
  private String removeStarComments(String input) {
    return STAR_COMMENT_PATTERN.matcher(input).replaceAll("");
  }

  /**
   * Runs the pattern \\s+ and replaces any amount of whitespace with a single
   * space.
   */
  private String cleanUpWhitespace(String input) {
    return WHITESPACE_PATTERN.matcher(input).replaceAll(" ");
  }

  /**
   * Runs the whitepsace and the star comments cleanup code. It also runs an
   * addition of a space at the end of each query
   */
  protected String runQueryCleanup(String input) {
    String output = removeStarComments(input);
    output = cleanUpWhitespace(output);
    output = output + ' ';
    return output;
  }
}
