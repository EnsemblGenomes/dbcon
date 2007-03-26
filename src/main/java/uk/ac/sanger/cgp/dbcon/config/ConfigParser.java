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

package uk.ac.sanger.cgp.dbcon.config;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.util.ReaderUtils;
import uk.ac.sanger.cgp.dbcon.util.Settings;

/**
 * An absract that has been defined to hold generic methods which can
 * be used by top end classes to retrieve information about a connection
 * pool and if that configuration is correct.
 *
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.9 $
 */
public abstract class ConfigParser {

	private static Pattern numericPattern = null;

	private static final Log STATIC_LOGGER = LogFactory.getLog(ConfigParser.class);
	protected Log log = LogFactory.getLog(this.getClass());

	static {
		numericPattern = Pattern.compile("\\d+");
	}

	/**
	 * Protected constructor
	 */
	protected ConfigParser() {
		//Nothing
	}

	/**
	 * Method which sends back a String[] of all available DBs to the configuration
	 * client to load from. These names can then be used in the parser to
	 * further extract more information.
	 */
	public abstract String[] getAvailableDBs();

	/**
	 * Returns a PoolConfig object for a given DB name.
	 *
	 * @param name The name/symbol of the database object which is stored in the config area
	 * @return The pool configuration object for the symbol
	 * @see uk.ac.sanger.cgp.dbcon.config.Config
	 */
	public abstract Config getConfig(String name);

	/**
	 * Returns a boolean indicating if config is good for a given DB name.
	 *
	 * @param name The name/symbol of the database object which is stored in the config area
	 */
	public abstract boolean checkConfig(String name);

	/**
	 * Forces a reloading of the configuration file
	 * @throws DbConException If there is an issue with the reload
	 */
	public abstract void reload();

	/**
	 * Runs a check for the existence of non numeric characters in the input
	 * String.
	 */
	public boolean checkNumberInput(String input) {
		boolean check = false;
		Matcher match = numericPattern.matcher(input);
		check = match.matches();
		return check;
	}

	/**
	 * Parses and returns a Long object of any long integer object and formats
	 * the correct exception
	 *
	 * @throws DbConException If there is a value parsing issue
	 */
	public Long parseLong(String inputLong, String db, String property) {

		Long currentLong = null;

		try {
			currentLong = new Long(inputLong);
		}
		catch(NumberFormatException e) {
			throw new DbConException("Error with "+property+": "+inputLong+" for synonym "+db, e);
		}

		return currentLong;
	}

	/**
	 * Returns the current default parser for the system. When searching for
	 * the XML parser the program attempts to find the nu.xom.Builder class on
	 * the classpath. If it cannot find this class then it will default to the
	 * parser based on JDOM.
	 */
	public static ConfigParser getDefaultParser() {

		ConfigParser parser = null;

		try {
			Class.forName("nu.xom.Builder");
			parser = new XOMConfigParser();
			if(STATIC_LOGGER.isInfoEnabled()) STATIC_LOGGER.info("Found XOM on classpath; will use as XML Parser");
		}
		//If anything goes wrong then we default to JDOM's parser
		catch(Exception e) {
			parser = new XMLConfigParser();
			if(STATIC_LOGGER.isInfoEnabled()) STATIC_LOGGER.info("Could not find XOM on classpath; will use JDOM as XML Parser");
		}

		return parser;
	}

	/**
	 * Goes through all configured URIs for Config locations, streams these into local
	 * memory stores of the file and produces Reader pointers to the files contents.
	 *
	 * @return A list of Readers which the parser <strong>has the responsibility of closing</strong>
	 * @throws DbConException If no locations give a parsable configuration location
	 */
	protected List getConfigReaders() {
		List output = new ArrayList();

		//Change this to something a bit better later on
		List locs = Settings.getConfigLocations();

		Iterator iter = locs.iterator();
		while(iter.hasNext()) {
			String resource = (String)iter.next();
			Reader reader = ReaderUtils.returnReaderFromResource(resource);
			if(reader != null) output.add(reader);
		}

		if(output.size() == 0) {
			throw new DbConException("Error occured whilst parsing Config locations. None returned a valid parsable location");
		}

		return output;
	}

	/**
	 * Method to be used for initalisation of the parser
	 */
	protected abstract void init() throws DbConException;

	/**
	 * Safe initalisation of the object
	 */
	protected synchronized void safeInit() {
		try {
			init();
		}
		catch(DbConException e) {
			if(log.isFatalEnabled()) log.fatal("Error occured during initalising settings", e);
		}
	}
}
