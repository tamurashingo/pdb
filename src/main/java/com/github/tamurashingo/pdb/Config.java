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
package com.github.tamurashingo.pdb;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.github.tamurashingo.pdb.bean.DBConnectInfoBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 設定ファイルの情報を保持するBeanクラス。
 * <p>
 * XML形式の設定ファイルから情報を読み込み、その情報を保持する。
 * 設定ファイルへの書き戻しは行わない。
 * </p>
 *
 * @author tamura shingo
 */
public class Config {

    /**
     * ログレベルマッピング用
     */
    private static final Map<String, Log.Level> map;

    /**
     * 設定ファイルから読み込んだログレベル
     */
    private Log.Level level;
    /**
     * 設定ファイルから読み込んだログクラス名
     */
    private String logInstanceName;
    /**
     * 設定ファイルから読み込んだポート番号
     */
    private String port;

    /**
     * 設定ファイルから読み込んだコネクタ情報
     */
    private String debugConnector;

    /**
     * 設定ファイルから読み込んだDB接続情報
     */
    private Map<String, DBConnectInfoBean> connectInfo;


    private static final Log log;

    static {
        map = new HashMap<String, Log.Level>();
        map.put("NONE", Log.Level.NONE);
        map.put("WARN", Log.Level.WARN);
        map.put("INFO", Log.Level.INFO);
        map.put("DEBUG", Log.Level.DEBUG);
        map.put("TRACE", Log.Level.TRACE);

        log = Logger.getLogger();
    }

    /**
     * コンストラクタ
     */
    public Config() {
        connectInfo = new HashMap<String, DBConnectInfoBean>();
        connectInfo.put("", new DBConnectInfoBean());
    }


    /**
     * 設定ファイルから読み込んだログレベルを設定する。
     *
     * @param logLevel ログレベル名
     */
    private void setLogLevel(String logLevel) {
        log.trace("開始");
        level = map.get(logLevel);
        log.trace("終了");
    }

    /**
     * 設定ファイルから読み込んだログレベルを取得する。
     *
     * @return ログレベル
     */
    public Log.Level getLogLevel() {
        log.trace("開始");
        log.trace("終了");
        return level;
    }

    /**
     * 設定ファイルから読み込んだログクラス名を取得する。
     *
     * @return ログクラス名
     */
    public String getLogInstanceName() {
        log.trace("開始");
        log.trace("終了");
        return logInstanceName;
    }

    /**
     * 設定ファイルから読み込んだログクラス名を設定する。
     *
     * @param logInstanceName ログクラス名
     */
    private void setLogInstanceName(String logInstanceName) {
        log.trace("開始");
        this.logInstanceName = logInstanceName;
        log.trace("終了");
    }

    /**
     * DB接続情報のマップを取得する。
     *
     * @return DB接続情報のマップ
     */
    public Map<String, DBConnectInfoBean> getConnectInfo() {
        log.trace("開始");
        log.trace("終了");
        return connectInfo;
    }

    /**
     * 設定ファイルから取得したデバッグ用ポート番号を取得する。
     *
     * @return デバッグ用ポート番号
     */
    public String getPort() {
        log.trace("開始");
        log.trace("終了");
        return port;
    }

    /**
     * 設定ファイルから取得したデバッグ用ポート番号を設定する。
     *
     * @param port デバッグ用ポート番号
     */
    private void setPort(String port) {
        log.trace("開始");
        this.port = port;
        log.trace("終了");
    }

    /**
     * 設定ファイルから読み込んだデバッグコネクタ名を取得する。
     *
     * @return デバッグコネクタ名
     */
    public String getDebugConnector() {
        log.trace("開始");
        log.trace("終了");
        return debugConnector;
    }

    /**
     * 設定ファイルから読み込んだデバッグコネクタ名を設定する。
     *
     * @param debugConnector デバッグコネクタ名
     */
    private void setDebugConnector(String debugConnector) {
        log.trace("開始");
        this.debugConnector = debugConnector;
        log.trace("終了");
    }

    /**
     * XML情報からログクラス名を取得する
     *
     * @param xpath
     * @param doc
     * @return ログクラス名
     * @throws XPathExpressionException
     */
    private String readLogInstance(XPath xpath, Document doc) throws XPathExpressionException {
        log.trace("開始");
        String logInstance = (String) xpath.evaluate("/pdb/log/instance", doc, XPathConstants.STRING);
        log.trace("終了");
        return logInstance;
    }

