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

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.github.tamurashingo.pdb.util.IconResource;

/**
 * アプリケーション全体で使用する定数を管理するクラス。
 *
 * @author tamura shingo
 */
public class SystemConstants {

    /**
     * メインウィンドウの幅
     */
    public static final int MAINWINDOW_WIDTH = 800;
    /**
     * メインウィンドウの高さ
     */
    public static final int MAINWINDOW_HEIGHT = 600;

    /**
     * ソース一覧ウィンドウの幅
     */
    public static final int TREEWINDOW_WIDTH = 250;
    /**
     * ソース一覧ウィンドウの高さ
     */
    public static final int TREEWINDOW_HEIGHT = 600;

    /**
     * ソースウィンドウの幅
     */
    public static final int SOURCEWINDOW_WIDTH = 550;
    /**
     * ソースウィンドウの高さ
     */
    public static final int SOURCEWINDOW_HEIGHT = 400;

    /**
     * パラメータダイアログの幅
     */
    public static final int PARAMDIALOG_WIDTH = 400;
    /**
     * パラメータダイアログの高さ
     */
    public static final int PARAMDIALOG_HEIGHT = 300;

    /**
     * アプリケーション名
     */
    public static final String APP_NAME = "PLSQLデバッガ";
    /**
     * バージョン
     */
    public static final String APP_VERSION = "0.1";
    /**
     * アプリ名+バージョン（起動時に設定）
     */
    public static final String APP_FULL_NAME;

    /**
     * 設定ファイル名
     */
    public static final String CONFIG_FILE_NAME = "pdbconf.xml";

    /**
     * 設定ファイル情報クラス（起動時に設定
     */
    public static final Config config;

    /**
     * 改行文字
     */
    public static final String NEWLINE;

    /**
     * クラスローダ
     */
    public static final ClassLoader classLoader;

    /**
     * アイコン
     */
    public static final List<Image> icon;

    /**
     * ロゴ
     */
    public static final IconResource logo;
    /**
     * ロゴのアドレス
     */
    public static final URL logoURL;

    static {
        classLoader = SystemConstants.class.getClassLoader();
        APP_FULL_NAME = APP_NAME + " " + APP_VERSION;
        NEWLINE = System.getProperty("line.separator");
        config = new Config();

        icon = new ArrayList<Image>();
        icon.add(Toolkit.getDefaultToolkit().createImage(classLoader.getResource("images/icon_016.png")));
        icon.add(Toolkit.getDefaultToolkit().createImage(classLoader.getResource("images/icon_032.png")));
        icon.add(Toolkit.getDefaultToolkit().createImage(classLoader.getResource("images/icon_048.png")));
        icon.add(Toolkit.getDefaultToolkit().createImage(classLoader.getResource("images/icon_128.png")));

        logo = new IconResource();
        logoURL = classLoader.getResource("images/logo.png");
    }
}
