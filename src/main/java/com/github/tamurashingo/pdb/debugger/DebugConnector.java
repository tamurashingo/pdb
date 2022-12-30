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
package com.github.tamurashingo.pdb.debugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.SystemConstants;
import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.bean.VariablesBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.Util;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.StepRequest;

/**
 * デバッギ(TragetJVM)とのJDWP通信を行う。
 *
 * @author tamura shingo
 */
public class DebugConnector implements Runnable, DbgRequestHandler {

    private static final Log log;

    private boolean connected = false;

    private ListeningConnector connector;

    private BreakPointRegister breakpointRegister;

    private DbgEventHandler eventHandler;

    private ThreadReference threadReference = null;

    static {
        log = Logger.getLogger();
    }

    public DebugConnector() {
    }

    @Override
    public void init(DbgEventHandler eventHandler) {
        log.trace("開始");
        this.eventHandler = eventHandler;
        this.breakpointRegister = new BreakPointRegister();
        this.connector = getConnector();
        log.trace("終了");
    }


    @Override
    public void run() {
        log.trace("開始");
        try {
            accept();

            // 例外は必ず検出するように設定する。
            // TODO: 設定で検出するか否かを設定できるようにする。
            ExceptionRequest r = Globals.getVM().eventRequestManager().createExceptionRequest(null, true, true);
            r.addClassFilter("$Oracle.*");
            r.enable();
            EventQueue queue = Globals.getVM().eventQueue();
            while (connected) {
                EventSet eventSet = null;
                while (connected && eventSet == null) {
                    eventSet = queue.remove(1000);
                }
                if (connected == true) {
                    for (Event event : eventSet) {
                        handleEvent(event);
                    }
                }
            }
        }
        catch (InterruptedException ex) {
            connected = false;
            log.printStackTrace(Level.WARN, ex);
            log.warn("デバッガを停止します。");
        }
        catch (IOException ex) {
            connected = false;
            log.printStackTrace(Level.WARN, ex);
            log.warn("デバッガを停止します。");
        }
        catch (IllegalConnectorArgumentsException ex) {
            connected = false;
            log.printStackTrace(Level.WARN, ex);
            log.warn("デバッガを停止します。");
        }
        finally {

        }
        log.trace("終了");
    }

    /**
     * コネクションの取得
     * @return
     */
    private ListeningConnector getConnector() {
        log.trace("開始");
        VirtualMachineManager manager = Bootstrap.virtualMachineManager();
        for (Connector connector : manager.listeningConnectors()) {
            log.debug("connector:%s", connector);
            if (connector.name().equals(SystemConstants.config.getDebugConnector())) {
                log.trace("終了");
                return (ListeningConnector) connector;
            }
        }
        log.trace("終了");
        throw new IllegalStateException();
    }

    /**
     * taretJVMからの接続を待つ。
     * @throws IllegalConnectorArgumentsException
     * @throws IOException
     */
    private void accept() throws IOException, IllegalConnectorArgumentsException {
        log.trace("開始");
        log.info("デバッギの接続を待ちます。待ちうけポート=%s", SystemConstants.config.getPort());
        Map<String, Connector.Argument> args = connector.defaultArguments();
        args.get("port").setValue(SystemConstants.config.getPort());
        VirtualMachine vm = connector.accept(args);
        Globals.setVM(vm);
        connected = true;
        log.info("デバッギが接続しました");
        log.trace("終了");
    }

