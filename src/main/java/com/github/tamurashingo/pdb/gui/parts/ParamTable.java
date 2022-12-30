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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.ListIterator;

import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


/**
 * パラメータ表示用テーブルのクラス。
 * {@link ParamsBean}をもとに、パラメータの表示を行う。
 * INパラメータについては入力可能とする。
 *
 * @author tamura shingo
 */
public class ParamTable extends JTable {

    private static final long serialVersionUID = -4976372837058360708L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    /** テーブル表示用model */
    private ParamModel model;


    /**
     * コンストラクタ
     * @param list 表示するパラメータのリスト
     */
    public ParamTable(List<ParamsBean> list) {
        super();
        makeComponent(list);
    }


    /**
     * テーブルを構築する。
     * @param list 表示するパラメータのリスト
     */
    private void makeComponent(List<ParamsBean> list) {
        log.trace("開始");

        model = new ParamModel(list);
        TableRowSorter<ParamModel> sorter = model.getSorter();
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setModel(model);
        this.setRowSorter(sorter);
        this.setRowHeight(Math.max(TreeIcon.getMaxHeight(), this.getRowHeight()) + 1);

        TableColumn col = this.getColumnModel().getColumn(ParamModel.TOGGLE);
        CellUtil util = new CellUtil();
        col.setCellRenderer(util);
        col.setCellEditor(util);

        log.trace("終了");
    }


    /**
     * 入力されたパラメータ情報を取得する。
     * @return 入力されたパラメータのリスト
     */
    public List<ParamsBean> getParams() {
        log.trace("開始");
        log.trace("終了");
        return model.getRawData();
    }

}


/**
 * テーブル表示用に拡張したパラメータBean。
 * @author Tamura Shingo
 *
 */
class ParamsBeanEx extends ParamsBean {

    private static final long serialVersionUID = 8952232180748654269L;

    private List<ParamsBeanEx> parent;

    /**
     * コンストラクタ
     * @param bean パラメータBean
     */
    public ParamsBeanEx(ParamsBean bean) {
        setArgumentName(bean.getArgumentName());
        setDataLevel(bean.getDataLevel());
        setDataType(bean.getDataType());
        setInOut(bean.getInOut());
        setPosition(bean.getPosition());
        setValue(bean.getValue());
        this.parent = new ArrayList<ParamsBeanEx>();
    }


    public void addParent(ParamsBeanEx parent) {
        if (parent != null) {
            this.parent.add(parent);
        }
    }

    public void addParents(List<ParamsBeanEx> parents) {
        if (parents != null) {
            this.parent.addAll(parents);
        }
    }

    public List<ParamsBeanEx> getParents() {
        return parent;
    }
}


interface CallBack {
    public void click(int row, boolean flag);
}

class ParamModel extends AbstractTableModel implements CallBack {

    private static final long serialVersionUID = -194422831100571305L;

    private String[] headerNames = {
            "",
            "Argument",
            "Type",
            "IN/OUT",
            "Value",
    };

    static final int TOGGLE = 0;

    static final int ARGUMENT = 1;

    static final int TYPE = 2;

    static final int INOUT = 3;

    static final int VALUE = 4;

    /** 各レコードの情報（表示用） */
    private List<Object[]> records;

    /** 各レコードの情報（生情報） */
    private List<ParamsBeanEx> beans;

    private List<ParamsBean> rawData;

    /** フィルタ情報 */
    private List<RowFilter<TableModel, Integer>> filters;

    private List<RowFilter<TableModel, Integer>> activeFilters;

    /** sorter */
    private TableRowSorter<ParamModel> sorter;

    public ParamModel(List<ParamsBean> list) {
        beans = new ArrayList<ParamsBeanEx>();
        records = new ArrayList<Object[]>();
        filters = new ArrayList<RowFilter<TableModel, Integer>>();
        activeFilters = new ArrayList<RowFilter<TableModel, Integer>>();
        sorter = new TableRowSorter<ParamModel>(this);
        rawData = list;

        Object[] rows = new Object[headerNames.length];
        rows[TOGGLE] = new TreeIcon(true, -1, this);
        filters.add(new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(
                    javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
                if (entry.getIdentifier() == 0) {
                    return true;
                }
                return false;
            }
        });
        rows[ARGUMENT] = "ARGUMENT";
        rows[TYPE] = "";
        rows[INOUT] = "";
        rows[VALUE] = "";
        records.add(rows);
        beans.add(new ParamsBeanEx(new ParamsBean()));


