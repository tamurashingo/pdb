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
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.db.DBConnection;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

import static com.github.tamurashingo.pdb.util.Util.nullToBlank;

public class UserArguments {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private DBConnection db;

    /** パッケージ用SQL */
    private static final String sql =
            "select "
                    + "  argument_name, "
                    + "  data_level, "
                    + "  data_type, "
                    + "  in_out, "
                    + "  position, "
                    + "  type_name, "
                    + "  type_subname "
                    + "from "
                    + "  user_arguments "
                    + "where "
                    + "  package_name = ? "
                    + "and "
                    + "  object_name = ? "
                    + "order by "
                    + "  sequence ";

    /** プロシージャ・ファンクション用SQL */
    private static final String sqlProcFunc =
            "select "
                    + "  argument_name, "
                    + "  data_level, "
                    + "  data_type, "
                    + "  in_out, "
                    + "  position, "
                    + "  type_name, "
                    + "  type_subname "
                    + "from "
                    + "  user_arguments "
                    + "where "
                    + "  package_name is null "
                    + "and "
                    + "  object_name = ? "
                    + "order by "
                    + "  sequence ";


    public UserArguments(DBConnection db) {
        this.db = db;
    }

    public List<ParamsBean> getParameters(String objectName) throws DBException {
        log.trace("開始");

        log.debug("SQL  :%s", sqlProcFunc);
        log.debug("PARAM:%s", objectName);

        try {
            db.prepare(sqlProcFunc);
            List<Map<String, String>> result = db.executeQuery(new String[] {objectName});

            log.trace("終了");
            return getParameters(result);
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    public List<ParamsBean> getParameters(String packageName, String objectName) throws DBException {
        log.trace("開始");

        if (packageName == null || packageName.equals("")) {
            return getParameters(objectName);
        }

        log.debug("SQL  :%s", sql);
        log.debug("PARAM:%s,%s", packageName, objectName);

        try {
            db.prepare(sql);
            List<Map<String, String>> result = db.executeQuery(new String[] {packageName, objectName});

            log.trace("終了");
            return getParameters(result);
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    private List<ParamsBean> getParameters(List<Map<String, String>> result) throws SQLException {
        log.trace("開始");

        List<ParamsBean> ret = new ArrayList<ParamsBean>();
        for (Map<String, String> map : result) {
            ParamsBean bean = new ParamsBean();
            bean.setArgumentName(nullToBlank(map.get("ARGUMENT_NAME")));
            bean.setDataLevel(Integer.valueOf(map.get("DATA_LEVEL")));
            bean.setDataType(nullToBlank(map.get("DATA_TYPE")));
            bean.setInOut(nullToBlank(map.get("IN_OUT")));
            bean.setPosition(Integer.valueOf(map.get("POSITION")));
            bean.setTypeName(nullToBlank(map.get("TYPE_NAME")));
            bean.setTypeSubname(nullToBlank(map.get("TYPE_SUBNAME")));
            ret.add(bean);
        }

        log.trace("終了");
        return ret;
    }
}
