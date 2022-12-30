/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2010 tamura shingo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.tamurashingo.pdb.db.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.github.tamurashingo.pdb.db.DBConnection;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

public class DbmsGetLinesDAO {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private DBConnection db;

    private static final String sql = "BEGIN DBMS_OUTPUT.GET_LINE( ?, ? ); END;";


    public DbmsGetLinesDAO(DBConnection db) {
        this.db = db;
    }

    public List<String> getOutput() throws DBException {
        log.trace("開始");

        Connection conn = db.getNativeConnection();
        List<String> list = new LinkedList<String>();
        CallableStatement stmt = null;
        try {
            stmt = conn.prepareCall(sql);
            boolean hasMore = true;
            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            stmt.registerOutParameter(2, java.sql.Types.INTEGER);

            while (hasMore) {

                stmt.execute();
                hasMore = stmt.getInt(2) == 0;
                if (hasMore) {
                    String str = stmt.getString(1);
                    log.debug("DBMS_OUTPUT:%s", str);
                    list.add(str);
                }
            }

        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException ex) {
                    // クローズ時のSQLExcetpionは何もしない
                }
            }
        }

        log.trace("終了");
        return list;
    }
}
