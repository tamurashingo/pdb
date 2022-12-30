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

import com.github.tamurashingo.pdb.controller.BreakPointManager;
import com.sun.jdi.VirtualMachine;


/**
 * アプリケーション全体で使用する変数を管理するクラス。
 *
 * @author tamura shingo
 */
public class Globals {

    /*******/
    /**
     * デバッギのJVM
     */
    private static VirtualMachine targetVM;

    /**
     * ブレイクポイント管理
     */
    private static BreakPointManager breakpointManager;

    /**
     * ブレイクしたソース名
     */
    private static String breakSource;
    /**
     * ブレイクしたソース種別
     */
    private static String breakType;
    /**
     * ブレイクした行数
     */
    private static int breakLine;

    /**
     * 自マシンのIPアドレス
     */
    private static String ipaddress;
    /**
     * 変数展開を行うかどうか
     */
    private static boolean valExpand;


    /**
     * デバッギのJVMを設定する。
     *
     * @param targetVM JVM
     */
    public static void setVM(VirtualMachine targetVM) {
        Globals.targetVM = targetVM;
    }

    /**
     * デバッギのJVMを取得する。
     *
     * @return JVM
     */
    public static VirtualMachine getVM() {
        return targetVM;
    }

    /**
     * ブレイクポイント管理クラスを取得する。
     *
     * @return ブレイクポイント管理クラス
     */
    public static BreakPointManager getBreakPointManager() {
        return breakpointManager;
    }

    /**
     * ブレイクポイント管理クラスを設定する。
     *
     * @param breakpointManager ブレイクポイント管理クラス
     */
    public static void setBreakPointManager(BreakPointManager breakpointManager) {
        Globals.breakpointManager = breakpointManager;
    }

    /**
     * ブレイクしたソース名を取得する。
     *
     * @return ソース名
     */
    public static String getBreakSource() {
        return breakSource;
    }

    /**
     * ブレイクしたソース名を設定する。
     *
     * @param breakSource ソース名
     */
    public static void setBreakSource(String breakSource) {
        Globals.breakSource = breakSource;
    }

    /**
     * ブレイクしたソース種別を取得する。
     *
     * @return ソース種別
     */
    public static String getBreakType() {
        return breakType;
    }

    /**
     * ブレイクしたソース種別を設定する。
     *
     * @param breakType ソース種別
     */
    public static void setBreakType(String breakType) {
        Globals.breakType = breakType;
    }

    /**
     * ブレイクした行番号を取得する。
     *
     * @return 行番号
     */
    public static int getBreakLine() {
        return breakLine;
    }

    /**
     * ブレイクした行番号を設定する。
     *
     * @param breakLine 行番号
     */
    public static void setBreakLine(int breakLine) {
        Globals.breakLine = breakLine;
    }

    /**
     * ブレイク情報を初期化する。
     */
    public static void initBreakPoint() {
        Globals.breakSource = "";
        Globals.breakType = "";
        Globals.breakLine = -1;
    }

    /**
     * 自マシンのIPアドレスを設定する。
     *
     * @param ipaddress IPアドレス
     */
    public static void setIpaddress(String ipaddress) {
        Globals.ipaddress = ipaddress;
    }

    /**
     * 自マシンのIPアドレスを取得する。
     *
     * @return IPアドレス
     */
    public static String getIpaddress() {
        return ipaddress;
    }

    /**
     * 変数展開の有無を設定する。
     *
     * @param flag trueの場合、変数展開を行う
     */
    public static void setValExpand(boolean flag) {
        Globals.valExpand = flag;
    }

    /**
     * 変数展開の有無を取得する。
     *
     * @return trueの場合、変数展開を行う
     */
    public static boolean getValExpand() {
        return Globals.valExpand;
    }

}
