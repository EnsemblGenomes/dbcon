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

package uk.ac.sanger.cgp.dbcon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * <p>
 * Set of methods which are used to create a set of obsfucated Strings to
 * be used to store passwords. This process is not clever or hard to crack
 * just prevents the oppertunistic hacker from gaining access unless they look
 * at this Javadoc in which case <em>clever boy</em>.
 * </p>
 *
 * <p>
 * The obsfucated method takes a random number from 1 - 15, adds this value
 * to the int value of each char in the String, then stores the offset as
 * the first char in the String but + 40. This offset is random for every
 * String this method encodes. If a calculated value in the String exceeds 126
 * i.e. it moves into the extended ASCII set we append a ! before it in the
 * obsfucated String and - 86 from the calculated value. The ! tells the
 * decoder that the next value needs to have this taken into account.
 * </p>
 *
 * <p>
 * Such as the String "hello" may be converted to 1qnuux where an offset of
 * 9 was chosen (indicated by a 1 which is 9 + 40) and each value of the phrase
 * is 9 away from the actual value.
 * <p>
 *
 * <p>
 * The code is also XML aware. Since it is possible for characters like &lt; and &gt;
 * to be encoded from the code it converts this into &amp;lt; and &amp;gt; which
 * is then compatable with XML. This is something to remember please do not use
 * passwords which contain characters from char value 39 and bellow. If you are
 * not sure which ones these are please see <a href="http://www.asciitable.com">here</a>.
 * </p>
 *
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.5 $
 */
public class Obsfucator {

	private static final int MIN_VALUE = 40;
	private static final int MAX_VALUE = 126;
	private static final int MAX_RAND_RANGE = 14;
	private static final int WRAPPER_VALUE = 86;
	private static Random rand = new Random();

	/** Prevents creation of a new instance of Obsfucator */
	private Obsfucator() {
		super();
	}

	/**
	 * This method takes a normal String in and converts them into an obsucated
	 * String.
	 *
	 * @param input The String to obsfucate
	 * @return The obsfucated String
	 */
	public static String encode(String input) {

		StringBuffer output = new StringBuffer();

		int offset = getRandomOffset();

		for(int i=0; i<input.length(); i++) {
			int intVal = ((int)input.charAt(i))+offset;
			//If the value exceeds the normal ascii range then we need to - 94 from it
			//to get it back into the normal range again
			if(intVal>MAX_VALUE) {
				intVal = intVal - WRAPPER_VALUE;
				output.append('!');
			}

			if(((char)intVal) == '<') {
				output.append("&lt;");
			}
			else if(((char)intVal) == '>') {
				output.append("&gt;");
			}
			else {
				output.append((char)intVal);
			}
		}

		output.insert(0, convertOffsetIntToChar(offset));

		return output.toString();
	}

	/**
	 * This method returns the correct String from the obsucated one.
	 *
	 * @param input The obsfucated String to decode
	 * @return The decoded String
	 */
	public static String decode(String input) {

		StringBuffer output = new StringBuffer();

		int offset = -1;

		for(int i=0; i<input.length(); i++) {
			if(i == 0) {
				offset = convertOffsetCharToInt(input.charAt(i));
			}
			else {
				int intVal = 0;
				char charVal = ' ';
				if(input.charAt(i) == '!') {
					i++;
					intVal = (int)input.charAt(i) - offset;
					intVal = intVal + WRAPPER_VALUE;
					charVal = (char)intVal;
				}
				//Looking for things like &gt; and &lt;
				else if(input.charAt(i) == '&') {
					String obs = "";
					for(int j=0;j<4;j++) {
						obs = obs + input.charAt(i);
						if(j != 3) i++;
					}
					if(obs.equalsIgnoreCase("&lt;")) {
						charVal = '<';
					}
					else if(obs.equalsIgnoreCase("&gt;")) {
						charVal = '>';
					}
					intVal = ((int)charVal) - offset;
					charVal = (char)intVal;
				}
				else {
					intVal = ((int)input.charAt(i)) - offset;
					charVal = (char)intVal;
				}

				output.append(charVal);
			}
		}

		return output.toString();
	}

	/**
	 * Generates the random number offset to encode the String
	 */
	private static int getRandomOffset() {
		return rand.nextInt(MAX_RAND_RANGE)+1;
	}

	/**
	 * Converts the offset to a char + 33 to get it into the normal ASCII range.
	 */
	private static char convertOffsetIntToChar(int offset) {
		return (char)(offset+MIN_VALUE);
	}

	/**
	 * Converts the offset to a char + 33 to get it into the normal ASCII range.
	 */
	private static int convertOffsetCharToInt(char offsetChar) {
		return ((int)offsetChar)-MIN_VALUE;
	}

	/**
	 * Simple main that takes the input from args, obs it and then unobs and
	 * prints out all results.
	 */
	public static void main(String[] args) {

		System.out.println("");
		System.out.println("Which action do you want to perform:");
		System.out.println("\t1). Encode string");
		System.out.println("\t2). Decode string");

		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(System.in));

			//Reading the action command
			String input = reader.readLine();
			int action = Integer.parseInt(input.trim());

			//Reading the input
			System.out.println("");
			System.out.println("Please insert the input");
			System.out.println("");
			input = reader.readLine();

			String output = "";

			if(action == 1) {
				output = encode(input);
			}
			else if(action == 2) {
				output = decode(input);
			}
			else {
				System.out.println("Unknown action specified - exiting");
				System.exit(1);
			}

			System.out.println("");
			System.out.println(output);
			System.out.println("");

			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
