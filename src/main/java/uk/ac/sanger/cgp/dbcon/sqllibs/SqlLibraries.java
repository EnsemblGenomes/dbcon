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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.sanger.cgp.dbcon.exceptions.SqlLibraryRuntimeException;
import uk.ac.sanger.cgp.dbcon.util.ReaderUtils;

/**
 * <p>
 * This is the main class from which SqlLibs should be loaded from & to.
 * SqlLibraries stores the SqlLibs in a private Map from which the lib
 * may be retrived without any need for reloading the object.
 * </p>
 *
 * @author Andrew Yates
 * @author $Author: ady $
 * @version $Revision: 1.27 $
 */
public class SqlLibraries {

	/** Holds current instance of the SqlLibraries object */
	private static SqlLibraries INSTANCE = null;

	/** Holds instance of the libraries Map which stores initalised libraries */
	private Map libraries = null;

	private Log log = LogFactory.getLog(SqlLibraries.class);

	/**
	 * Private default constructor to avoid class initalisation
	 */
	private SqlLibraries() {
		super();
		libraries = Collections.synchronizedMap(new LinkedHashMap());
	}

	/**
	 * Gets an instance of this SqlLibrary holder object
	 */
	public static synchronized SqlLibraries getInstance() {

		if(INSTANCE == null) {
			INSTANCE = new SqlLibraries();
		}

		return INSTANCE;
	}
  
  public SqlLib getLib(SqlLibLocation location) throws SqlLibraryRuntimeException {
    SqlLib lib = null;
    lib = getLib(location.getLocation());
    return lib;
  }

	/**
	 * Returns an already instanciated library from the libraries Map for
	 * a specified libraryName. Do not access custom libs this way unless you
	 * have already successfully loaded the lib.
	 *
	 * @param resource The library resource to retrieve. This is consistent with
	 * dbcon's method of specifying locations of resources
	 * @return The library specificed by resource
	 * @throws SqlLibraryRuntimeException Gets thrown if the library does not exist
	 * @see uk.ac.sanger.cgp.dbcon.util.ReaderUtils#returnReaderFromResource(java.lang.String)
	 */
	public SqlLib getLib(String resource) throws SqlLibraryRuntimeException {

		SqlLib lib = null;

		if(libraries.containsKey(resource)) {
			lib = (SqlLib)libraries.get(resource);
		}
		else {
			Reader reader = ReaderUtils.returnReaderFromResource(resource);
			if(reader != null) {
				lib = getLib(resource, reader);
				libraries.put(resource, lib);
			}
			if(lib==null) {
				throw new SqlLibraryRuntimeException("Submitted resource "+resource+" did not create a valid library");
			}
		}

		return lib;
	}

	/**
	 * Overloaded synonym for loadLibFromFile. This method allows for the
	 * conversion of method calls between using an external resource and using
	 * the inbuilt libraries.
	 *
	 * @param libraryName The library name to retrive
	 * @param	filename	The file location
	 * @return The library specificed by libraryName
	 * @deprecated IT'S JUST PLAIN BAD - USE SINGLE IDENTIFIER METHOD OR STREAM
	 */
	public SqlLib getLib(String libraryName, File filename) {
		return loadLibFromFile(libraryName, filename);
	}

	/**
	 * Loads an SqlLib from a specified file location/name and a library name
	 * from which this library can be called from again.
	 *
	 * @param libraryName The library name to retrive
	 * @param	filename	The file location
	 * @return The library specificed by libraryName
	 * @deprecated IT'S JUST PLAIN BAD - USE SINGLE IDENTIFIER METHOD OR STREAM
	 */
	public SqlLib loadLibFromFile(String libraryName, File filename) {

		SqlLib output = null;
		String resource = "file://"+filename.getAbsolutePath();
		output = getLib(resource);
		return output;
	}

