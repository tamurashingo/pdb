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

import java.util.HashMap;
import java.util.Map;

import com.github.tamurashingo.pdb.controller.command.dbg.BreakPointCommand;
import com.github.tamurashingo.pdb.controller.command.dbg.ExceptionCommand;
import com.github.tamurashingo.pdb.controller.command.gui.AboutCommand;
import com.github.tamurashingo.pdb.controller.command.gui.CompileSourceCommand;
import com.github.tamurashingo.pdb.controller.command.gui.DBConnectCommand;
import com.github.tamurashingo.pdb.controller.command.gui.ExitCommand;
import com.github.tamurashingo.pdb.controller.command.gui.LogLevelDebugCommand;
import com.github.tamurashingo.pdb.controller.command.gui.LogLevelInfoCommand;
import com.github.tamurashingo.pdb.controller.command.gui.LogLevelNoneCommand;
import com.github.tamurashingo.pdb.controller.command.gui.LogLevelTraceCommand;
import com.github.tamurashingo.pdb.controller.command.gui.LogLevelWarnCommand;
import com.github.tamurashingo.pdb.controller.command.gui.OpenSourceCommand;
import com.github.tamurashingo.pdb.controller.command.gui.ResumeCommand;
import com.github.tamurashingo.pdb.controller.command.gui.RunCommand;
import com.github.tamurashingo.pdb.controller.command.gui.SourceUpateCommand;
import com.github.tamurashingo.pdb.controller.command.gui.StepCommand;
import com.github.tamurashingo.pdb.controller.command.gui.StepOutCommand;
import com.github.tamurashingo.pdb.controller.command.gui.StepOverCommand;
import com.github.tamurashingo.pdb.controller.command.gui.ToggleBreakpointCommand;
import com.github.tamurashingo.pdb.controller.command.gui.ValueExpandCommand;
import com.github.tamurashingo.pdb.debugger.DBGEvent;
import com.github.tamurashingo.pdb.debugger.DbgEventHandler;
import com.github.tamurashingo.pdb.debugger.DbgRequestHandler;
import com.github.tamurashingo.pdb.debugger.DebugConnector;
import com.github.tamurashingo.pdb.gui.GUIEvent;
import com.github.tamurashingo.pdb.gui.GUIEventHandler;
import com.github.tamurashingo.pdb.gui.GUIRequestHandler;
import com.github.tamurashingo.pdb.gui.MainWindow;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


/**
 * メインコントローラ。
 * GUIとデバッガのイベントを管理する。
 *
 * @author tamura shingo
 */
public class MainController implements GUIEventHandler, DbgEventHandler {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    /** GUI部 */
    GUIRequestHandler gui;

    /** デバッガ部 */
    DbgRequestHandler dbg;


    /** GUIイベント */
    private Map<GUIEventHandler.ID, GUIEventProcessCommand> guiEvent = new HashMap<GUIEventHandler.ID, GUIEventProcessCommand>();

    /** デバッガイベント */
    private Map<DbgEventHandler.ID, DBGEventProcessCommand> dbgEvent = new HashMap<DbgEventHandler.ID, DBGEventProcessCommand>();

    /**
     * コンストラクタ。
     */
    public MainController() {
        gui = new MainWindow();
        dbg = new DebugConnector();
    }

    /**
     * 初期化。
     * 各イベントとそれに対応する処理のマッピングを行う。
     */
    public void init() {
        log.trace("開始");

        dbg.init(this);

        guiEvent.put(GUIEventHandler.ID.DBCONNECT, new DBConnectCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.SELECT_SOURCE, new OpenSourceCommand(gui, dbg));

        guiEvent.put(GUIEventHandler.ID.LOGLEVEL_WARN, new LogLevelWarnCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.LOGLEVEL_INFO, new LogLevelInfoCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.LOGLEVEL_DEBUG, new LogLevelDebugCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.LOGLEVEL_TRACE, new LogLevelTraceCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.LOGLEVEL_NONE, new LogLevelNoneCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.VARIABLE, new ValueExpandCommand(gui, dbg));

        guiEvent.put(GUIEventHandler.ID.SELECT_BREAKPOINT, new ToggleBreakpointCommand(gui, dbg));

        guiEvent.put(GUIEventHandler.ID.RUN, new RunCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.RESUME, new ResumeCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.STEP, new StepCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.STEPOUT, new StepOutCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.STEPOVER, new StepOverCommand(gui, dbg));

        guiEvent.put(GUIEventHandler.ID.COMPILE_SOURCE, new CompileSourceCommand(gui, dbg));

        guiEvent.put(GUIEventHandler.ID.UPDATE_SOURCETREE, new SourceUpateCommand(gui, dbg));

        guiEvent.put(GUIEventHandler.ID.EXIT, new ExitCommand(gui, dbg));
        guiEvent.put(GUIEventHandler.ID.ABOUT, new AboutCommand(gui, dbg));

        dbgEvent.put(DbgEventHandler.ID.VMBREAK, new BreakPointCommand(gui, dbg));
        dbgEvent.put(DbgEventHandler.ID.EXCEPTION, new ExceptionCommand(gui, dbg));

        gui.init(this);

        log.trace("終了");
    }

    /**
     * アプリケーションの開始。
     */
    public void start() {
        log.trace("開始");
        gui.showWindow();
        processGuiEvent(GUIEventHandler.ID.DBCONNECT, null);
        log.trace("終了");
    }


    /**
     * GUI部からのイベント受付。
     * @param eventid イベントID
     * @param event イベントコマンドへの引数
     * @see GUIEventHandler
     */
    @Override
    public void processGuiEvent(GUIEventHandler.ID eventid, GUIEvent event) {
        log.trace("開始");

        // ログレベルがtraceの場合、コマンドに対応するコメントを表示する。
        if (log.isTrace()) {
            String comment = "";
            try {
                EventAnnotation ann = eventid.getClass().getField(eventid.name()).getAnnotation(EventAnnotation.class);
                comment = ann.value();
            }
            catch (SecurityException ex) {
                // コメントを取得できない場合は空を表示する
            }
            catch (NoSuchFieldException ex) {
                // コメントを取得できない場合は空を表示する
            }
            log.trace("processGuiEvent:%s", comment);
        }

        GUIEventProcessCommand command = guiEvent.get(eventid);
        if (command == null) {
            command = new GUIEventProcessCommand() {
                @Override
                public void doCommand(GUIEvent event) {
                    log.info("未実装");
                }
            };
        }
        command.doCommand(event);

        log.trace("終了");
    }

    /**
     * デバッガ部からのイベント受付。
     * @param eventid イベントID
     * @param event イベントコマンドへの引数
     * @see DbgEventHandler
     */
    @Override
    public void processDbgEvent(DbgEventHandler.ID eventid, DBGEvent event) {
        log.trace("開始");

        // ログレベルがtraceの場合、コマンドに対応するコメントを表示する。
        if (log.isTrace()) {
            String comment = "";
            try {
                EventAnnotation ann = eventid.getClass().getField(eventid.name()).getAnnotation(EventAnnotation.class);
                comment = ann.value();
            }
            catch (SecurityException ex) {
                // コメントを取得できない場合は空を表示する
            }
            catch (NoSuchFieldException ex) {
                // コメントを取得できない場合は空を表示する
            }
            log.trace("processGuiEvent:%s", comment);
        }

        DBGEventProcessCommand command = dbgEvent.get(eventid);
        if (command == null) {
            command = new DBGEventProcessCommand() {
                @Override
                public void doCommand(DBGEvent event) {
                    log.info("未実装");
                }
            };
        }
        command.doCommand(event);

        log.trace("終了");
    }
}
