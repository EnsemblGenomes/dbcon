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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.util.Obsfucator;

/**
 * Loads the XML configuration file into memory which is meant to provide
 * greater flexibility when using files.
 *
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.19 $
 */
public class XMLConfigParser extends ConfigParser {

	private String[] dbNames = null;
	private Hashtable configs = null;

	/** Creates a new instance of XMLConfigParser */
	protected XMLConfigParser() {
		super();
	}

	/**
	 * A method which loads the config XML file into memory
	 * @throws DbConException If there is an error with initalisation
	 */
	protected void init() {

		if(configs != null) return;

		configs = new Hashtable();

		try {
			SAXBuilder builder = new SAXBuilder();

			//Getting out the list of config readers
			Iterator readers = getConfigReaders().iterator();
			while(readers.hasNext()) {
				Reader reader = (Reader)readers.next();
				//Now open the local copy of the XML and read this off
				BufferedReader br = new BufferedReader(reader);
				Document doc = builder.build(br);
				Iterator dbIterator = doc.getRootElement().getChildren().iterator();
				Element db = null;
				String driver = "";
				String url = "";
				String backupUrl = "";

				while(dbIterator.hasNext()) {
					db = (Element)dbIterator.next();
					Iterator subIter = db.getChildren().iterator();
					Element el = null;

					while(subIter.hasNext()) {
						el = (Element)subIter.next();

						//Generic DB driver
						if(el.getName().equalsIgnoreCase("driver")) driver = el.getText().trim();

						//Generic DB url
						else if(el.getName().equalsIgnoreCase("url")) url = el.getText().trim();

						//Generic DB backup url
						else if(el.getName().equalsIgnoreCase("backupurl")) backupUrl = el.getText().trim();

						//Individual pool definitions
						else if(el.getName().equalsIgnoreCase("pool")) {
							Config config = Config.createEmptyConfig();
							config.setDriver(driver);
							config.setUrl(url);
							config.setBackupUrl(backupUrl);
							config.setName(el.getAttributeValue("synonym").trim());

							Iterator settingsIterator = el.getChildren().iterator();
							Element settings = null;
							while(settingsIterator.hasNext()) {
								settings = (Element)settingsIterator.next();

								if(settings.getName().equalsIgnoreCase("username")) config.setUsername(settings.getText().trim());

								else if(settings.getName().equalsIgnoreCase("password")) {
									//The password is currently obsfucated therefore we need to undo it
									String passwordObs = settings.getText().trim();
									String password = Obsfucator.decode(passwordObs);
									config.setPassword(password);
								}

								else if(settings.getName().equalsIgnoreCase("query")) config.setValidationQuery(settings.getText().trim());

								else if(settings.getName().equalsIgnoreCase("maxconnections")) {
									config.setMaxActive( parseLong(settings.getText().trim(), config.getName(), "maxconnections").intValue() );
								}

								else if(settings.getName().equalsIgnoreCase("exhaustedaction")) {
									config.setExhausted( parseLong(settings.getText().trim(), config.getName(), "exhaustedaction").byteValue() );
								}

								else if(settings.getName().equalsIgnoreCase("timeout")) {
									config.setMaxWait( parseLong(settings.getText().trim(), config.getName(), "timeout").longValue() );
								}

								else if(settings.getName().equalsIgnoreCase("maxidle")) {
									config.setMaxIdle( parseLong(settings.getText().trim(), config.getName(), "maxidle").intValue() );
								}

								else if(settings.getName().equalsIgnoreCase("testonborrow")) {
									config.setTestOnBorrow( Boolean.valueOf(settings.getText().trim()).booleanValue());
								}

								else if(settings.getName().equalsIgnoreCase("testonreturn")) {
									config.setTestOnReturn( Boolean.valueOf(settings.getText().trim()).booleanValue());
								}

								else if(settings.getName().equalsIgnoreCase("testwhileidle")) {
									config.setTestWhileIdle( Boolean.valueOf(settings.getText().trim()).booleanValue());
								}

								else if(settings.getName().equalsIgnoreCase("timebetweenevicts")) {
									config.setTimeBetweenEvictRun( parseLong(settings.getText().trim(), config.getName(), "timebetweenevicts").longValue() );
								}

								else if(settings.getName().equalsIgnoreCase("mintimeforevict")) {
									config.setMinEvictTime( parseLong(settings.getText().trim(), config.getName(), "mintimeforevict").longValue() );
								}

								else if(settings.getName().equalsIgnoreCase("numberofevicttests")) {
									config.setNumTestsPerEvictionRun( parseLong(settings.getText().trim(), config.getName(), "numberofevicttests").intValue() );
								}

								else if(settings.getName().equalsIgnoreCase("cachedpreparedstatements")) {
									config.setCachedPreparedStatements(parseLong(settings.getText().trim(), config.getName(), "cachedpreparedstatements").intValue() );
								}
							}
							configs.put(config.getName(), config);
						}
					}
				}

				//Closing down the most recent reader
				br.close();
			}
		}
		catch(JDOMException e) {
			throw new DbConException("An error occured whilst trying to parse the dbcon.xml file", e);
		}
		catch(IOException e) {
			throw new DbConException("An error occured whilst trying to parse the dbcon.xml file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getAvailableDBs() {

		safeInit();

		if(dbNames == null) {
			dbNames = (String[])configs.keySet().toArray(new String[configs.keySet().size()]);
			Arrays.sort(dbNames);
		}

		//Now do a copy of the array and pass that out
		String[] output = new String[dbNames.length];
		System.arraycopy(dbNames, 0, output, 0, dbNames.length);

		return output;
	}

	/**
	 * {@inheritDoc}
	 */
	public Config getConfig(String name) {

		safeInit();

		Config config = (Config)configs.get(name);
		if(config != null) {
			config = (Config)config.clone();
		}
		return config;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean checkConfig(String name) {
		//First check for the config existing
		int index = Arrays.binarySearch(getAvailableDBs(), name);
		if(index < 0) return false;

		//If the name exisits then we say that it's okay
		return true;
	}

	/**
	 * Reloads all configuration from the original resources
	 */
	public void reload() {
		configs = null;
		dbNames = null;
	}
}