	/**
	 * Overloaded synonym for loadLibFromFile. This method allows for the
	 * conversion of method calls between using an external resource and using
	 * the inbuilt libraries. Modified to allow the use of InputStreams instead
	 * of file handles.
	 *
	 * @param libraryName The library name to retrive
	 * @param	is					The stream used to populate this library
	 * @return The library specificed by libraryName
	 * @deprecated Very old version now no longer used
	 */
	public SqlLib loadLibFromFile(String libraryName, InputStream is) {
		return getLib(libraryName, is);
	}

	/**
	 * Loads an SqlLib from a specified file location/name and a library name
	 * from which this library can be called from again. Modified to allow the
	 * use of an InputStream instead of file handles.
	 *
	 * @param libraryName The library name to retrive
	 * @param	is					The stream used to populate this library
	 * @return The library specificed by libraryName
	 * @deprecated Use the getLib(String) method which will parse the InputStream
	 * if not then ask to have your resource location added
	 */
	public SqlLib getLib(String libraryName, InputStream is) {

		SqlLib output = null;

		//Testing to see if this library has already been initalised and if so,
		//return this library out instead of recreating the object
		if (libraries.containsKey(libraryName)) {
			output = (SqlLib)libraries.get(libraryName);
		}
		else {
			//Run the loading method found in SqlLib
			try {
				StringReader reader = new StringReader(IOUtils.toString(is));
				output = getLib(libraryName, reader);
			}
			catch(IOException e) {
				throw new SqlLibraryRuntimeException("Error occured whilst setting up the StringReader", e);
			}

			//Put this into the Map for future retrival
			libraries.put(libraryName, output);
		}

		return output;
	}

	/**
	 * Private method which uses a reader to parse the library out
	 */
	private SqlLib getLib(String libraryName, Reader reader) throws SqlLibraryRuntimeException {

		SqlLib output = null;
		Reader detectionReader = null;
		Reader parserReader = null;
		try {
			String temp = IOUtils.toString(reader);
			detectionReader = new StringReader(temp);
			parserReader = new StringReader(temp);
		}
		catch(IOException e) {
			throw new SqlLibraryRuntimeException("Could not copy input reader into " +
				"string readers for advanced processing", e);
		}

		LibraryParser parser = detectAndCreateParser(detectionReader);
		output = parser.parseLibrary(parserReader, libraryName);

		return output;
	}

	/**
	 * Returns a String[] for the currently loaded libraries
	 */
	public String[] avaialbleLibs() {
		return (String[])libraries.keySet().toArray(new String[0]);
	}

	/**
	 * Nulls all currently held SqlLib objects stored in the internal Hashtable.
	 */
	public static void reloadLibrary() {
		SqlLibraries.getInstance().libraries.clear();
	}

	/**
	 * Nulls a specified library held in the Map
	 */
	public void reloadLibrary(String key) {
		libraries.remove(key);
	}

	/**
	 * Takes in a string and looks to see if it is an XML definition file
	 */
	private LibraryParser detectAndCreateParser(Reader reader) throws SqlLibraryRuntimeException {

		LibraryParser parser = null;

		try {
			BufferedReader br = new BufferedReader(reader);
			if(!br.markSupported()) {
				throw new SqlLibraryRuntimeException("Reader does not support marking - aborting");
			}

			br.mark(50);
			char[] xmlDefArray = new char[6];
			br.read(xmlDefArray, 0, 6);
			br.reset();

			String xmlDef = new String(xmlDefArray);

			if(xmlDef.matches("^.*<\\?xml.*")) {
				if(log.isInfoEnabled()) log.info("Detected .xml sql library");
				parser = new XMLLibraryParser();
			}
			else {
				if(log.isInfoEnabled()) log.info("Detected .sqllib sql library");
				parser = new SqlLibLibraryParser();
			}
		}
		catch(IOException e) {
			throw new SqlLibraryRuntimeException("Error occured whilst testing the InputStream for XML syntax", e);
		}

		return parser;
	}

	/**
	 * Overriden version of toString() showing the contents of this object
	 */
	public String toString() {
		String nl = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("Object hashcode: ");
		sb.append(super.toString());
		sb.append(nl);
		sb.append("Available libs: ");
		sb.append(libraries.keySet().toString());
		sb.append(nl);
		return sb.toString();
	}
}
