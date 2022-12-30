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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.db.DBManager;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.GuiUtil;

import static com.github.tamurashingo.pdb.SystemConstants.PARAMDIALOG_HEIGHT;
import static com.github.tamurashingo.pdb.SystemConstants.PARAMDIALOG_WIDTH;
import static com.github.tamurashingo.pdb.SystemConstants.icon;

/**
 * 引数を設定
 *
 * @author tamura shingo
 */
public class ParamDialog extends JDialog {

    private static final long serialVersionUID = 5220020093780359134L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private DBManager db;

    private List<ParamsBean> list;

    private ParamTable view;

    private boolean okFlag;

    public ParamDialog(Frame parent, String packageName, String objectName) {
        super(parent, true);

        db = DBManager.getInstance();
        getArguments(packageName, objectName);
        makeDialog();
    }

    private void makeDialog() {
        log.trace("開始");

        setTitle("引数");
        setIconImages(icon);

        /*-- 上部 --*/
        JLabel top = new JLabel("プロシージャの引数を指定してください");
        add(top, BorderLayout.NORTH);

        /*-- 中央 --*/
        view = new ParamTable(list);

        JScrollPane scroll = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(PARAMDIALOG_WIDTH, PARAMDIALOG_HEIGHT));
        scroll.doLayout();

        add(scroll, BorderLayout.CENTER);

        /*-- 下部 --*/
        JButton okButton = new JButton("実行");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                okFlag = true;
                setVisible(false);
            }
        });
        JButton cancelButton = new JButton("キャンセル");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                okFlag = false;
                setVisible(false);
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();

        log.trace("終了");
    }

    private void getArguments(String packageName, String objectName) {
        log.trace("開始");

        log.debug("packageName : %s", packageName);
        log.debug("objectName  : %s", objectName);
        try {
            list = db.getParameters(packageName, objectName);
        }
        catch (DBException ex) {
            log.printStackTrace(Level.WARN, ex);
            this.setVisible(false);
        }

        log.trace("終了");
    }

    public void showDialog() {
        log.trace("開始");
        GuiUtil.showCenter(this);
        log.trace("終了");
    }

    public boolean isOk() {
        log.trace("開始");
        log.trace("終了");
        return okFlag;
    }

    public List<ParamsBean> getParams() {
        log.trace("開始");
        log.trace("終了");
        return view.getParams();
    }

}
