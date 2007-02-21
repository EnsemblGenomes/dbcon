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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.sanger.cgp.dbcon.exceptions.DbConException;

/**
 * Holds a number of methods which help when trying to create and run multi-threading
 * test environments
 *
 * @author Andy Yates
 * @author $Author: ady $
 * @version $Revision: 1.1.1.1 $
 */
public class ThreadHelpers {
	
	/**
	 * Creates a new instance of ThreadHelpers
	 */
	public ThreadHelpers() {
		super();
	}
	
	private static final String EXCEPTION_MSG = "Could not instanciate threadClass";
	
	/**
	 * For the given thread class (which should be a ThreadHelpers.AbstractTestThread thread)
	 * this will instantiate the thread with an ID
	 */
	public static List createAndStartThreads(final Class threadClass, final int number) {
		
		if(!AbstractThread.class.isAssignableFrom(threadClass)) {
			throw new DbConException("Thread that was passed in was not assignable from AbstractDbconThread");
		}
		
		//Find the constructor we need first
		Constructor constructor = null;
		
		try {
			constructor = threadClass.getConstructor(new Class[]{String.class});
		} 
		catch (SecurityException e) {
			throw new DbConException(EXCEPTION_MSG, e);
		} 
		catch (NoSuchMethodException e) {
			throw new DbConException(EXCEPTION_MSG, e);
		}
		
		if(constructor == null) {
			throw new DbConException("Could not find specified constructor threadClass(String)");
		}
		
		//Create all the objects we need
		ArrayList threads = new ArrayList();
		for(int i=0; i<number; i++) {
			
			String threadId = Integer.toString(i);
			
			Thread thread = null;
			
			try {
				thread = (Thread)constructor.newInstance(new Object[]{threadId});
			}
			catch (IllegalArgumentException e) {
				throw new DbConException(EXCEPTION_MSG, e);
			} 
			catch (InvocationTargetException e) {
				throw new DbConException(EXCEPTION_MSG, e);
			} 
			catch (InstantiationException e) {
				throw new DbConException(EXCEPTION_MSG, e);
			} 
			catch (IllegalAccessException e) {
				throw new DbConException(EXCEPTION_MSG, e);
			}

			threads.add(thread);
		}
		
		//Once they're all created start them off
		for(int i=0; i<threads.size(); i++) {
			((Thread)threads.get(i)).start();
		}
		
		return threads;
	}

	
	public static void waitForAllThreadsToFinish(final List threads) {
		//First loop through to make sure all threads have finished
		while(true) {

			//Here we count to make sure that all threads are finished
			int threadFinishedCount = 0;

			for(int i=0; i<threads.size(); i++) {
				Thread thread = (Thread)threads.get(i);
				if(!thread.isAlive()) {
					threadFinishedCount++;
				}
			}
			//If all threads are finished then break out of the loop
			if(threadFinishedCount == threads.size()) {
				break;
			}
			else {
				
				//Sleeps for 1/2 a second
				try {
					Thread.sleep(500L);
				} 
				catch (InterruptedException e) {
					throw new DbConException("Could not make current thread wait", e);
				}
			}
		}
	}
}
