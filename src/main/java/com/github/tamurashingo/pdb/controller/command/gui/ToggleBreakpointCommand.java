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

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.controller.BreakPointManager;
import com.github.tamurashingo.pdb.controller.command.AbstractGUIEventProcessCommand;
import com.github.tamurashingo.pdb.debugger.DbgRequestHandler;
import com.github.tamurashingo.pdb.gui.GUIEvent;
import com.github.tamurashingo.pdb.gui.GUIEventSelectBreakpoint;
import com.github.tamurashingo.pdb.gui.GUIRequestHandler;

import static com.github.tamurashingo.pdb.util.Util.cast;

public class ToggleBreakpointCommand extends AbstractGUIEventProcessCommand {

    public ToggleBreakpointCommand(GUIRequestHandler guiHandler, DbgRequestHandler dbgHandler) {
        super(guiHandler, dbgHandler);
    }

    @Override
    public void doCommand(GUIEvent event) {
        log.trace("??????");

        GUIEventSelectBreakpoint bp = cast(event);
        BreakPointManager breakpointManager = Globals.getBreakPointManager();

        BreakPointBean bean = breakpointManager.toggle(bp.getSourceName(), bp.getSourceType(), bp.getLine());
        // DebugRequest???????????????????????????
        if (bean != null) {
            dbgHandler.setBreakPoint(bean);
            guiHandler.updateBreakPoint(bp.getSourceName(), bp.getSourceType());
        }

        log.trace("??????");
    }
}