        makeRecords(list.listIterator(), -1, 0, null, null);
    }

    public ParamsBeanEx getBean(int row) {
        return beans.get(row);
    }

    public List<ParamsBean> getRawData() {
        return rawData;
    }

    @Override
    public void click(int row, boolean flag) {

        if (flag == true) {
            activeFilters.add(filters.get(row));
        }
        else {
            activeFilters.remove(filters.get(row));
        }
        sorter.setRowFilter(RowFilter.andFilter(activeFilters));

    }

    public TableRowSorter<ParamModel> getSorter() {
        return sorter;
    }

    private void makeRecords(ListIterator<ParamsBean> iterator, int baseLevel, int depth, ParamsBeanEx parent, List<ParamsBeanEx> parents) {
        while (iterator.hasNext()) {
            ParamsBean bean = iterator.next();
            if (bean.getDataLevel() <= baseLevel) {
                iterator.previous();
                break;
            }

            ParamsBeanEx beanEx = new ParamsBeanEx(bean);
            beanEx.addParent(parent);
            beanEx.addParents(parents);
            beans.add(beanEx);
            Object[] rows = new Object[headerNames.length];
            records.add(rows);

            for (int ix = 0; ix < headerNames.length; ix++) {
                switch (ix) {
                    case TOGGLE:
                        if (bean.getDataType().equals("VARRAY") || bean.getDataType().equals("PL/SQL RECORD")) {
                            rows[ix] = new TreeIcon(true, depth, this);
                            RowFilter<TableModel, Integer> filter = makeRowFilter(beanEx);
                            filters.add(filter);
                        }
                        else {
                            rows[ix] = new TreeIcon(false, depth, this);
                            filters.add(normalFilter);
                        }
                        break;
                    case ARGUMENT:
                        StringBuilder buf = new StringBuilder();
                        for (int iy = 0; iy < bean.getDataLevel(); iy++) {
                            buf.append("  ");
                        }
                        buf.append(bean.getArgumentName());
                        rows[ix] = buf.toString();
                        break;
                    case TYPE:
                        rows[ix] = bean.getDataType();
                        break;
                    case INOUT:
                        rows[ix] = bean.getInOut();
                        break;
                    case VALUE:
                        rows[ix] = bean.getValue();
                        break;
                    default:
                        break;
                }
            }
            if (bean.getDataType().equals("VARRAY") || bean.getDataType().equals("PL/SQL RECORD")) {
                makeRecords(iterator, bean.getDataLevel(), depth + 1, beanEx, beanEx.getParents());
            }
        }
    }

    private RowFilter<TableModel, Integer> makeRowFilter(final ParamsBeanEx targetBean) {
        return new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                ParamModel model = (ParamModel) entry.getModel();
                ParamsBeanEx beanEx = model.getBean(entry.getIdentifier());
                for (ParamsBeanEx bean : beanEx.getParents()) {
                    if (targetBean == bean) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private static RowFilter<TableModel, Integer> normalFilter = new RowFilter<TableModel, Integer>() {
        @Override
        public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
            return true;
        }
    };


    /**
     * 列数を取得する。
     * @return 列数
     */
    @Override
    public int getColumnCount() {
        return headerNames.length;
    }

    /**
     * 行数を取得する。
     * @return 行数
     */
    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] rowValues = records.get(rowIndex);
        return rowValues[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Object[] rowValues = records.get(rowIndex);
        rowValues[columnIndex] = aValue;

        if (columnIndex == VALUE) {
            ParamsBean bean = rawData.get(rowIndex - 1);
            bean.setValue((String) aValue);
            rawData.set(rowIndex - 1, bean);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return headerNames[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == TOGGLE) {
            return true;
        }
        else if (columnIndex == VALUE) {
            Object obj = getValueAt(rowIndex, INOUT);
            if ("IN".equals(obj) || "IN/OUT".equals(obj)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }
}

class TreeIcon extends JComponent {
    private static final long serialVersionUID = 3591990165607221724L;

    private static final int MARGIN = 5;

    private static final Icon icon1 = UIManager.getIcon("Tree.collapsedIcon");

    private static final Icon icon2 = UIManager.getIcon("Tree.expandedIcon");

    private static final Icon icon3 = UIManager.getIcon("Tree.closedIcon");

    private static final Icon icon4 = UIManager.getIcon("Tree.openIcon");

    private static final Icon icon5 = UIManager.getIcon("Tree.leafIcon");

    private static final int toggleWidth;

    private static final int toggleHeight;

    @SuppressWarnings("unused")
    private static final int parentWidth;

    private static final int parentHeight;

    @SuppressWarnings("unused")
    private static final int childWidth;

    private static final int childHeight;

    private static final int maxHeight;

    static {
        toggleWidth = Math.max(icon1.getIconWidth(), icon2.getIconHeight());
        toggleHeight = Math.max(icon1.getIconWidth(), icon2.getIconHeight());
        parentWidth = Math.max(icon3.getIconWidth(), icon4.getIconWidth());
        parentHeight = Math.max(icon3.getIconHeight(), icon4.getIconHeight());
        childWidth = icon5.getIconWidth();
        childHeight = icon5.getIconHeight();
        maxHeight = Math.max(Math.max(toggleHeight, parentHeight), childHeight);
    }

    private Icon iconToggle;

    private Icon iconItem;

    private int depth;

    private boolean isExpanded;

    public static int getMaxHeight() {
        return maxHeight;
    }

    private transient CallBack callBack = new CallBack() {
        @Override
        public void click(int row, boolean flag) {
        }
    };

    public TreeIcon(boolean parent, int depth, CallBack callBack) {

        if (parent == true) {
            iconToggle = icon2;
            iconItem = icon4;
        }
        else {
            iconToggle = null;
            iconItem = icon5;
        }
        this.depth = depth;
        this.isExpanded = true;
        this.callBack = callBack;
    }

    public void setExpanded(boolean flag) {
        isExpanded = flag;
    }

    public void flip() {
        isExpanded = isExpanded == true ? false : true;
        if (iconToggle != null) {
            iconToggle = iconToggle == icon1 ? icon2 : icon1;
            iconItem = iconItem == icon3 ? icon4 : icon3;
        }
        setExpanded(isExpanded);
    }

    public void click(int row) {
        callBack.click(row, isExpanded == false);
    }

    public void setLevel(int level) {
        this.depth = level;
    }

    @Override
    public void paint(Graphics g) {
        if (depth == -1) {
            iconItem.paintIcon(this, g, 0, 0);
        }
        else {
            if (iconToggle != null) {
                iconToggle.paintIcon(this, g, (toggleWidth) * depth, 0);
            }
            iconItem.paintIcon(this, g, (toggleWidth) * depth + toggleWidth + MARGIN, 0);
        }
    }
}

class CellUtil implements TableCellRenderer, TableCellEditor {

    JComponent component = new JComponent() {
        private static final long serialVersionUID = -882959314909581876L;
    };


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        int r = table.convertRowIndexToModel(row);
        if (r >= 0) {
            TreeIcon icon = (TreeIcon) table.getModel().getValueAt(r, column);
            return icon;
        }
        else {
            return component;
        }
    }

    @Override
    public void addCellEditorListener(CellEditorListener arg0) {
    }

    @Override
    public void cancelCellEditing() {
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public boolean isCellEditable(EventObject arg0) {
        return true;
    }

    @Override
    public void removeCellEditorListener(CellEditorListener arg0) {
    }

    @Override
    public boolean shouldSelectCell(EventObject arg0) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        int r = table.convertRowIndexToModel(row);
        if (r >= 0) {
            TreeIcon icon = (TreeIcon) table.getModel().getValueAt(r, column);
            icon.flip();
            icon.click(r);
            return icon;
        }
        else {
            return component;
        }
    }

}
