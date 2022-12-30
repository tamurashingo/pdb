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
package com.github.tamurashingo.pdb.gui.parts;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;

import com.github.tamurashingo.pdb.SystemConstants;
import com.github.tamurashingo.pdb.gui.GUIEvent;
import com.github.tamurashingo.pdb.gui.GUIEventHandler;
import com.github.tamurashingo.pdb.gui.GUIEventValueExpand;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;


/**
 * メニューバーの作成・管理を行うクラス。
 *
 * @author tamura shingo
 */
public class PdbMenuBar {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private final GUIEventHandler eventHandler;

    /** DB接続アクション */
    private MenuAction dbConnectAction;

    /** DB切断アクション */
    private MenuAction dbDisconnectAction;

    /** 終了アクション */
    private MenuAction exitAction;


    /** 実行アクション */
    private MenuAction runAction;

    /** RESUMEアクション */
    private MenuAction resumeAction;

    /** STEP実行アクション */
    private MenuAction stepAction;

    /** STEPオーバーアクション */
    private MenuAction stepOverAction;

    /** STEPアウトアクション */
    private MenuAction stepOutAction;

    /** トレースアクション */
    private MenuAction traceAction;

    /** ログ：なしアクション */
    private MenuAction logNoneAction;

    /** ログ：重要アクション */
    private MenuAction logWarnAction;

    /** ログ：通知アクション */
    private MenuAction logInfoAction;

    /** ログ：詳細アクション */
    private MenuAction logDebugAction;

    /** ログ：調査アクション */
    private MenuAction logTraceAction;

    /** 変数展開アクション */
    private CheckAction valExpandAction;


    /** ヘルプアクション */
    private MenuAction helpAction;

    /** アバウトアクション */
    private MenuAction aboutAction;


