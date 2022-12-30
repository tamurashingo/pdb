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

import java.awt.Container;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.bean.VariablesBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

public class InformationWindow extends JPanel {

    private static final long serialVersionUID = 151936724217073848L;

    private static final Log log;

    private BreakPointView breakPointView;

    private VariableView variableView;

    private JComponent logView;

    static {
        log = Logger.getLogger();
    }

    public InformationWindow() {
        breakPointView = new BreakPointView();
        variableView = new VariableView();
        logView = (JComponent) Logger.getRawLogger();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createInformationWindow());
    }

    public Container createInformationWindow() {
        log.trace("開始");

        JTabbedPane pane = new JTabbedPane();

        pane.add(breakPointView);
        pane.setTitleAt(pane.indexOfComponent(breakPointView), "ブレイクポイント");

        pane.add(variableView);
        pane.setTitleAt(pane.indexOfComponent(variableView), "変数");

        pane.add(logView);
        pane.setTitleAt(pane.indexOfComponent(logView), "ログ");

        log.trace("終了");
        return pane;
    }

    public void updateBreakPoint(List<BreakPointBean> list) {
        log.trace("開始");
        breakPointView.setBreakPoint(list);
        log.trace("終了");
    }

    public void addBreakPoint(BreakPointBean bean) {
        log.trace("開始");
        breakPointView.addBreakPoint(bean);
        log.trace("終了");
    }

    public void updateVariable(List<VariablesBean> list) {
        log.trace("開始");
        variableView.updateVariable(list);
        log.trace("終了");
    }

}
