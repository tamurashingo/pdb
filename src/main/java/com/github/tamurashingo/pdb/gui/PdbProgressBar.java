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
package com.github.tamurashingo.pdb.gui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.BackGroundProcess;
import com.github.tamurashingo.pdb.util.BackGroundProcessException;
import com.github.tamurashingo.pdb.util.GuiUtil;

import static com.github.tamurashingo.pdb.SystemConstants.icon;

public class PdbProgressBar extends JDialog {

    private static final long serialVersionUID = 961284569633016665L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private JProgressBar bar;

    private String title;
    private String message;
    private BackGroundProcess proc;
    private PdbProgressBar thisInstance;

    public PdbProgressBar(Frame parent, String title, String message, BackGroundProcess proc) {
        super(parent, true);
        thisInstance = this;
        this.title = title;
        this.message = message;
        this.proc = proc;
        makeDialog();
    }

    public void showProgressBar() {
        log.trace("開始");
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected void done() {
                log.trace("開始");
                bar.setIndeterminate(false);
                thisInstance.setVisible(false);
                log.trace("終了");
            }

            @Override
            protected Object doInBackground() throws Exception {
                log.trace("開始");
                try {
                    proc.runBackground();
                } catch (BackGroundProcessException ex) {
                    log.printStackTrace(Level.WARN, ex);
                    GuiUtil.showException(null, true, ex);
                }
                log.trace("終了");
                return null;
            }
        };

        bar.setIndeterminate(true);
        worker.execute();
        GuiUtil.showCenter(thisInstance);
        log.trace("終了");
    }

    private void makeDialog() {
        log.trace("開始");
        setTitle(title);
        setIconImages(icon);
        Container contentPane = getContentPane();
        contentPane.add(new JLabel(message), BorderLayout.NORTH);

        bar = new JProgressBar();
        contentPane.add(bar, BorderLayout.CENTER);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        pack();
        log.trace("終了");
    }
}
