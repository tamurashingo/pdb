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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.GuiUtil;

import static com.github.tamurashingo.pdb.SystemConstants.NEWLINE;
import static com.github.tamurashingo.pdb.SystemConstants.PARAMDIALOG_HEIGHT;
import static com.github.tamurashingo.pdb.SystemConstants.PARAMDIALOG_WIDTH;
import static com.github.tamurashingo.pdb.SystemConstants.icon;

/**
 * 結果を表示
 *
 * @author tamura shingo
 */
public class ResultDialog extends JDialog {

    private static final long serialVersionUID = -6310658180994796559L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private List<ParamsBean> list;

    private List<String> outputs = null;

    private ParamTable view;


    public ResultDialog(Frame parent, List<ParamsBean> list, List<String> outputs) {
        super(parent, true);

        this.list = list;
        this.outputs = outputs;
        makeDialog();
    }

    private void makeDialog() {
        log.trace("開始");

        setTitle("実行結果");
        setIconImages(icon);

        /*-- 上部 --*/
        JLabel top = new JLabel("実行結果");
        add(top, BorderLayout.NORTH);

        /*-- 中央 --*/
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        view = new ParamTable(list);
        JScrollPane scroll = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(PARAMDIALOG_WIDTH, PARAMDIALOG_HEIGHT));
        scroll.doLayout();

        centerPanel.add(scroll);

        if (outputs != null) {
            centerPanel.add(new JLabel("標準出力結果："));
            JTextArea text = new JTextArea();
            for (String out : outputs) {
                text.append(out);
                text.append(NEWLINE);
            }
            JScrollPane textScroll = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            centerPanel.add(textScroll);
        }


        add(centerPanel, BorderLayout.CENTER);

        /*-- 下部 --*/
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
            }
        });

        add(okButton, BorderLayout.SOUTH);

        pack();
        log.trace("終了");
    }

    public void showDialog() {
        log.trace("開始");
        GuiUtil.showCenter(this);
        log.trace("終了");
    }
}