    /**
     * targetJVMからのイベントを処理する。
     */
    private void handleEvent(Event event) {
        log.trace("開始");
        log.debug("イベント受付:%s", event);

        if (event instanceof AccessWatchpointEvent) {
            accessWatchpoint((AccessWatchpointEvent) event);
        }
        else if (event instanceof BreakpointEvent) {
            breakpoint((BreakpointEvent) event);
        }
        else if (event instanceof ClassPrepareEvent) {
            classPrepare((ClassPrepareEvent) event);
        }
        else if (event instanceof ClassUnloadEvent) {
            classUnload((ClassUnloadEvent) event);
        }
        else if (event instanceof ExceptionEvent) {
            exceptionEv((ExceptionEvent) event);
        }
        else if (event instanceof MethodEntryEvent) {
            methodEntry((MethodEntryEvent) event);
        }
        else if (event instanceof MethodExitEvent) {
            methodExit((MethodExitEvent) event);
        }
        else if (event instanceof ModificationWatchpointEvent) {
            modificationWatchpoint((ModificationWatchpointEvent) event);
        }
        else if (event instanceof StepEvent) {
            step((StepEvent) event);
        }
        else if (event instanceof ThreadDeathEvent) {
            threadDeath((ThreadDeathEvent) event);
        }
        else if (event instanceof ThreadStartEvent) {
            threadStart((ThreadStartEvent) event);
        }
        else if (event instanceof VMDeathEvent) {
            vmDeath((VMDeathEvent) event);
        }
        else if (event instanceof VMDisconnectEvent) {
            vmDisconnect((VMDisconnectEvent) event);
        }
        else if (event instanceof VMStartEvent) {
            vmStart((VMStartEvent) event);
        }
        else if (event instanceof WatchpointEvent) {
            watchpoint((WatchpointEvent) event);
        }
        else {
            // 未実装イベント
            log.warn("未実装イベント:%s", event);
        }
        log.trace("終了");
    }


    /**
     * フィールドアクセス通知のイベント処理。
     * 本ツールでは{@code AccessWatchpointRequest}を発行していないため、
     * {@code AccessWatchpointEvent}は発生しない。
     * @param event
     */
    private void accessWatchpoint(AccessWatchpointEvent event) {
        log.trace("開始");
        log.trace("終了");
    }


    /**
     * 変数を{@code VariablesBean}に変換する。
     * @param bean 変換先オブジェクト
     * @param obj
     * @param type
     */
    private void parseVariables(VariablesBean bean, ObjectReference obj, String type) {
        log.trace("開始");

        bean.setVariableType(type);

        /*-
         * PL/SQLの型の場合、 _value フィールドの値を取得する。
         * _value フィールドがない場合は null 値とする。
         */
        if (Util.nullToBlank(type).startsWith("$Oracle.Builtin.")) {
            log.debug("Oracle変数:%s", bean.getVariableName());
            Field field = obj.referenceType().fieldByName("_value");
            bean.setValue("");
            if (field != null) {
                Value value = obj.getValue(field);
                if (value != null) {
                    bean.setValue(((StringReference) value).value());
                }
            }
        }
        /*-
         * PL/SQLで定義した型の場合
         */
        else if (Util.nullToBlank(type).startsWith("$Oracle.")) {

            switch (typecheck(obj)) {

                /*-
                 * 配列の場合、_values フィールドで中身を取得できるため、それを更に解析する。
                 */
                case ARRAY: {
                    log.debug("PL/SQL配列変数:%s", bean.getVariableName());

                    bean.addVariable(new VariablesBean());
//				bean.setObject( obj );
                    if (Globals.getValExpand()) {
                        Field field = obj.referenceType().fieldByName("_values");
                        Value value = obj.getValue(field);
                        if (value != null && value instanceof ArrayReference) {
                            ArrayReference ar = (ArrayReference) value;
                            parseArray(bean, ar);
                        }
                    }
                    break;
                }
                /*-
                 * PL/SQL RECORDの場合、 "_type" フィールド以外のフィールドを更に解析する。
                 */
                case RECORD: {
                    log.debug("PL/SQLレコード変数:%s", bean.getVariableName());

                    bean.addVariable(new VariablesBean());
//				bean.setObject( obj );
                    if (Globals.getValExpand()) {
                        parsePLSQLRecord(bean, obj);
                    }
                    break;
                }
                /*-
                 * 配列、PL/SQL RECORD以外の場合は、 _value の値を再解析にかける。
                 */
                default: {
                    log.debug("PL/SQL変数:%s", bean.getVariableName());

                    Field f = obj.referenceType().fieldByName("_value");
                    Value value = obj.getValue(f);
                    if (value instanceof ObjectReference) {
                        parseVariables(bean, (ObjectReference) value, ((ObjectReference) value).referenceType().name());
                    }
                    break;
                }
            }
        }
        /*-
         * primitive型などの場合、PL/SQL の変数ではありえないため、無視する。
         */
        else {
            //
            log.debug("解析対象外の型です:%s", Util.nullToBlank(type));
        }
        log.trace("終了");
    }

