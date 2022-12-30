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
package com.github.tamurashingo.pdb.controller.command.dbg;

import com.github.tamurashingo.pdb.controller.command.AbstractDBGEventProcessCommand;
import com.github.tamurashingo.pdb.debugger.DBGEvent;
import com.github.tamurashingo.pdb.debugger.DBGEventExceptionInfo;
import com.github.tamurashingo.pdb.debugger.DbgRequestHandler;
import com.github.tamurashingo.pdb.gui.GUIRequestHandler;

import static com.github.tamurashingo.pdb.util.Util.cast;

/**
 * デバッガからの例外イベントを処理する。
 *
 * @author tamura shingo
 */
public class ExceptionCommand extends AbstractDBGEventProcessCommand {

    public ExceptionCommand(GUIRequestHandler guiHandler, DbgRequestHandler dbgHandler) {
        super(guiHandler, dbgHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doCommand(DBGEvent event) {
        log.trace("開始");

        DBGEventExceptionInfo ev = cast(event);
        guiHandler.showProcedureException(ev.getSource(), ev.getLine(), ev.getErrMsg());

        log.trace("終了");
    }
}
