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

package uk.ac.sanger.cgp.dbcon.support;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;

/**
 * Contains a number of helper methods for usage with reflection
 * 
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.1.1.1 $
 */
public class ReflectionUtils {

  /**
   * Creates a new instance of ReflectionUtils
   */
  private ReflectionUtils() {
    super();
  }

  /**
   * Attempts to list all the classes in the specified package as determined by
   * the context class loader
   * 
   * @param pckgname
   *          the package name to search
   * @return a list of classes that exist within that package
   * @throws ClassNotFoundException
   *           if something went wrong
   */
  public static List getClassesForPackage(String pckgname)
      throws ClassNotFoundException {

    // This will hold a list of directories matching the pckgname. There may be
    // more than one if a package is split over multiple jars/paths
    ArrayList directories = new ArrayList();
    try {
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      if (cld == null) {
        throw new ClassNotFoundException("Can't get class loader.");
      }
      String path = pckgname.replace('.', '/');
      // Ask for all resources for the path
      Enumeration resources = cld.getResources(path);
      while (resources.hasMoreElements()) {
        directories.add(new File(URLDecoder.decode(((URL) resources
            .nextElement()).getPath(), "UTF-8")));
      }
    }
    catch (NullPointerException x) {
      throw new ClassNotFoundException(pckgname
          + " does not appear to be a valid package (Null pointer exception)");
    }
    catch (UnsupportedEncodingException encex) {
      throw new ClassNotFoundException(pckgname
          + " does not appear to be a valid package (Unsupported encoding)");
    }
    catch (IOException ioex) {
      throw new ClassNotFoundException(
          "IOException was thrown when trying to get all resources for "
              + pckgname);
    }

    ArrayList classes = new ArrayList();
    // For every directory identified capture all the .class files
    Iterator iter = directories.iterator();
    while (iter.hasNext()) {
      File directory = (File) iter.next();
      if (directory.exists()) {
        // Get the list of the files contained in the package
        String[] files = directory.list();
        for (int i = 0; i < files.length; i++) {
          String file = files[i];
          // we are only interested in .class files
          if (file.endsWith(".class")) {
            // removes the .class extension
            classes.add(Class.forName(pckgname + '.'
                + file.substring(0, file.length() - 6)));
          }
        }
      }
      else {
        throw new ClassNotFoundException(pckgname + " (" + directory.getPath()
            + ") does not appear to be a valid package");
      }
    }
    return classes;
  }

  /**
   * For the given class this method will create a zero argument class
   * 
   * @return The object will be null if the class failed to be created and did
   *         not throw an exception
   * @throws Wraps
   *           any exception that could be thrown from instanciation
   */
  public static Object createInstanceFromClass(Class _class) {
    return createInstanceFromClass(_class, new Object[0]);
  }

  /**
   * For the given class this method will create an object with the given inputs
   * as the constructor arguments. The object array must be constructed with the
   * exact same order of the inputs to the constructor
   * 
   * @return The object will be null if the class failed to be created and did
   *         not throw an exception
   * @throws Wraps
   *           any exception that could be thrown from instanciation
   */
  public static Object createInstanceFromClass(Class _class, Object[] args) {

    Object output = null;

    Class[] classInputs = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      classInputs[i] = args[i].getClass();
    }

    try {
      if (args.length == 0) {
        output = _class.newInstance();
      }
      else {
        Constructor constructor = _class.getConstructor(classInputs);
        output = constructor.newInstance(args);
      }
    }
    catch (NoSuchMethodException e) {
      throwExceptionFromInstance(_class, args, e);
    }
    catch (SecurityException e) {
      throwExceptionFromInstance(_class, args, e);
    }
    catch (InstantiationException e) {
      throwExceptionFromInstance(_class, args, e);
    }
    catch (IllegalAccessException e) {
      throwExceptionFromInstance(_class, args, e);
    }
    catch (InvocationTargetException e) {
      throwExceptionFromInstance(_class, args, e);
    }

    return output;
  }

  /**
   * Used when we want an exception to be thrown from the create instance method
   */
  private static void throwExceptionFromInstance(Class _class, Object[] args,
      Throwable cause) {

    // Creating the exception text
    List argsList = Arrays.asList(args);
    String uncheckedExceptionText = "Could not create object "
        + _class.getName() + " from args " + argsList.toString();

    // Now create it and throw
    throw new DbConException(uncheckedExceptionText, cause);
  }
}