    private void parseArray(VariablesBean bean, ArrayReference obj) {
        for (Value value : obj.getValues()) {

            VariablesBean child = new VariablesBean();
            bean.addVariable(child);

            // PL/SQL の配列は $Oracle.xxxx 型のインスタンス情報であるため、
            // すべて ObjectReference となる。
            // 念のため、インスタンスのチェックをしておく。
            if (value instanceof ObjectReference) {
                // 配列に設定されている変数を解析し、VariablesBeanに変換する
                ObjectReference o = (ObjectReference) value;
                parseVariables(child, o, o.referenceType().name());
            }
            else {
                child.setVariableType("unknown");
                child.setValue("");
            }
        }
    }

    private void parsePLSQLRecord(VariablesBean bean, ObjectReference obj) {

        for (Field field : obj.referenceType().fields()) {
            Value value = obj.getValue(field);

            /*-
             * PL/SQL Record型のメンバはすべて $Oracle.xxx のインスタンスであるため、
             * メンバの型はObjectReference型となる。
             * ただし StringReferenceは _type フィールドであるため、除外する。
             */
            if ((value instanceof StringReference) == false) {
                if (value instanceof ObjectReference) {
                    ObjectReference of = (ObjectReference) value;
                    VariablesBean child = new VariablesBean();
                    bean.addVariable(child);
                    child.setVariableName(field.name());
                    parseVariables(child, of, of.referenceType().name());
                }
            }
        }
    }


    /**
     * 配列 or PL/SQL RECORDの判定を行う。
     * @param obj
     */
    private VariableType typecheck(ObjectReference obj) {

        /*-
         * _type フィールドが "varray" の場合、配列
         * _type フィールドが "record" の場合、PL/SQL RECORD
         *
         */
        Field field = obj.referenceType().fieldByName("_type");
        if (field != null) {
            Value value = obj.getValue(field);
            String type = ((StringReference) value).value();
            if (type.equals("varray")) {
                return VariableType.ARRAY;
            }
            else if (type.equals("record")) {
                return VariableType.RECORD;
            }
        }
        return VariableType.UNKNOWN;
    }


    /**
     * 指定したスタック上の変数の一覧を作成する。
     * @param stackFrame
     * @return
     * @throws Exception
     */
    private List<VariablesBean> makeVariablesList(StackFrame stackFrame) {
        List<VariablesBean> values = new ArrayList<VariablesBean>();

        try {
            for (LocalVariable val : stackFrame.location().method().variables()) {

                if (val.isVisible(stackFrame)) {

                    ObjectReference obj = (ObjectReference) stackFrame.getValue(val);
                    ReferenceType ref = obj.referenceType();

                    VariablesBean bean = new VariablesBean();
                    bean.setVariableName(val.name());
                    parseVariables(bean, obj, ref.classObject().reflectedType().name());

                    values.add(bean);
                }
            }
        }
        catch (AbsentInformationException ex) {
            // 行番号・変数の情報が利用不可能
            log.printStackTrace(Level.WARN, ex);
            log.warn("行番号、変数の情報が取得できません。");
        }

        return values;
    }


