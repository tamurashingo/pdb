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
import java.sql.SQLException;
import java.util.List;

import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.db.dao.DbmsGetLinesDAO;
import com.github.tamurashingo.pdb.db.dao.UserArguments;
import com.github.tamurashingo.pdb.db.dao.UserObjectsDAO;
import com.github.tamurashingo.pdb.db.dao.UserSourceDAO;
import com.github.tamurashingo.pdb.util.Pair;


public class DBManager {

    private static final DBManager inst;

    static {
        inst = new DBManager();
    }

    private DBConnection db;

    private DBConnection procDb;

    public static DBManager getInstance() {
        return inst;
    }

    public synchronized void connect(String userid, String password, String dbname, String server, String port) throws DBException {
        ConnectConfig config = ConnectConfig.getInstance();
        config.init(userid, password, server, port, dbname);
        connect();
    }

    public synchronized void connect() throws DBException {
        close();
        try {
            db = new DBConnection();
            procDb = new DBConnection();
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    public synchronized void close() {
        if (db != null) {
            db.close();
        }
        db = null;
        if (procDb != null) {
            procDb.close();
        }
        procDb = null;

    }


    public List<ProceduresBean> getProceduresForTree() throws DBException {
        UserObjectsDAO dao = new UserObjectsDAO(db);
        return dao.getProceduresForTree();
    }

    public List<Pair<String, String>> getProceduresForRun() throws DBException {
        UserObjectsDAO dao = new UserObjectsDAO(db);
        return dao.getProceduresForRun();
    }

    public List<String> getMethods(String objectName, String objectType) throws DBException {
        UserObjectsDAO dao = new UserObjectsDAO(db);
        return dao.getMethods(objectName, objectType);
    }

    public String getSource(String objectName, String objectType) throws DBException {
        UserSourceDAO dao = new UserSourceDAO(db);
        return dao.getSource(objectName, objectType);
    }

    public List<ParamsBean> getParameters(String packageName, String objectName) throws DBException {
        UserArguments dao = new UserArguments(db);
        return dao.getParameters(packageName, objectName);
    }

    public void connectDebugger() throws DBException {
        DebugConnection dao = new DebugConnection(procDb);
        dao.connectDebugger();
    }

    public void prepareOutput() throws DBException {
        String sql = "BEGIN DBMS_OUTPUT.ENABLE(NULL); END;";
        try {
            procDb.prepare(sql);
            procDb.executeUpdate(new String[] {});
        }
        catch (SQLException ex) {
            throw new DBException("DBMS_OUTPUT.ENABLEの発行に失敗しました。", ex);
        }
    }

    public List<String> getOutput() throws DBException {
        DbmsGetLinesDAO dao = new DbmsGetLinesDAO(procDb);
        return dao.getOutput();
    }

    public void test() {
        try {
            String sql = "begin hello_world; end;";
            db.prepare(sql);
            db.executeUpdate(new String[] {});
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getNativeConnection() {
        return procDb.getNativeConnection();
    }

    public void compieSouece(String sourceName, String sourceType, boolean isDebug) throws DBException {
        String sql = String.format("ALTER %s %s COMPILE %s", sourceType, sourceName, isDebug ? "DEBUG" : "");
        try {
            procDb.prepare(sql);
            procDb.executeUpdate(new String[] {});
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }


    private DBManager() {
    }
}
