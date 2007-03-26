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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.util.Obsfucator;

/**
 * Uses the new XML Object Model toolkit to parse the XML based configuration
 * file. This is different to all other versions of the config creator as it
 * stores the config in memory as the XML sheet and uses XPath queries to extract
 * all relevant values out and places it into a new config file.
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.6 $
 */
public class XOMConfigParser extends ConfigParser {

	private List _documentList = null;

	/** Creates a new instance of XOMConfigParser */
	protected XOMConfigParser(){
		super();
	}

	/**
	 * A method which loads and parses the config XML files into memory
	 */
	protected void init() throws DbConException {
		if(_documentList != null) return;

		_documentList = new ArrayList();

		try {
			Builder parser = new Builder();
			//Getting out the list of config readers
			Iterator readers = getConfigReaders().iterator();
			while(readers.hasNext()) {
				Reader reader = (Reader)readers.next();
				_documentList.add(parser.build(new BufferedReader(reader)));
			}
		}
		catch(IOException e) {
			throw new DbConException("An error occured whilst trying to read the dbcon.xml file", e);
		}
		catch(ParsingException e) {
			throw new DbConException("An error occured whilst parsing the config file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getAvailableDBs() {

		safeInit();
		Set names = new TreeSet();
		Iterator iter = _documentList.iterator();
		while(iter.hasNext()) {
			Document documentStore = (Document)iter.next();
			//This XPath query looks for all elements in the XML file which are <pool>.
			Nodes availableNodes = documentStore.getRootElement().query("//pool");
			for(int i=0; i<availableNodes.size(); i++) {
				names.add(((Element)availableNodes.get(i)).getAttributeValue("synonym").trim());
			}
		}

		return (String[])names.toArray(new String[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	public Config getConfig(String name) {

		safeInit();

		Config config = null;

		Iterator iter = _documentList.iterator();
		while(iter.hasNext()) {
			Document documentStore = (Document)iter.next();
			Nodes availableNodes = documentStore.getRootElement().query("//pool[@synonym='"+name+"']");
			if(availableNodes.size() == 0) {
				//If we can't find a def then move onto the next one
				continue;
			}
			else {
				config = Config.createEmptyConfig();

				try {
					config.setName(name);
					Element poolElement = (Element)availableNodes.get(0);
					config.setDriver(xPathForSingleElementValue(poolElement.getParent(), "driver"));
					config.setUrl(xPathForSingleElementValue(poolElement.getParent(), "url"));
					config.setBackupUrl(xPathForSingleElementValue(poolElement.getParent(), "backupurl"));
					config.setUsername(xPathForSingleElementValue(poolElement, "username"));
					config.setPassword(Obsfucator.decode(xPathForSingleElementValue(poolElement, "password")));
					config.setValidationQuery(xPathForSingleElementValue(poolElement, "query"));
					config.setMaxActive(parseLong(xPathForSingleElementValue(poolElement, "maxconnections"), config.getName(), "maxconnections").intValue());
					config.setExhausted(parseLong(xPathForSingleElementValue(poolElement, "exhaustedaction"), config.getName(), "exhaustedaction").byteValue());
					config.setMaxWait(parseLong(xPathForSingleElementValue(poolElement, "timeout"), config.getName(), "timeout").longValue());
					config.setMaxIdle(parseLong(xPathForSingleElementValue(poolElement, "maxidle"), config.getName(), "maxidle").intValue());
					config.setTestOnBorrow(Boolean.valueOf(xPathForSingleElementValue(poolElement, "testonborrow")).booleanValue());
					config.setTestOnReturn(Boolean.valueOf(xPathForSingleElementValue(poolElement, "testonreturn")).booleanValue());
					config.setTestWhileIdle(Boolean.valueOf(xPathForSingleElementValue(poolElement, "testwhileidle")).booleanValue());
					config.setTimeBetweenEvictRun(parseLong(xPathForSingleElementValue(poolElement, "timebetweenevicts"), config.getName(), "timebetweenevicts").longValue());
					config.setMinEvictTime(parseLong(xPathForSingleElementValue(poolElement, "mintimeforevict"), config.getName(), "mintimeforevict").longValue());
					config.setNumTestsPerEvictionRun(parseLong(xPathForSingleElementValue(poolElement, "numberofevicttests"), config.getName(), "numberofevicttests").intValue());
					config.setCachedPreparedStatements(parseLong(xPathForSingleElementValue(poolElement, "cachedpreparedstatements"), config.getName(), "cachedpreparedstatements").intValue());
				}
				catch(DbConException e) {
					throw new DbConException("Exception occured during config parsing. Runtime exception thrown as an unchecked exception", e);
				}
			}
		}

		if(config == null) {
			throw new DbConException("Could not find pool definition for "+name+" in "+_documentList.size()+" config file(s)");
		}

		return config;
	}

	/**
	 * Small method which takes in the node and an xpath query and returns the
	 * String value of the searched parameter and trims the result
	 */
	private String xPathForSingleElementValue(Node node, String query) {
		String output = "";
		Nodes availNodes = node.query(query);
		if(availNodes.size() > 0) {
			output = ((Element)availNodes.get(0)).getValue();
		}
		return output.trim();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean checkConfig(String name) {
		//Get the available names out and then sort these for the binary search
		//as binary search says we HAVE to have sorted the array into a natural order
		String[] names = getAvailableDBs();
		Arrays.sort(names);

		//First check for the config existing
		int index = Arrays.binarySearch(names, name);
		if(index < 0) return false;

		//If the name exisits then we say that it's okay
		return true;
	}

	/**
	 * Reloads all configuration from the original resources
	 */
	public void reload() {
		_documentList = null;
	}
}