    /**
     * ブレイクポイント通知のイベント処理。
     * ブレイク時の変数一覧を作成し、メインコントローラへブレイクイベントを通知する。
     * @param event
     */
    private void breakpoint(BreakpointEvent event) {
        log.trace("開始");

        // 位置情報をもとに、ソース表示用の情報を取得する
        BreakPointBean bean = createBreakPointBean(event.location());
        Globals.setBreakSource(bean.getSourceName());
        Globals.setBreakType(bean.getType());
        Globals.setBreakLine(bean.getLine());

        threadReference = event.thread();

        log.info("break at %s.%s(%d)", bean.getSourceName(), bean.getType(), bean.getLine());

        List<VariablesBean> values = null;
        try {
            values = makeVariablesList(event.thread().frame(0));
        }
        catch (IncompatibleThreadStateException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("スレッドから情報を取得できませんでした。");
        }


        // メインコントローラへブレイクイベントを通知する。
        DBGEventBreak ev = new DBGEventBreak();
        ev.setSourceName(bean.getSourceName());
        ev.setSourceType(bean.getType());
        ev.setLine(bean.getLine());
        ev.setVariables(values);
        eventHandler.processDbgEvent(DbgEventHandler.ID.VMBREAK, ev);
    }


    private void classPrepare(ClassPrepareEvent event) {
        log.trace("開始");
        log.debug("ロードされたクラス:%s", event.referenceType().name());

        // breakpointの再定義
        breakpointRegister.update(event.referenceType().name());
        Globals.getVM().resume();

        log.trace("終了");
    }

