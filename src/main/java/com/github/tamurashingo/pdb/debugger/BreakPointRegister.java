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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

/**
 * ブレイクポイントのデバッギとのやり取りを管理するクラス。
 *
 * @author tamura shingo
 */
public class BreakPointRegister {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private List<BreakPointBean> breakPointList = new ArrayList<BreakPointBean>();

    /** ClassPrepareRequestを行ったクラスの集合 */
    private Set<String> prepareClass = new HashSet<String>();

    public void set(BreakPointBean bean) {
        log.trace("開始");
        if (isLoaded(bean.getClassName())) {
            /* TargetVMがクラスをロード済みの場合、ブレイクポイントを設定する */
            log.debug("%s:ロード済み", bean.getClassName());

            if (bean.isValid()) {
                setBreakPoint(bean);
            }
            else {
                removeBreakPoint(bean);
            }
        }
        else {
            log.debug("%s:未ロード", bean.getClassName());

            if (bean.isValid()) {

                /* TargetVMにクラスが読み込まれていない場合、クラスを読み込んだタイミングで通知を受けるよう、リクエストする */
                /* ClassPrepareRequest登録済みのクラスであるかを事前チェックする　*/
                if (prepareClass.contains(bean.getClassName()) == false) {
                    prepareClass.add(bean.getClassName());
                    VirtualMachine vm = Globals.getVM();
                    ClassPrepareRequest req = vm.eventRequestManager().createClassPrepareRequest();
                    req.addClassFilter(bean.getClassName());
                    req.addCountFilter(1);
                    req.enable();
                }

                /* クラス読み込みイベントを受けた時にブレイクポイントを設定できるよう、保持する */
                breakPointList.add(bean);
            }
            else {
                // ブレイクポイント設定用のリストから対象のBeanをはずす。
                breakPointList.remove(bean);
            }

        }
        log.trace("終了");
    }

    /**
     * @param bean
     */
//	public void setBreak( BreakPointBean bean ) {
//		
//	}


    /**
     * 未定義のブレイクポイントを設定する。
     */
    public void update(String className) {
        log.trace("update開始");
        for (Iterator<BreakPointBean> it = breakPointList.iterator(); it.hasNext(); ) {
            BreakPointBean bean = it.next();
            if (className.equals(bean.getClassName())) {
                setBreakPoint(bean);
            }
//			if ( isLoaded( bean.getClassName() ) ) {
//				setBreakPoint( bean );
//			}
        }
        log.trace("update終了");
    }

    /**
     * クラスが読み込み済みかをチェックする。
     * @param className
     * @return 読み込み済みの場合true
     */
    private boolean isLoaded(String className) {
        log.trace("isLoaded開始");
        log.trace("isLoaded終了");
        return getReferenceType(className) != null ? true : false;
    }


    private ReferenceType getReferenceType(String className) {
        log.trace("getReferenceType開始");
        List<ReferenceType> classes = Globals.getVM().classesByName(className);

        for (ReferenceType ref : classes) {
            if (ref.name().equals(className)) {
                log.trace("getReferenceType終了:found");
                return ref;
            }
        }
        log.trace("getReferenceType終了:notfound");
        return null;
    }

    private void setBreakPoint(BreakPointBean bean) {
        log.trace("addBreakPoint開始");
        try {
            ReferenceType ref = getReferenceType(bean.getClassName());
            List<Location> loc = ref.locationsOfLine(bean.getLine());
            if (loc == null || loc.isEmpty()) {
                log.warn("%s:%d ブレイクポイントを設定できない行です。", bean.getSourceName(), bean.getLine());
                bean.setValid(false);
                return;
            }
            BreakpointRequest req = Globals.getVM().eventRequestManager().createBreakpointRequest(loc.get(0));
            req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            req.enable();

            log.info("%s:%d ---------------->ブレイクポイントを[%s]に設定しました。", bean.getSourceName(), bean.getLine(), bean.isValid() ? "有効" : "無効");
        }
        catch (AbsentInformationException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("%s:%d ブレイクポイントの設定に失敗しました。", bean.getSourceName(), bean.getLine());
        }
        log.trace("addBreakPoint終了");
    }

    private void removeBreakPoint(BreakPointBean bean) {
        log.trace("開始");

        try {
            // Locationオブジェクトを作成する。
            ReferenceType ref = getReferenceType(bean.getClassName());
            List<Location> loc = ref.locationsOfLine(bean.getLine());

            List<BreakpointRequest> rmList = new LinkedList<BreakpointRequest>();

            for (BreakpointRequest req : Globals.getVM().eventRequestManager().breakpointRequests()) {
                if (req.location().equals(loc.get(0))) {
                    rmList.add(req);
                    log.debug("デバッグポイントを削除します。%s:%d", bean.getSourceName(), bean.getLine());
                }
            }

            Globals.getVM().eventRequestManager().deleteEventRequests(rmList);

        }
        catch (AbsentInformationException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("デバッグポイントの削除に失敗しました。");
        }
        finally {

        }
        log.trace("終了");
    }
}
