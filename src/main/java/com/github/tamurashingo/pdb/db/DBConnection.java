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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBConnection {

    private Connection conn = null;

    private PreparedStatement ps = null;

    private ResultSet rs = null;

    public DBConnection() throws SQLException {
        ConnectConfig conf = ConnectConfig.getInstance();
        StringBuilder buf = new StringBuilder();

        buf.append("jdbc:oracle:thin:@");
        buf.append(conf.getServer());
        buf.append(":");
        buf.append(conf.getPort());
        buf.append(":");
        buf.append(conf.getDbname());

        conn = DriverManager.getConnection(buf.toString(), conf.getUserid(), conf.getPassword());
    }

    public void prepare(String sql) throws SQLException {
        if (ps != null) {
            try {
                ps.close();
            }
            catch (SQLException ex) {
                // クローズ時のSQLExceptionは何も行わない。
            }
            ps = null;
        }
        ps = conn.prepareStatement(sql);
    }

    public List<Map<String, String>> executeQuery(String[] params) throws SQLException {
        for (int ix = 0; ix < params.length; ix++) {
            if (params[ix] != null) {
                ps.setString(ix + 1, params[ix]);
            }
            else {
                ps.setNull(ix + 1, java.sql.Types.VARCHAR);
            }
        }

        rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnSize = rsmd.getColumnCount();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        while (rs.next()) {
            Map<String, String> map = new HashMap<String, String>();
            for (int ix = 1; ix <= columnSize; ix++) {
                String val = rs.getString(ix);
                map.put(rsmd.getColumnName(ix), val);
            }
            list.add(map);
        }

        try {
            rs.close();
        }
        catch (SQLException ex) {
            // クローズ時のSQLExceptionは何も行わない。
        }
        rs = null;

        return list;
    }

    public int executeUpdate(String[] params) throws SQLException {
        for (int ix = 0; ix < params.length; ix++) {
            ps.setString(ix + 1, params[ix]);
        }
        int ret = ps.executeUpdate();
        return ret;
    }

    public void close() {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException ex) {
            // クローズ時のSQLExceptionは何も行わない
        }
        rs = null;
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException ex) {
            // クローズ時のSQLExceptionは何も行わない
        }
        ps = null;
    }

    public void closeAll() {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException ex) {}
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException ex) {}
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException ex) {
            // クローズ時のSQLExceptionは何も行わない
        }
    }

    public Connection getNativeConnection() {
        return conn;
    }
}
