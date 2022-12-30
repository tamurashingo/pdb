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
package com.github.tamurashingo.pdb.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.db.ConnectConfig;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


/**
 * ブレイクポイントを管理する。
 * <p>
 * ここで管理しているブレイクポイントは、一覧を表示したり、ソースビューの左側にブレイク位置を表示するのに使用する。
 * あくまでアプリケーションが認識しているブレイクポイントであるため、
 * 実際にデバッギに設定されているブレイクポイントと違う場合がある。
 * </p>
 *
 * @author tamura shingo
 */
public class BreakPointManager {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    /** ブレイクポイント情報 */
    private List<BreakPointBean> list = new ArrayList<BreakPointBean>();

    /** どのクラスでどのブレイクポイントがあるか */
    private Map<String, List<BreakPointBean>> map = new HashMap<String, List<BreakPointBean>>();


    /**
     * ブレイクポイントを追加する。
     * @param bean ブレイクポイント情報
     */
    public void add(BreakPointBean bean) {
        log.trace("開始");
        list.add(bean);
        List<BreakPointBean> blist = map.get(bean.getClassName());
        if (blist == null) {
            blist = new ArrayList<BreakPointBean>();
            map.put(bean.getClassName(), blist);
        }
        blist.add(bean);
        log.trace("終了");
    }


    /**
     * ブレイクポイントを削除する。
     * @param bean ブレイクポイント情報
     */
    public void remove(BreakPointBean bean) {
        log.trace("開始");
        list.remove(bean);
        List<BreakPointBean> blist = map.get(bean.getClassName());
        if (blist != null) {
            blist.remove(bean);
        }
        log.trace("終了");
    }


    /**
     * 指定した箇所のブレイクポイント情報を取得する。
     * @param sourceName ソース名
     * @param sourceType ソース種別
     * @param line 行番号
     * @return ブレイクポイント情報。存在しない場合はnullを返す。
     */
    public BreakPointBean getAt(String sourceName, String sourceType, int line) {
        log.trace("開始");
        BreakPointBean bean = new BreakPointBean();
        bean.setSourceName(sourceName);
        bean.setType(sourceType);
        bean.setLine(line);

        int index = list.indexOf(bean);

        if (index != -1) {
            log.trace("終了");
            return list.get(index);
        }

        log.trace("終了");
        return null;
    }


    /**
     * 指定した箇所のブレイクポイントの有効／無効を切り替える。
     * <p>
     * 指定した位置が、内部で管理しているブレイクポイント情報に存在しない場合は、
     * 新たにブレイクポイント情報を作成する。
     * </p>
     * @param sourceName ソース名
     * @param sourceType ソース種別
     * @param line 行番号
     * @return ブレイクポイント情報
     */
    public BreakPointBean toggle(String sourceName, String sourceType, int line) {
        log.trace("開始");
        BreakPointBean bean = getAt(sourceName, sourceType, line);
        if (bean == null) {
            String className = getClassName(sourceName, sourceType);
            if (className == null) {
                log.info("%sはブレイクポイントを設定できません", sourceType);
                return null;
            }
            // ConnectConfigが保持しているユーザ名をスキーマと仮定する。
            bean = new BreakPointBean();
            bean.setSourceName(sourceName);
            bean.setType(sourceType);
            bean.setLine(line);
            bean.setClassName(className);
            add(bean);
        }

        boolean flag = bean.isValid() ? false : true;
        bean.setValid(flag);

        log.info("BreakPointManager:%s[%s]:%dを[%s]に設定しました。", sourceName, sourceType, line, flag ? "有効" : "無効");

        log.trace("終了");
        return bean;
    }

    /**
     * 指定したソース名、ソース種別のブレイクポイント情報一覧を取得する。
     * @param sourceName ソース名
     * @param sourceType ソース種別
     * @return ブレイクポイント情報のリスト
     */
    public List<BreakPointBean> getBreakPoint(String sourceName, String sourceType) {
        log.trace("開始");
        String className = getClassName(sourceName, sourceType);
        log.trace("終了");
        return map.get(className);
    }

    /**
     * 全ブレイクポイント情報を取得する。
     * @return ブレイクポイント情報のリスト
     */
    public List<BreakPointBean> getAllBreakPoints() {
        log.trace("開始");
        log.trace("終了");
        return list;
    }


    /**
     * ソース名、ソース種別より、Oracle内部で管理しているクラス名を生成する。
     * @param sourceName ソース名
     * @param sourceType ソース種別
     * @return クラス名
     */
    private String getClassName(String sourceName, String sourceType) {
        log.trace("開始");
        String type;
        if (sourceType.equalsIgnoreCase("PROCEDURE")) {
            type = "Procedure";
        }
        else if (sourceType.equalsIgnoreCase("PACKAGE")) {
            type = "Package";
        }
        else if (sourceType.equalsIgnoreCase("PACKAGE BODY")) {
            type = "PackageBody";
        }
        else if (sourceType.equalsIgnoreCase("FUNCTION")) {
            type = "Function";
        }
        else {
            log.debug("getClassNameエラー。種別が不明です:%s", sourceType);
            log.trace("終了");
            return null;
        }
        String userid = ConnectConfig.getInstance().getUserid().toUpperCase();
        StringBuilder buf = new StringBuilder();
        buf.append("$Oracle.");
        buf.append(type);
        buf.append(".");
        buf.append(userid);
        buf.append(".");
        buf.append(sourceName);

        log.debug("getClassName:%s", buf);
        log.trace("終了");
        return buf.toString();
    }
}