    /**
     * コンストラクタ
     * @param eventHandler イベント通知先
     */
    public PdbMenuBar(GUIEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * メニューバーを作成する。
     * @return メニューバー
     */
    public JMenuBar makeMenu() {
        log.trace("開始");

        createActions();

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("ファイル");
        file.add(dbConnectAction);
        file.add(dbDisconnectAction);
        file.addSeparator();
        file.add(exitAction);

        JMenu run = new JMenu("実行");
        run.add(runAction);
        run.add(resumeAction);
        run.addSeparator();
        run.add(stepAction);
        run.add(stepOverAction);
        run.add(stepOutAction);
        run.addSeparator();
        run.add(traceAction);

        JMenu tool = new JMenu("ツール");
        JRadioButtonMenuItem logNone = new JRadioButtonMenuItem(logNoneAction);
        JRadioButtonMenuItem logWarn = new JRadioButtonMenuItem(logWarnAction);
        JRadioButtonMenuItem logInfo = new JRadioButtonMenuItem(logInfoAction);
        JRadioButtonMenuItem logDebug = new JRadioButtonMenuItem(logDebugAction);
        JRadioButtonMenuItem logTrace = new JRadioButtonMenuItem(logTraceAction);
        JCheckBoxMenuItem valExpand = new JCheckBoxMenuItem(valExpandAction);
        valExpandAction.setCheckBox(valExpand);
        tool.add(logNone);
        tool.add(logWarn);
        tool.add(logInfo);
        tool.add(logDebug);
        tool.add(logTrace);
        tool.addSeparator();
        tool.add(valExpand);

        ButtonGroup logGroup = new ButtonGroup();
        logGroup.add(logNone);
        logGroup.add(logWarn);
        logGroup.add(logInfo);
        logGroup.add(logDebug);
        logGroup.add(logTrace);

        Level level = SystemConstants.config.getLogLevel();
        switch (level) {
            case NONE:
                logNone.setSelected(true);
                break;
            case WARN:
                logWarn.setSelected(true);
                break;
            case INFO:
                logInfo.setSelected(true);
                break;
            case DEBUG:
                logDebug.setSelected(true);
                break;
            case TRACE:
                logTrace.setSelected(true);
                break;
            default:
                logNone.setSelected(true);
                break;
        }

        JMenu help = new JMenu("ヘルプ");
        help.add(helpAction);
        help.add(aboutAction);


        menuBar.add(file);
        menuBar.add(run);
        menuBar.add(tool);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(help);

        log.trace("終了");
        return menuBar;
    }


    private void createActions() {
        log.trace("開始");

        dbConnectAction = new MenuAction("DB接続", GUIEventHandler.ID.DBCONNECT);
        dbDisconnectAction = new MenuAction("DB切断", GUIEventHandler.ID.DBDISCONNECT);
        exitAction = new MenuAction("終了", GUIEventHandler.ID.EXIT);

        runAction = new MenuAction("実行", GUIEventHandler.ID.RUN);
        resumeAction = new MenuAction("継続", GUIEventHandler.ID.RESUME);
        stepAction = new MenuAction("step in", GUIEventHandler.ID.STEP);
        stepOverAction = new MenuAction("step over", GUIEventHandler.ID.STEPOVER);
        stepOutAction = new MenuAction("step out", GUIEventHandler.ID.STEPOUT);
        traceAction = new MenuAction("TRACE", GUIEventHandler.ID.TRACE);

        logNoneAction = new MenuAction("ログレベル[なし]", GUIEventHandler.ID.LOGLEVEL_NONE);
        logWarnAction = new MenuAction("ログレベル[警告]", GUIEventHandler.ID.LOGLEVEL_WARN);
        logInfoAction = new MenuAction("ログレベル[情報]", GUIEventHandler.ID.LOGLEVEL_INFO);
        logDebugAction = new MenuAction("ログレベル[詳細]", GUIEventHandler.ID.LOGLEVEL_DEBUG);
        logTraceAction = new MenuAction("ログレベル[調査]", GUIEventHandler.ID.LOGLEVEL_TRACE);
        valExpandAction = new CheckAction("変数展開", GUIEventHandler.ID.VARIABLE);

        helpAction = new MenuAction("ヘルプ", GUIEventHandler.ID.HELP);
        aboutAction = new MenuAction("アバウト", GUIEventHandler.ID.ABOUT);

        log.trace("終了");
    }


    /**
     * ツールバーを作成する
     * @return ツールバー
     */
    public JToolBar makeToolBar() {
        log.trace("開始");

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        toolbar.add(dbConnectAction);
        toolbar.add(dbDisconnectAction);
        toolbar.addSeparator();
        toolbar.add(runAction);
        toolbar.add(resumeAction);
        toolbar.addSeparator();
        toolbar.add(stepAction);
        toolbar.add(stepOverAction);
        toolbar.add(stepOutAction);

        log.trace("終了");
        return toolbar;
    }


    /**
     * メニューバー・ツールバーを初期状態にする。
     */
    public void init() {
        log.trace("開始");

        dbConnectAction.setEnabled(true);
        dbDisconnectAction.setEnabled(false);
        exitAction.setEnabled(true);

        runAction.setEnabled(false);
        resumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        stepOverAction.setEnabled(false);
        stepOutAction.setEnabled(false);

        logNoneAction.setEnabled(true);
        logWarnAction.setEnabled(true);
        logInfoAction.setEnabled(true);
        logDebugAction.setEnabled(true);
        logTraceAction.setEnabled(true);
        valExpandAction.setEnabled(true);

        helpAction.setEnabled(true);
        aboutAction.setEnabled(true);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをDB接続状態にする。
     */
    public void DBConnect() {
        log.trace("開始");

        dbConnectAction.setEnabled(false);
        dbDisconnectAction.setEnabled(true);

        runAction.setEnabled(true);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをDB切断状態にする。
     */
    public void DBClose() {
        log.trace("開始");

        dbConnectAction.setEnabled(true);
        dbDisconnectAction.setEnabled(false);

        runAction.setEnabled(false);
        resumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        stepOverAction.setEnabled(false);
        stepOutAction.setEnabled(false);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーを実行状態にする。
     */
    public void Run() {
        log.trace("開始");

        runAction.setEnabled(false);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをBREAK状態にする。
     */
    public void Break() {
        log.trace("開始");

        resumeAction.setEnabled(true);
        stepAction.setEnabled(true);
        stepOverAction.setEnabled(true);
        stepOutAction.setEnabled(true);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをRESUME状態にする。
     */
    public void Resume() {
        log.trace("開始");

        resumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        stepOverAction.setEnabled(false);
        stepOutAction.setEnabled(false);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをSTEP実行状態にする。
     */
    public void Step() {
        log.trace("開始");

        resumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        stepOverAction.setEnabled(false);
        stepOutAction.setEnabled(false);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをSTEP OVER実行状態にする。
     */
    public void StepOver() {
        log.trace("開始");

        resumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        stepOverAction.setEnabled(false);
        stepOutAction.setEnabled(false);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーをSTEP OUT実行状態にする。
     */
    public void StepOut() {
        log.trace("開始");

        resumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        stepOverAction.setEnabled(false);
        stepOutAction.setEnabled(false);

        log.trace("終了");
    }

    /**
     * メニューバー・ツールバーを実行終了状態にする。
     */
    public void RunEnd() {
        log.trace("開始");

        runAction.setEnabled(true);

        log.trace("終了");
    }


    /**
     * アクション定義簡略化のためのクラス。
     * アクション実行時に、指定したイベントIDでイベントハンドラを呼び出す。
     * イベントハンドラ呼び出し時のイベント引数は{@code null}となる。
     *
     * @author Tamura Shingo
     * @see GUIEventHandler#processGuiEvent(int, GUIEvent)
     *
     */
    private class MenuAction extends AbstractAction {

        private static final long serialVersionUID = 1762034510979449643L;

        /**
         * イベントID
         * @see GUIEventHandler
         */
        private GUIEventHandler.ID eventid;

        /**
         * コンストラクタ
         * @param eventid イベントID
         * @see GUIEventHandler
         */
        public MenuAction(String name, GUIEventHandler.ID eventid) {
            super(name);
            this.eventid = eventid;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            log.trace("開始");
            eventHandler.processGuiEvent(eventid, null);
            log.trace("終了");
        }
    }


    private class CheckAction extends AbstractAction {

        private static final long serialVersionUID = 5418700961740083615L;

        /**
         * イベントID
         * @see GUIEventHandler
         */
        private GUIEventHandler.ID eventid;

        /**
         * チェックボックス
         */
        private JCheckBoxMenuItem item;

        public CheckAction(String name, GUIEventHandler.ID eventid) {
            super(name);
            this.eventid = eventid;
        }

        public void setCheckBox(JCheckBoxMenuItem item) {
            log.trace("開始");
            this.item = item;
            log.trace("終了");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            log.trace("開始");
            GUIEventValueExpand args = new GUIEventValueExpand();
            args.setExpandFlag(item.getState());
            eventHandler.processGuiEvent(eventid, args);
            log.trace("終了");
        }
    }
}
