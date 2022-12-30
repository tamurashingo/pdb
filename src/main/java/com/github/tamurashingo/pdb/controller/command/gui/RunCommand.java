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
package com.github.tamurashingo.pdb.controller.command.gui;

import javax.swing.SwingWorker;
import java.util.List;

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.controller.command.AbstractGUIEventProcessCommand;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.db.DBManager;
import com.github.tamurashingo.pdb.db.ExecProcedure;
import com.github.tamurashingo.pdb.debugger.DbgRequestHandler;
import com.github.tamurashingo.pdb.gui.GUIEvent;
import com.github.tamurashingo.pdb.gui.GUIEventExec;
import com.github.tamurashingo.pdb.gui.GUIRequestHandler;
import com.github.tamurashingo.pdb.gui.parts.ParamDialog;
import com.github.tamurashingo.pdb.gui.parts.ResultDialog;
import com.github.tamurashingo.pdb.gui.parts.RunDialog;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.util.GuiUtil;

public class RunCommand extends AbstractGUIEventProcessCommand {

    public RunCommand(GUIRequestHandler guiHandler, DbgRequestHandler dbgHandler) {
        super(guiHandler, dbgHandler);
    }

    @Override
    public void doCommand(GUIEvent event) {
        log.trace("開始");

        RunDialog dialog = null;

        if (event instanceof GUIEventExec) {
            GUIEventExec ev = (GUIEventExec) event;
            dialog = new RunDialog(null, ev.getSourceName(), ev.getSourceType());
        }
        else {
            dialog = new RunDialog(null);
        }

        dialog.showDialog();
        if (dialog.getOkCancel() == true) {
            String packageName = dialog.getSelectedProcedure().getObjectName();
            String objectType = dialog.getSelectedProcedure().getObjectType();
            String methodName = dialog.getSelectedMethod();
            log.debug("%s[%s]", dialog.getSelectedProcedure().getObjectName(), dialog.getSelectedProcedure().getObjectType());
            ParamDialog paramDialog = new ParamDialog(null, packageName, methodName);
            paramDialog.showDialog();
            if (paramDialog.isOk() == true) {
                guiHandler.menuRun();
                SwingWorker<Object, Object> worker = new ExecuteWorker(packageName, methodName, objectType, paramDialog.getParams());
                worker.execute();
            }
        }

        log.trace("終了");
    }

    private class ExecuteWorker extends SwingWorker<Object, Object> {
        String packageName;

        String methodName;

        String objectType;

        List<ParamsBean> list;

        public ExecuteWorker(String packageName, String methodName, String objectType, List<ParamsBean> list) {
            this.packageName = packageName;
            this.methodName = methodName;
            this.objectType = objectType;
            this.list = list;
            log.debug("packageName:%s", packageName);
            log.debug("methodName:%s", methodName);
            log.debug("objectType:%s", objectType);
        }

        @Override
        protected Object doInBackground() throws Exception {
            log.info("実行します");
            DBManager.getInstance().prepareOutput();
            guiHandler.updateTitlebar("[実行中]");
            dbgHandler.clearStep();
            try {
                ExecProcedure proc = new ExecProcedure(packageName, methodName, objectType, list);
                proc.execute();

                List<String> outputs = DBManager.getInstance().getOutput();

                ResultDialog dialog = new ResultDialog(null, list, outputs);
                dialog.showDialog();
            }
            catch (DBException ex) {
                log.printStackTrace(Log.Level.WARN, ex);
                log.warn("実行中に異常が発生しました。");
                GuiUtil.showException(null, true, ex);
            }
            guiHandler.updateTitlebar(null);
            log.info("終了しました");

            guiHandler.menuRunEnd();
            Globals.initBreakPoint();
            guiHandler.updateSourceView();

            return null;
        }

    }
}


