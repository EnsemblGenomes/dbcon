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

import java.sql.Connection;
import java.sql.Statement;

import uk.ac.sanger.cgp.dbcon.pooling.Connections;
import uk.ac.sanger.cgp.dbcon.pooling.Pools;
import uk.ac.sanger.cgp.dbcon.util.DatabaseUtils;

/**
 * Used to help in setting up test database instances.
 * 
 * @author andrewyates
 * @author $Author$
 * @version $Revision$
 */
public class DatabaseSupport {
  
  /**
   * Run to create the default in memory HSQLDB database
   */
  public static void createDatabase() throws Exception {
    DatabaseSupport.runStataments(new String[]{
        "CREATE MEMORY TABLE PERSON(FIRST_NAME CHAR(20),LAST_NAME CHAR(20))",
        "SET SCHEMA PUBLIC",
        "INSERT INTO PERSON VALUES('Andy','Yates')"
    });
  }
  
  /**
   * Run to destroy the database
   */
  public static void destroyDatabase() throws Exception {
    DatabaseSupport.runStataments(new String[]{
        "drop table person", 
        "shutdown"
    });
  }
  
  /**
   * Hardcoded to work against the default database instace given by
   * {@link Constants#DB_SYNONYM}
   */
  private static void runStataments(String[] statements) throws Exception {
    Connection conn = null;
    Statement st = null;
    try {
      conn = Connections.getConnection(Constants.DB_SYNONYM);
      st = conn.createStatement();
      for(int i=0; i<statements.length; i++) {
        st.execute(statements[i]);
      }
      conn.commit();
    }
    finally {
      DatabaseUtils.closeDbObject(st);
      DatabaseUtils.closeDbObject(conn);
      Pools.getInstance().destroyAllPools();
    }
  }
  
}
