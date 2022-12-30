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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


public class BreakPointView extends JPanel {

    private static final long serialVersionUID = -6651069884665088734L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    BreakPointModel model;

    JTable table;


    public BreakPointView() {
        model = new BreakPointModel();
        makeView();
    }

    public BreakPointView(List<BreakPointBean> list) {
        model = new BreakPointModel(list);
        makeView();
    }

    public void addBreakPoint(BreakPointBean bean) {
        log.trace("開始");
        List<BreakPointBean> list = model.getList();
        list.add(bean);
        setBreakPoint(list);
        log.trace("終了");
    }

    public void setBreakPoint(List<BreakPointBean> list) {
        log.trace("開始");
        model = new BreakPointModel(list);
        table.setModel(model);
        log.trace("終了");
    }

    private void makeView() {
        log.trace("開始");
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setModel(model);

        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(scrollPane);

        log.trace("終了");
    }

}

class BreakPointModel extends AbstractTableModel {

    private static final long serialVersionUID = -8179290471812589202L;

    /**
     * 各カラムの名称
     */
    private static final String[] headerNames = {
            "No",
            "Type",
            "Source",
            "Line",
            "Valid"
    };

    /**
     * 各レコードの情報
     */
    private List<BreakPointBean> records;

    public List<BreakPointBean> getList() {
        return records;
    }

    public BreakPointModel() {
        records = new ArrayList<BreakPointBean>();
    }

    public BreakPointModel(List<BreakPointBean> list) {
        records = list;
    }

    public void add(BreakPointBean bean) {
        records.add(bean);
    }

    /**
     * 列数を取得する
     *
     * @return 列数
     */
    @Override
    public int getColumnCount() {
        return headerNames.length;
    }

    /**
     * 行数を取得する
     *
     * @return 行数
     */
    @Override
    public int getRowCount() {
        return records.size();
    }

    /**
     * columnIndexとrowIndexにあるセルの値を返す。
     *
     * @param rowIndex    指定行
     * @param columnIndex 指定列
     * @return 指定されたセルのObject値
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BreakPointBean rowBean = records.get(rowIndex);
        Object ret = null;
        switch (columnIndex) {
            case 0:
                ret = rowIndex + 1;
                break;
            case 1:
                ret = rowBean.getType();
                break;
            case 2:
                ret = rowBean.getSourceName();
                break;
            case 3:
                ret = rowBean.getLine();
                break;
            case 4:
                ret = rowBean.isValid();
                break;
            default:
                ret = "???";
                break;
        }
        return ret;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return headerNames[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

}
