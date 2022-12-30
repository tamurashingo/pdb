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

import com.github.tamurashingo.pdb.controller.command.AbstractGUIEventProcessCommand;
import com.github.tamurashingo.pdb.db.ConnectConfig;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.db.DBManager;
import com.github.tamurashingo.pdb.debugger.DbgRequestHandler;
import com.github.tamurashingo.pdb.gui.GUIEvent;
import com.github.tamurashingo.pdb.gui.GUIRequestHandler;
import com.github.tamurashingo.pdb.util.BackGroundProcess;
import com.github.tamurashingo.pdb.util.BackGroundProcessException;

import static com.github.tamurashingo.pdb.SystemConstants.APP_FULL_NAME;

public class DBConnectCommand extends AbstractGUIEventProcessCommand {

    private DBManager db;

    public DBConnectCommand(GUIRequestHandler guiHandler, DbgRequestHandler dbgHandler) {
        super(guiHandler, dbgHandler);
        db = DBManager.getInstance();
    }

    @Override
    public void doCommand(GUIEvent event) {
        log.trace("開始");

        final String[] dbinfo = guiHandler.login();
        if (dbinfo != null) {
            guiHandler.showProgressBar(APP_FULL_NAME, "DB接続中です", new BackGroundProcess() {
                @Override
                public void runBackground() throws BackGroundProcessException {
                    try {
                        log.info("DB接続中です");
                        db.connect(dbinfo[0], dbinfo[1], dbinfo[2], dbinfo[3], dbinfo[4]);
                        log.info("DB接続しました");
                        log.info("デバッガ起動します");
                        dbgHandler.startDebugger();
                        db.connectDebugger();
                        log.info("デバッガ起動しました");
                        ConnectConfig.getInstance().setConnected(true);
                    }
                    catch (DBException ex) {
                        ConnectConfig.getInstance().setConnected(false);
                        log.warn("ログイン失敗:%s", ex.getMessage());
                        db.close();
                        throw new BackGroundProcessException("DBアクセスエラー", ex);
                    }
                }
            });

            if (ConnectConfig.getInstance().getConnected()) {
                guiHandler.menuDBConnect();
                guiHandler.updateSourceTree();
            }
        }

        log.trace("終了");
    }
}
