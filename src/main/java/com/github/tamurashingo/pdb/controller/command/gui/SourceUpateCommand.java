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

import java.util.List;

import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.controller.command.AbstractGUIEventProcessCommand;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.db.DBManager;
import com.github.tamurashingo.pdb.debugger.DbgRequestHandler;
import com.github.tamurashingo.pdb.gui.GUIEvent;
import com.github.tamurashingo.pdb.gui.GUIRequestHandler;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.util.BackGroundProcess;
import com.github.tamurashingo.pdb.util.BackGroundProcessException;

import static com.github.tamurashingo.pdb.SystemConstants.APP_FULL_NAME;

public class SourceUpateCommand extends AbstractGUIEventProcessCommand {

    private DBManager db;

    public SourceUpateCommand(GUIRequestHandler guiHandler, DbgRequestHandler dbgHandler) {
        super(guiHandler, dbgHandler);
        db = DBManager.getInstance();
    }

    @Override
    public void doCommand(GUIEvent event) {
        log.trace("開始");

        guiHandler.showProgressBar(APP_FULL_NAME, "ソース一覧を取得中です", new BackGroundProcess() {
            @Override
            public void runBackground() throws BackGroundProcessException {
                try {
                    log.trace("開始");
                    List<ProceduresBean> list = db.getProceduresForTree();
                    guiHandler.updateTreeWindow(list);
                    log.trace("終了");
                }
                catch (DBException ex) {
                    log.printStackTrace(Log.Level.WARN, ex);
                    throw new BackGroundProcessException("DBアクセスエラー", ex);
                }
            }
        });

        log.trace("終了");
    }
}
