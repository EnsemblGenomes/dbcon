/*
 ##########################################################################
 #                          COPYRIGHT NOTICE                              #
 ##########################################################################
 #                                                                        #
 # Copyright (c) 2006 Genome Research Ltd.                                #
 # Author: The Cancer Genome Project IT group cancerit@sanger.ac.uk       #
 # Author: Andrew Yates andyyatz@gmail.com                                #
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Provides a limited amount of templating to be used in conjunction with
 * the SQL Libraries. Each templated region is indicated by flanking the
 * target name with {} such as:
 * 
 * <code>
 * select {0} from dual
 * </code>
 * 
 * Where a parameter named 0 will be replaced with any given object's toString
 * value. This is performed currently by looping through all named parameters
 * and performing a regex replaceAll.
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public class BasicTemplater {
	
	public static String template(String template, Object[] params) {
		BasicTemplater templater = new BasicTemplater(template, params);
		String output = templater.generate();
		return output;
	}
	
	public static String template(String template, Map params) {
		BasicTemplater templater = new BasicTemplater(template, params);
		String output = templater.generate();
		return output;
	}

	private String template = null;
	private Map params = null;
	
	public BasicTemplater(String template, Map params) {
		this(template);
		addParams(params);
	}
	
	public BasicTemplater(String template, Object[] params) {
		this(template);
		addParams(params);
	}
	
	public BasicTemplater(String template) {
		this();
		this.template = template;
	}
	
	public BasicTemplater() {
		this.params = new LinkedHashMap();
	}
	
	public void addParam(String key, Object value) {
		this.params.put(key, value);
	}
	
	public void addParams(Map params) {
		params.putAll(params);
	}
	
	/**
	 * Puts an array of parameters into the templater object. The name given
	 * to this depends on the current offset of parameters in the Object. e.g.
	 * If there are no parameters registered then the names will be 0, 1 & 2.
	 * If there are already 3 parameters then the names will be 3, 4 & 5
	 * 
	 * @param params The params array
	 */
	public void addParams(Object[] params) {
		int offset = this.params.size();
		for(int i =0; i<params.length; i++) {
			offset = offset + i;
			String offsetString = Integer.toString(offset);
			Object value = params[i];
			addParam(offsetString, value);
		}
	}
	
	public void clearParams() {
		this.params.clear();
	}
	
	public String getTemplate() {
		return this.template;
	}
	
	public String generate() {
		String finalValue = template;
		for(Iterator iter = params.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Map.Entry)iter.next();
			String key = (String)entry.getKey();
			String value = entry.getValue().toString();
			//Done because of regex
			String target = "\\{"+key+"\\}";
			finalValue = finalValue.replaceAll(target, value);
		}
		return finalValue;
	}
}
