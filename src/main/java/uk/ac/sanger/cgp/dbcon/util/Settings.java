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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;
import uk.ac.sanger.cgp.dbcon.util.resources.ClasspathResource;
import uk.ac.sanger.cgp.dbcon.util.resources.Resource;

/**
 * A file of constants to be used around the entire application. Also contains
 * the list of resources to be consulted for dbcon configuration locations. Currently
 * this defaults to
 * 
 * <ol>
 * <li>{@link #getConfigLocationsFromSystemProperty()} - Should be set as a line of , separated locations</li>
 * <li>{@link #getConfigLocationsFromPropertiesFile()} - Each location should be NAME=LOCATION where name is irrelevant</li>
 * <li>Class path resource /dbcon.xml</li>
 * </ol>
 * 
 *
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.7 $
 */
public class Settings {

	/** Prevents a new instance of Settings */
	private Settings() {
		super();
	}

	/** Holds location of the configuration files for DBCon */
	private static List locs = Collections.synchronizedList(new ArrayList());
  
  public static void initaliseDefaults() {
    addConfigLocations(getConfigLocationsFromSystemProperty());
    addConfigLocations(getConfigLocationsFromPropertiesFile());
    addConfigLocations("/dbcon.xml");
  }

	static {
    initaliseDefaults();
	}

	/**
	 * Holds a static list of locations which are used to parse the locations and settings
	 * for Pooling DB settings. The last item in the list takes the greatest precidence in
	 * all Config parsers. The returned list is immutable.
	 */
	public static List getConfigLocations() {
		return Collections.unmodifiableList(locs);
	}
  
  public static final String SYSTEM_PROPERTY = "uk.ac.sanger.cgp.dbcon.config.locations";
  
  protected static List getConfigLocationsFromSystemProperty() {
    String locations = System.getProperty(SYSTEM_PROPERTY, StringUtils.EMPTY);
    String[] locationsArray = locations.split(",");
    return Arrays.asList(locationsArray);
  }
  
  public static final String PROPERTIES_LOCATION = "/dbcon-config.properties";
  
  protected static List getConfigLocationsFromPropertiesFile() {
    Resource resource = new ClasspathResource(PROPERTIES_LOCATION);
    InputStream resourceInputStream = null;
    
    try {
      resourceInputStream = resource.getInputStream();
    }
    catch(DbConException e) {
      return Collections.EMPTY_LIST;
    }
    
    Properties props = new Properties();
    
    try {
      props.load(resourceInputStream);
    }
    catch (IOException e) {
      throw new DbConException("Could not load properties from "+PROPERTIES_LOCATION, e);
    }
    finally {
      InputOutputUtils.closeQuietly(resourceInputStream);
    }
    
    return new ArrayList(props.values());
  }

	/**
	 * Places a config location for DBCon into the background map file
	 */
	public static void addConfigLocations(String newLocation) {
		locs.add(newLocation);
	}
  
  public static void addConfigLocations(List newLocations) {
    locs.addAll(newLocations);
  }

	/**
	 * A method which runs a clear on the underlying List
	 */
	public static void clearConfigLocations() {
		locs.clear();
  }
}
