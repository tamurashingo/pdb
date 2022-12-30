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

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.db.DBManager;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.GuiUtil;

import static com.github.tamurashingo.pdb.SystemConstants.icon;


public class RunDialog extends JDialog {

    private static final long serialVersionUID = -2813159718110900557L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private boolean runFlag = false;

    private DBManager db;

    private JComboBox procedures;

    private JComboBox methods;

    private ProceduresBean selectedProcedure;

    private String selectedMethod;

    List<ProceduresBean> procs;

    public RunDialog(Frame parent) {
        super(parent, true);
        db = DBManager.getInstance();
        makeDialog();
        makeSelectProcedure();
    }

    public RunDialog(Frame parent, String procedureName, String sourceType) {
        this(parent);
        select(procedureName, sourceType);
    }

    /**
     *
     * @return 実行の場合true
     */
    public boolean getOkCancel() {
        log.trace("開始");
        log.trace("終了");
        return runFlag;
    }

    public ProceduresBean getSelectedProcedure() {
        log.trace("開始");
        log.trace("終了");
        return selectedProcedure;
    }

    public String getSelectedMethod() {
        log.trace("開始");
        log.trace("終了");
        return selectedMethod;
    }


    private void makeDialog() {
        log.trace("開始");

        setTitle("実行");
        setIconImages(icon);

        /*-- 上部 --*/
        JLabel top = new JLabel("実行するプロシージャを選択してください");
        add(top, BorderLayout.NORTH);

        /*-- 中央 --*/
        procedures = new JComboBox();
        procedures.setRenderer(new ComboBoxRenderer());
        procedures.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    makeSelectMethods((ProceduresBean) event.getItem());
                }
            }
        });

        methods = new JComboBox();

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setLayout(layout);

        JLabel labelPackage = new JLabel("パッケージ");
        JLabel labelMethod = new JLabel("メソッド");

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup().addComponent(labelPackage).addComponent(labelMethod));
        hGroup.addGroup(layout.createParallelGroup().addComponent(procedures).addComponent(methods));
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelPackage).addComponent(procedures));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelMethod).addComponent(methods));
        layout.setVerticalGroup(vGroup);

        add(panel, BorderLayout.CENTER);


        /*-- 下部 --*/
        JButton okButton = new JButton("実行");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                runFlag = true;
                selectedProcedure = (ProceduresBean) procedures.getSelectedItem();
                if (selectedProcedure.getObjectType().equals("FUNCTION") || selectedProcedure.getObjectType().equals("PROCEDURE")) {
                    selectedProcedure.setObjectName(null);
                }
                selectedMethod = (String) methods.getSelectedItem();
                setVisible(false);
            }
        });
        JButton cancelButton = new JButton("キャンセル");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runFlag = false;
                selectedProcedure = null;
                selectedMethod = null;
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

    private void makeSelectProcedure() {
        log.trace("開始");

        try {
            procs = db.getProceduresForTree();
            Vector<ProceduresBean> vec = new Vector<ProceduresBean>(procs);
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            procedures.setModel(model);

            int maxCount = procedures.getItemCount();
            if (maxCount == 1) {
                makeSelectMethods((ProceduresBean) procedures.getItemAt(0));
            }
            else if (procedures.getItemCount() > 1) {
                procedures.setSelectedIndex(procedures.getItemCount() - 1);
                procedures.setSelectedIndex(0);
            }
        }
        catch (DBException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("DBエラーが発生しました。");
            this.setVisible(false);
        }
        pack();
        log.trace("終了");
    }


    private void makeSelectMethods(ProceduresBean bean) {
        log.trace("開始");

        try {
            List<String> procMethods = db.getMethods(bean.getObjectName(), bean.getObjectType());
            Vector<String> vec = new Vector<String>(procMethods);
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            methods.setModel(model);
        }
        catch (DBException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("DBエラーが発生しました。");
            this.setVisible(false);
        }
        log.trace("終了");
    }

    public void showDialog() {
        log.trace("開始");
        GuiUtil.showCenter(this);
        log.trace("終了");
    }

    public void select(String procedureName, String sourceType) {
        log.trace("開始");

        if (sourceType.equals("PACKAGE BODY")) {
            sourceType = "PACKAGE";
        }
        if (procs != null) {
            for (ProceduresBean bean : procs) {
                if (bean.getObjectName().equals(procedureName) && bean.getObjectType().equals(sourceType)) {
                    procedures.setSelectedItem(bean);
                    break;
                }
            }
        }

        log.trace("終了");
    }

    private static class ComboBoxRenderer extends JPanel implements ListCellRenderer {

        private static final long serialVersionUID = 7466624319875287389L;

        private JLabel leftLabel = new JLabel();

        private JLabel rightLabel = new JLabel();

        private static final int MARGIN = 5;

        public ComboBoxRenderer() {
            super(new BorderLayout());
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            leftLabel.setOpaque(false);
            leftLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));

            rightLabel.setOpaque(false);
            rightLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
            rightLabel.setForeground(Color.GRAY);
            rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            add(leftLabel);
            add(rightLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            if (value instanceof ProceduresBean) {

                ProceduresBean bean = (ProceduresBean) value;
                leftLabel.setText(bean.getObjectName());
                rightLabel.setText(bean.getObjectType());

                leftLabel.setFont(list.getFont());
                rightLabel.setFont(list.getFont());

                int width = list.getFontMetrics(list.getFont()).stringWidth(bean.getObjectType() + MARGIN * 2);

                rightLabel.setPreferredSize(new Dimension(width, 0));

                leftLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                this.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            }

            return this;
        }

    }

    /**
     * キー補完を行う。
     * @author Tamura Shingo
     *
     */
    @SuppressWarnings("unused")
    private static class KeyHandler extends KeyAdapter {

        private JComboBox box;

        private Vector<ProceduresBean> list = new Vector<ProceduresBean>();

        private boolean shouldHide = false;

        public KeyHandler(JComboBox box) {
            this.box = box;
            for (int ix = 0; ix < box.getModel().getSize(); ix++) {
                ProceduresBean bean = (ProceduresBean) box.getItemAt(ix);
                list.addElement(bean);
            }
        }

        @Override
        public void keyTyped(final KeyEvent event) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String text = ((JTextField) event.getSource()).getText();
                    if (text.length() == 0) {
                        setSuggestionModel(box, new DefaultComboBoxModel(list), "");
                        box.hidePopup();
                    }
                    else {
                        ComboBoxModel model = getSuggestionModel(list, text);
                        if (model.getSize() == 0 || shouldHide) {
                            box.hidePopup();
                        }
                        else {
                            setSuggestionModel(box, model, text);
                            box.showPopup();
                        }
                    }
                }
            });
        }

        private void setSuggestionModel(JComboBox box, ComboBoxModel model, String str) {
            box.setModel(model);
            box.setSelectedIndex(-1);
            ((JTextField) box.getEditor().getEditorComponent()).setText(str);
        }

        private ComboBoxModel getSuggestionModel(Vector<ProceduresBean> list, String text) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            for (ProceduresBean bean : list) {
                if (bean.getObjectName().startsWith(text)) {
                    model.addElement(bean);
                }
            }
            return model;
        }

    }
}
