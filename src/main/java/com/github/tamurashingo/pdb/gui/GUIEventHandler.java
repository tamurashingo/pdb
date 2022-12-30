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
package com.github.tamurashingo.pdb.gui;

import com.github.tamurashingo.pdb.controller.EventAnnotation;

/**
 * Viewからのイベントを処理する。
 *
 * @author tamura shingo
 */
public interface GUIEventHandler {

    public void processGuiEvent(GUIEventHandler.ID eventid, GUIEvent event);

//	/*-- ファイルメニュー --*/
//	/** DB接続 */
//	public static final int DBCONNECT = 10000;
//	/** DB切断 */
//	public static final int DBDISCONNECT = 10001;
//	/** デバッガ開始 */
//	public static final int DEBUGGERSTART = 10002;
//	/** デバッガ終了 */
//	public static final int DEBUGGERSTOP = 10003;
//	/** 終了 */
//	public static final int EXIT = 10004;
//
//	/*-- 設定メニュー --*/
//	/** ログレベル[なし] */
//	public static final int LOGLEVEL_NONE = 20001;
//	/** ログレベル[重要] */
//	public static final int LOGLEVEL_WARN = 20002;
//	/** ログレベル[通知] */
//	public static final int LOGLEVEL_INFO = 20003;
//	/** ログレベル[詳細] */
//	public static final int LOGLEVEL_DEBUG = 20004;
//	/** ログレベル[調査] */
//	public static final int LOGLEVEL_TRACE = 20005;
//	/** 設定 */
//	public static final int SETTING = 20006;
//	
//	/*-- 実行メニュー --*/
//	/** 実行 */
//	public static final int RUN = 30001;
//	/** 継続 */
//	public static final int RESUME = 30002;
//	/** ステップ */
//	public static final int STEP = 30003;
//	/** ステップオーバー */
//	public static final int STEPOVER = 30004;
//	/** ステップアウト */
//	public static final int STEPOUT = 30005;
//	/** トレース */
//	public static final int TRACE = 30006;
//	
//	/*-- ヘルプメニュー --*/
//	/** ヘルプ */
//	public static final int HELP = 40001;
//	/** アバウト */
//	public static final int ABOUT = 40002;
//	
//	
//	/*-- ウィンドウイベント --*/
//	/** 閉じるアクション */
//	public static final int WINDOW_CLOSE = 50001;
//	/** ソース選択アクション */
//	public static final int SELECT_SOURCE = 50002;
//	/** ブレイクポイントトグルアクション */
//	public static final int SELECT_BREAKPOINT = 50003;


    public enum ID {

        /** DB接続 */
        @EventAnnotation("DB接続")
        DBCONNECT,
        /** DB切断 */
        @EventAnnotation("DB切断")
        DBDISCONNECT,
        /** デバッガ開始 */
        @EventAnnotation("デバッガ開始")
        DEBUGGERSTART,
        /** デバッガ終了 */
        @EventAnnotation("デバッガ終了")
        DEBUGGERSTOP,
        /** 終了 */
        @EventAnnotation("終了")
        EXIT,
        /*-- 設定メニュー --*/
        /** ログレベル[なし] */
        @EventAnnotation("ログレベル[なし]")
        LOGLEVEL_NONE,
        /** ログレベル[重要] */
        @EventAnnotation("ログレベル[重要]")
        LOGLEVEL_WARN,
        /** ログレベル[通知] */
        @EventAnnotation("ログレベル[通知]")
        LOGLEVEL_INFO,
        /** ログレベル[詳細] */
        @EventAnnotation("ログレベル[詳細]")
        LOGLEVEL_DEBUG,
        /** ログレベル[調査] */
        @EventAnnotation("ログレベル[調査]")
        LOGLEVEL_TRACE,
        /** 変数展開 */
        @EventAnnotation("変数展開")
        VARIABLE,

        /*-- 実行メニュー --*/
        /** 実行 */
        @EventAnnotation("実行")
        RUN,
        /** 継続 */
        @EventAnnotation("継続")
        RESUME,
        /** ステップ */
        @EventAnnotation("ステップ")
        STEP,
        /** ステップオーバー */
        @EventAnnotation("ステップオーバー")
        STEPOVER,
        /** ステップアウト */
        @EventAnnotation("ステップアウト")
        STEPOUT,
        /** トレース */
        @EventAnnotation("トレース")
        TRACE,

        /*-- ヘルプメニュー --*/
        /** ヘルプ */
        @EventAnnotation("ヘルプ")
        HELP,
        /** アバウト */
        @EventAnnotation("アバウト")
        ABOUT,

        /*-- ウィンドウイベント --*/
        /** 閉じるアクション */
        @EventAnnotation("閉じる")
        WINDOW_CLOSE,
        /** ソース選択アクション */
        @EventAnnotation("ソース選択")
        SELECT_SOURCE,
        /** ブレイクポイント設定アクション */
        @EventAnnotation("ブレイクポイント設定")
        SELECT_BREAKPOINT,


        /** 再コンパイル */
        @EventAnnotation("再コンパイル")
        COMPILE_SOURCE,

        /** ソースツリーの更新 */
        @EventAnnotation("ソースツリー更新")
        UPDATE_SOURCETREE,
    }
}
