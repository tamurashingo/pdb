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
package com.github.tamurashingo.pdb.db;

import java.sql.SQLException;

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.SystemConstants;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


public class DebugConnection {

    private static final Log log;

    private static final String connectSql =
            "begin dbms_Debug_jdwp.connect_tcp( ?, ? ); end;";

    static {
        log = Logger.getLogger();
    }

    private DBConnection db;

    public DebugConnection(DBConnection db) {
        this.db = db;
    }

    public void connectDebugger() throws DBException {
        log.trace("開始");

        try {
            db.prepare(connectSql);
            db.executeUpdate(new String[] {Globals.getIpaddress(), SystemConstants.config.getPort()});
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }

        log.trace("終了");
    }
}
