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

import java.util.List;

import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.bean.VariablesBean;
import com.github.tamurashingo.pdb.util.BackGroundProcess;


/**
 * GUI部向けのイベントを処理するインタフェース。
 * デバッガ部分などからGUIを変更する場合は、このインタフェース向けにイベントを飛ばす。
 *
 * @author tamura shingo
 */
public interface GUIRequestHandler {

    /**
     * ウィンドウ情報の初期化
     * @param handler コールバック用インスタンス
     */
    public void init(GUIEventHandler handler);

    /**
     * メインウィンドウを表示する。
     */
    public void showWindow();

    /**
     * メインウィンドウを非表示にする。
     */
    public void hideWindow();

    /**
     * 新しいソース（タブ）を開く
     */
    public void openNewSource(String sourceName, String sourceType, String source);

    /**
     * ソース（タブ）を閉じる
     */
    public void closeSource();

    /**
     * ツリーウィンドウを更新する。
     * @param list
     */
    public void updateTreeWindow(List<ProceduresBean> list);


    /**
     * データベースログイン用ダイアログを表示する。
     * @return ユーザid、パスワード、データベース名、サーバ、ポートを配列で返す。
     */
    public String[] login();

    /**
     * 処理中ダイアログを表示する。
     * @param title ダイアログのタイトル
     * @param message ダイアログメッセージ
     * @param proc ダイアログ表示中の処理
     */
    public void showProgressBar(String title, String message, BackGroundProcess proc);

    /**
     * ブレイクポイントの更新
     * @param sourceName
     * @param sourceType
     */
    public void updateBreakPoint(String sourceName, String sourceType);


    /**
     * 指定したソースを表示する。
     * 行が画面の中心となるように移動する。
     * @param sourceName ソース名
     * @param sourceType 種別
     * @param line 行
     */
    public void updateBreakPointSourceView(String sourceName, String sourceType, int line);

    public void updateVariableView(List<VariablesBean> list);

    public void updateSourceView();


    /**
     * メニューバーを初期状態にする。
     */
    public void menuInit();

    /**
     * メニューバーをDB接続後の状態にする。
     */
    public void menuDBConnect();

    /**
     * メニューバーをDB切断後の状態にする。
     */
    public void menuDBClose();

    /**
     * メニューバーをPL/SQL実行後の状態にする。
     */
    public void menuRun();

    /**
     * メニューバーをBREAK発生後の状態にする。
     */
    public void menuBreak();

    /**
     * メニューバーをRESUME発生後の状態にする。
     */
    public void menuResume();

    /**
     * メニューバーをSTEP発生後の状態にする。
     */
    public void menuStep();

    /**
     * メニューバーをSTEP OUT発生後の状態にする。
     */
    public void menuStepOut();

    /**
     * メニューバーをSTEP OVER発生後の状態にする。
     */
    public void menuStepOver();

    /**
     * メニューバーをPL/SQL終了後の状態にする。
     */
    public void menuRunEnd();

    /**
     * PL/SQLで発生した例外を表示する。
     * @param source ソース名
     * @param line 発生行
     * @param sqlerrm ORAエラーメッセージ
     */
    public void showProcedureException(String source, int line, String sqlerrm);

    /**
     * ソースツリーを更新する。
     */
    public void updateSourceTree();


    /**
     * タイトルバーを更新する。
     * タイトルバーは「アプリケーション名 + スキーマ名＠DB名」となっているので、その後ろに指定した文字列を付与する。
     * @param title
     */
    public void updateTitlebar(String title);

    /**
     * アバウト画面を表示する。
     */
    public void showAbout();

}