    private void classUnload(ClassUnloadEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void exceptionEv(ExceptionEvent event) {
        log.trace("開始");
        log.warn("例外が発生しました。");
        String source = "";
        int line = -1;
        String oraMsg = "";

        for (Field f : event.exception().referenceType().allFields()) {
            if (f.name().equals("_sqlerrm")) {
                // _sqlcode は int
                // _sqlerrm は java.lang.String
                oraMsg = ((StringReference) event.exception().getValue(f)).value();
                log.warn("%s", oraMsg);
                break;
            }
        }
        try {
            Location from = event.location();
            log.warn("例外発生箇所：%s(%d)", from.sourceName(), from.lineNumber());
            source = from.sourceName();
            line = from.lineNumber();
        }
        catch (AbsentInformationException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("例外解析中にエラーが発生しました。");
        }

        // メインコントローラへ例外発生を通知する。
        DBGEventExceptionInfo ev = new DBGEventExceptionInfo();
        ev.setSource(source);
        ev.setLine(line);
        ev.setErrMsg(oraMsg);
        eventHandler.processDbgEvent(DbgEventHandler.ID.EXCEPTION, ev);

        Globals.getVM().resume();

        log.trace("終了");
    }

    private void methodEntry(MethodEntryEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void methodExit(MethodExitEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void modificationWatchpoint(ModificationWatchpointEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void step(StepEvent event) {
        log.trace("開始");

        BreakPointBean bean = createBreakPointBean(event.location());
        Globals.setBreakSource(bean.getSourceName());
        Globals.setBreakType(bean.getType());
        Globals.setBreakLine(bean.getLine());

        threadReference = event.thread();

        log.info("step at %s.%s(%d)", bean.getSourceName(), bean.getType(), bean.getLine());


        List<VariablesBean> values = null; ;
        try {
            values = makeVariablesList(event.thread().frame(0));
        }
        catch (IncompatibleThreadStateException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("変数情報の作成に失敗しました。");
        }

        DBGEventBreak ev = new DBGEventBreak();
        ev.setSourceName(bean.getSourceName());
        ev.setSourceType(bean.getType());
        ev.setLine(bean.getLine());
        ev.setVariables(values);
        eventHandler.processDbgEvent(DbgEventHandler.ID.VMBREAK, ev);

        log.trace("開始");
    }

    private void threadDeath(ThreadDeathEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void threadStart(ThreadStartEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void vmDeath(VMDeathEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void vmDisconnect(VMDisconnectEvent event) {
        log.trace("開始");
        log.trace("終了");
    }

    private void vmStart(VMStartEvent event) {
        log.trace("開始");
        Globals.getVM().resume();
        log.trace("終了");
    }

    private void watchpoint(WatchpointEvent event) {
        log.trace("開始");
        log.trace("終了");
    }


    /**
     * {@code Location}からパッケージ名、ソース名、行番号などを取得する。
     * @param location ブレイク時の位置情報
     * @return 取得した情報で作成した{@link BreakPointBean}
     */
    private BreakPointBean createBreakPointBean(Location location) {
        log.trace("開始");
        BreakPointBean bean = new BreakPointBean();
        try {
            String sourcePath = location.sourcePath();
            String sourceName = location.sourceName();
            String className = sourcePath.replace('\\', '.').replace(".pls", "");
            bean.setClassName(className);
            bean.setLine(location.lineNumber());
            bean.setSourceName(sourceName.replace(".pls", "").toUpperCase());
            String[] cls = sourcePath.split("\\\\");
            if (cls[1].equals("PackageBody")) {
                cls[1] = "PACKAGE BODY";
            }
            else {
                cls[1] = cls[1].toUpperCase();
            }
            bean.setType(cls[1]);
            bean.setValid(false);
        }
        catch (NullPointerException ex) {
            // プログラム実行前のOracleエラーの場合、locationがnullとなる。
            log.printStackTrace(Level.WARN, ex);
            log.warn("位置情報を取得できませんでした");
        }
        catch (AbsentInformationException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("位置情報を取得できませんでした");
        }

        log.trace("終了");
        return bean;
    }


    @Override
    public void startDebugger() {
        log.info("デバッガ用スレッドを立ち上げます。");
        Thread th = new Thread(this);
        th.start();
        log.info("デバッガ用スレッドを立ち上げました。");
    }


    @Override
    public void stopDebugger() {
        log.info("デバッガを停止します。");
        connected = false;
        log.info("デバッガを停止しました。");
    }


    @Override
    public List<BreakPointBean> getBreakPoints() {
        log.trace("開始");
        log.trace("終了");
        return null;
    }


    @Override
    public boolean setBreakPoint(BreakPointBean bean) {
        breakpointRegister.set(bean);
        return true;
    }


    @Override
    public boolean removeBreakPoint(BreakPointBean bean) {
        log.trace("開始");
        log.trace("終了");
        return false;
    }


    @Override
    public boolean removeBreakPoint(String sourceName, int line) {
        log.trace("開始");
        log.trace("終了");
        return false;
    }

    @Override
    public void resume() {
        log.trace("開始");
        Globals.getVM().resume();
        log.trace("終了");
    }

    @Override
    public void step() {
        log.trace("開始");

        if (threadReference != null) {
            EventRequestManager mgr = Globals.getVM().eventRequestManager();

            clearStep();

            StepRequest req = mgr.createStepRequest(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
            req.addCountFilter(1);
            req.enable();
            Globals.getVM().resume();
        }

        log.trace("終了");
    }

    @Override
    public void stepOver() {
        log.trace("開始");

        clearStep();

        EventRequestManager mgr = Globals.getVM().eventRequestManager();
        StepRequest req = mgr.createStepRequest(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        req.addCountFilter(1);
        req.enable();
        Globals.getVM().resume();

        log.trace("終了");
    }

    @Override
    public void stepOut() {
        log.trace("開始");

        clearStep();

        EventRequestManager mgr = Globals.getVM().eventRequestManager();
        StepRequest req = mgr.createStepRequest(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_OUT);
        req.addCountFilter(1);
        req.enable();
        Globals.getVM().resume();

        log.trace("終了");
    }


    @Override
    public void clearStep() {
        log.trace("開始");

        EventRequestManager mgr = Globals.getVM().eventRequestManager();
        List<StepRequest> stepRequests = mgr.stepRequests();
        for (StepRequest req : stepRequests) {
            if (req.thread().equals(threadReference)) {
                mgr.deleteEventRequest(req);
                log.debug("残っていたStepRequstを削除しました。");
                break;
            }
        }

        log.trace("終了");
    }

    /**
     * 変数の型
     * @author Tamura Shingo
     */
    private static enum VariableType {
        /** 配列 */
        ARRAY,
        /** PLSQL RECORD */
        RECORD,
        /** 不明 */
        UNKNOWN,
    }
}