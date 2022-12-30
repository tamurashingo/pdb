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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.db.DBConnection;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


public class UserSourceDAO {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private DBConnection db;

    private static final String sourceSql =
            "select "
                    + "  text "
                    + "from "
                    + "  user_source "
                    + "where "
                    + "  name = ? "
                    + "and "
                    + " type = ? "
                    + "order by "
                    + "  line ";

    public UserSourceDAO(DBConnection db) {
        this.db = db;
    }

    public String getSource(String objectName, String objectType) throws DBException {
        log.trace("開始");

        try {
            StringBuilder buf = new StringBuilder();
            db.prepare(sourceSql);
            List<Map<String, String>> result = db.executeQuery(new String[] {objectName, objectType});

            for (Iterator<Map<String, String>> it = result.iterator(); it.hasNext(); ) {
                Map<String, String> map = it.next();
                buf.append(map.get("TEXT"));
            }

            log.trace("終了");
            return buf.toString();

        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }
}
