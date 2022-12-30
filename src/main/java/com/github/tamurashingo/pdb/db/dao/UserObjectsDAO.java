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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.db.DBConnection;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.Pair;


public class UserObjectsDAO {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    DBConnection db;

    private static final String getProcedureSqlForView =
            "select "
                    + "  distinct "
                    + "  obj.object_name, "
                    + "  obj.object_type, "
                    + "  obj.status, "
                    + "  pls.plsql_debug "
                    + "from "
                    + "  user_procedures pro, "
                    + "  user_objects obj, "
                    + "  user_plsql_object_settings pls "
                    + "where "
                    + "  pro.object_name = obj.object_name "
                    + "and "
                    + "  obj.object_name = pls.name(+) "
                    + "and "
                    + "  obj.object_type = pls.type(+)"
                    + "order by "
                    + "  obj.object_type, "
                    + "  obj.object_name ";

    private static final String getProcedureSqlForRun =
            "select "
                    + "  distinct object_name, "
                    + "           object_type "
                    + "from "
                    + "  user_procedures "
                    + "order by"
                    + "  object_name ";

    private static final String getMethodSql =
            "select "
                    + "  nvl(procedure_name, object_name) as procedure_name "
                    + "from "
                    + "  user_procedures "
                    + "where "
                    + "  object_name = ? "
                    + "and "
                    + "  object_type = ? "
                    + "and "
                    + "  subprogram_id != '0' "
                    + "order by "
                    + "  subprogram_id ";


    public UserObjectsDAO(DBConnection db) {
        this.db = db;
    }

    /**
     * プロシージャ一覧（ツリー用）の取得
     * @return
     * @throws DBException
     */
    public List<ProceduresBean> getProceduresForTree() throws DBException {
        log.trace("開始");

        log.debug("SQL  :%s", getProcedureSqlForView);
        log.debug("PARAM:なし");
        try {
            db.prepare(getProcedureSqlForView);
            List<Map<String, String>> result = db.executeQuery(new String[] {});
            List<ProceduresBean> ret = new ArrayList<ProceduresBean>();

            for (Map<String, String> map : result) {
                ProceduresBean bean = new ProceduresBean();
                bean.setObjectName(map.get("OBJECT_NAME"));
                bean.setObjectType(map.get("OBJECT_TYPE"));
                bean.setValid(map.get("STATUS").equals("VALID"));
                bean.setDebuggable(map.get("PLSQL_DEBUG").equals("TRUE"));
                ret.add(bean);
            }

            log.trace("終了");
            return ret;
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    /**
     * プロシージャ一覧（実行用）の取得
     * @return
     * @throws DBException
     */
    public List<Pair<String, String>> getProceduresForRun() throws DBException {
        log.trace("開始");

        log.debug("SQL  :%s", getProcedureSqlForRun);
        log.debug("PARAM:なし");
        try {
            db.prepare(getProcedureSqlForRun);
            List<Map<String, String>> result = db.executeQuery(new String[] {});
            List<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();

            for (Iterator<Map<String, String>> it = result.iterator(); it.hasNext(); ) {
                Map<String, String> map = it.next();
                Pair<String, String> pair = new Pair<String, String>(map.get("OBJECT_NAME"), map.get("OBJECT_TYPE"));
                ret.add(pair);
            }

            log.trace("終了");
            return ret;
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }


    /**
     * プロシージャのメソッドの取得
     */
    public List<String> getMethods(String objectName, String objectType) throws DBException {
        log.trace("開始");

        log.debug("SQL  :%s", getMethodSql);
        log.debug("PARAM:%s,%s", objectName, objectType);
        try {
            db.prepare(getMethodSql);
            List<Map<String, String>> result = db.executeQuery(new String[] {objectName, objectType});
            List<String> ret = new ArrayList<String>();
            for (Iterator<Map<String, String>> it = result.iterator(); it.hasNext(); ) {
                Map<String, String> map = it.next();
                ret.add(map.get("PROCEDURE_NAME"));
            }

            log.trace("終了");
            return ret;
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }
}