    /**
     * XML情報からログレベルを取得する。
     *
     * @param xpath
     * @param doc
     * @return ログレベル
     * @throws XPathExpressionException
     */
    private String readLogLevel(XPath xpath, Document doc) throws XPathExpressionException {
        log.trace("開始");
        String logLevel = (String) xpath.evaluate("/pdb/log/level", doc, XPathConstants.STRING);
        log.trace("終了");
        return logLevel;
    }

    /**
     * XML情報からデバッグ用ポート番号を取得する。
     *
     * @param xpath
     * @param doc
     * @return デバッグ用ポート番号
     * @throws XPathExpressionException
     */
    private String readPort(XPath xpath, Document doc) throws XPathExpressionException {
        log.trace("開始");
        String port = (String) xpath.evaluate("/pdb/dbg/port", doc, XPathConstants.STRING);
        log.trace("終了");
        return port;
    }

    /**
     * XML情報からデバッグコネクタ名を取得する。
     *
     * @param xpath
     * @param doc
     * @return デバッグコネクタ名
     * @throws XPathExpressionException
     */
    private String readDebugConnector(XPath xpath, Document doc) throws XPathExpressionException {
        log.trace("開始");
        String debugConnector = (String) xpath.evaluate("/pdb/dbg/connector", doc, XPathConstants.STRING);
        log.trace("終了");
        return debugConnector;
    }

    private void readConnectInfo(XPath xpath, Document doc) throws XPathExpressionException {
        log.trace("開始");
        NodeList nodes = (NodeList) xpath.evaluate("/pdb/login", doc, XPathConstants.NODESET);
        for (int ix = 0; ix < nodes.getLength(); ix++) {
            DBConnectInfoBean bean = parseConnectInfo(xpath, nodes.item(ix));
            String label = bean.getLabel();
            if (label.isEmpty()) {
                label = String.format("No.%d", ix + 1);
            }
            connectInfo.put(label, bean);
        }
        log.trace("終了");

    }

    private DBConnectInfoBean parseConnectInfo(XPath xpath, Node node) throws XPathExpressionException {
        log.trace("開始");
        String label = (String) xpath.evaluate("label", node, XPathConstants.STRING);
        String username = (String) xpath.evaluate("username", node, XPathConstants.STRING);
        String password = (String) xpath.evaluate("password", node, XPathConstants.STRING);
        String dbname = (String) xpath.evaluate("dbname", node, XPathConstants.STRING);
        String server = (String) xpath.evaluate("server", node, XPathConstants.STRING);
        String port = (String) xpath.evaluate("port", node, XPathConstants.STRING);
        log.debug("label:%s,ユーザ名:%s,パスワード:%s,DB:%s,サーバ:%s,ポート:%s", label, username, password, dbname, server, port);

        DBConnectInfoBean bean = new DBConnectInfoBean();
        bean.setLabel(label);
        bean.setUsername(username);
        bean.setPassword(password);
        bean.setDbname(dbname);
        bean.setServer(server);
        bean.setPort(port);

        log.trace("終了");
        return bean;
    }


    /**
     * XML形式の設定ファイルから設定情報を読み込む。
     *
     * @param filename 設定ファイル名
     * @throws IOException 設定ファイル読み込みエラー、XMLパースエラーが発生した場合
     */
    public void readConfig(String filename) throws IOException {
        log.trace("開始");
        FileInputStream in = null;
        try {
            in = new FileInputStream(filename);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String logInstance = readLogInstance(xpath, doc);
            setLogInstanceName(logInstance);
            log.info("ログインスタンス名:%s", logInstance);

            String level = readLogLevel(xpath, doc);
            setLogLevel(level);
            log.info("ログレベル:%s", level);

            String port = readPort(xpath, doc);
            setPort(port);
            log.info("ポート番号:%s", port);

            String debugConnector = readDebugConnector(xpath, doc);
            setDebugConnector(debugConnector);
            log.info("デバッグコネクタ:%s", debugConnector);

            readConnectInfo(xpath, doc);
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        } catch (SAXException ex) {
            throw new IOException(ex);
        } catch (XPathExpressionException ex) {
            throw new IOException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) { /* クローズ時のIOExceptionは無視する */ }
            }

        }
        log.trace("終了");
    }
}
