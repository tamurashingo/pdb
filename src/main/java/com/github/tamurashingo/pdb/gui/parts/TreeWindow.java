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
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.db.ConnectConfig;
import com.github.tamurashingo.pdb.gui.GUIEventCompile;
import com.github.tamurashingo.pdb.gui.GUIEventExec;
import com.github.tamurashingo.pdb.gui.GUIEventHandler;
import com.github.tamurashingo.pdb.gui.GUIEventOpenSource;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


/**
 * このクラスは{@link ProceduresBean}のリストをもとに、
 * ストアド一覧をツリー表示します。
 * ストアドをダブルクリックすると、ソースウィンドウに指定されたストアドのソースを表示します。
 * ストアドで右クリックすると、ソース表示、実行、再コンパイル、デバッグ可能コンパイルを実行できます。
 *
 * @author tamura shingo
 */
public class TreeWindow extends JPanel {

    private static final long serialVersionUID = -4228321521387926270L;

    /** ログ出力インスタンス */
    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    JScrollPane scrollPane;

    /** 右クリック時のポップアップメニュー */
    JPopupMenu popupMenu;

    /** ソース開く */
    TreeMenuItem openItem;

    /** 実行 */
    TreeMenuItem execItem;

    /** 再コンパイル */
    TreeMenuItem compileItem;

    /** デバッグ可能コンパイル */
    TreeMenuItem debugItem;

    /**
     * コンストラクタ
     */
    public TreeWindow() {
        scrollPane = new JScrollPane(null, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(scrollPane);

        popupMenu = new JPopupMenu();
        openItem = new TreeMenuItem("開く");
        execItem = new TreeMenuItem("実行");
        compileItem = new TreeMenuItem("コンパイル");
        debugItem = new TreeMenuItem("デバッグコンパイル");
        popupMenu.add(openItem);
        popupMenu.add(execItem);
        popupMenu.addSeparator();
        popupMenu.add(compileItem);
        popupMenu.add(debugItem);
    }

    /**
     * ストアド情報をもとに、ツリー情報を更新する。
     * @param list
     * @param handler
     */
    public void updateWindow(List<ProceduresBean> list, final GUIEventHandler handler) {
        log.trace("開始");

        final JTree tree = new JTree(createNode(list));

        tree.setTransferHandler(new ProceduresTreeTransferHandler());

        tree.setCellRenderer(new ProceduresTreeCellRender());

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int selRow = tree.getRowForLocation(event.getX(), event.getY());
                TreePath selPath = tree.getPathForLocation(event.getX(), event.getY());
                if (javax.swing.SwingUtilities.isLeftMouseButton(event)) {
                    if (selRow != -1) {
                        if (event.getClickCount() == 1) {
                            // click
                        }
                        else if (event.getClickCount() == 2) {
                            // double click
                            showSourceCheck(selPath, handler);
                        }
                    }
                }
                else if (javax.swing.SwingUtilities.isRightMouseButton(event)) {
                    if (selRow != -1) {
                        if (setPopupMenu(selPath, handler)) {
                            popupMenu.show(event.getComponent(), event.getX(), event.getY());
                        }
                    }
                }
            }
        });


        JViewport viewPort = scrollPane.getViewport();
        viewPort.setView(tree);

        log.trace("終了");
    }


    /**
     *
     * @param list
     * @return
     */
    private DefaultMutableTreeNode createNode(List<ProceduresBean> list) {
        log.trace("開始");

        StringBuilder buf = new StringBuilder();
        buf.append(ConnectConfig.getInstance().getUserid());
        buf.append("@");
        buf.append(ConnectConfig.getInstance().getDbname());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(buf.toString());
        Map<String, DefaultMutableTreeNode> typeMap = new HashMap<String, DefaultMutableTreeNode>();

        for (ProceduresBean bean : list) {
            DefaultMutableTreeNode node = typeMap.get(bean.getObjectType());
            if (node == null) {
                node = new DefaultMutableTreeNode(bean.getObjectType());
                typeMap.put(bean.getObjectType(), node);
                root.add(node);
            }
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(bean);
            node.add(child);
        }

        log.trace("終了");
        return root;
    }

    private void showSourceCheck(TreePath selPath, GUIEventHandler handler) {
        log.trace("開始");

        Object selComp = selPath.getLastPathComponent();
        if (selComp instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selComp;
            Object obj = node.getUserObject();
            if (obj instanceof ProceduresBean) {
                ProceduresBean bean = (ProceduresBean) obj;
                GUIEventOpenSource args = new GUIEventOpenSource();
                args.setSourceName(bean.getObjectName());
                args.setSourceType(bean.getObjectType());
                handler.processGuiEvent(GUIEventHandler.ID.SELECT_SOURCE, args);
            }
        }

        log.trace("終了");
    }


    private boolean setPopupMenu(TreePath selPath, final GUIEventHandler handler) {
        log.trace("開始");

        Object selComp = selPath.getLastPathComponent();
        if (selComp instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selComp;
            Object obj = node.getUserObject();
            if (obj instanceof ProceduresBean) {
                ProceduresBean bean = (ProceduresBean) obj;
                final String sourceName = bean.getObjectName();
                final String sourceType = bean.getObjectType();

                openItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUIEventOpenSource args = new GUIEventOpenSource();
                        args.setSourceName(sourceName);
                        args.setSourceType(sourceType);
                        handler.processGuiEvent(GUIEventHandler.ID.SELECT_SOURCE, args);
                    }
                });

                execItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUIEventExec args = new GUIEventExec();
                        args.setSourceName(sourceName);
                        args.setSourceType(sourceType);
                        handler.processGuiEvent(GUIEventHandler.ID.RUN, args);
                    }
                });

                compileItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUIEventCompile args = new GUIEventCompile();
                        args.setSourceName(sourceName);
                        args.setSourceType(sourceType);
                        args.setDebug(false);
                        handler.processGuiEvent(GUIEventHandler.ID.COMPILE_SOURCE, args);
                    }
                });

                debugItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        GUIEventCompile args = new GUIEventCompile();
                        args.setSourceName(sourceName);
                        args.setSourceType(sourceType);
                        args.setDebug(true);
                        handler.processGuiEvent(GUIEventHandler.ID.COMPILE_SOURCE, args);
                    }
                });

                log.trace("終了");
                return true;
            }
        }
        log.trace("終了");
        return false;
    }

}

class TreeMenuItem extends JMenuItem {

    private static final long serialVersionUID = -4265384215515822755L;

    ActionListener action;

    public TreeMenuItem(String text) {
        super(text);
    }

    @Override
    public void addActionListener(ActionListener l) {
        if (action != null) {
            removeActionListener(action);
        }
        action = l;
        super.addActionListener(action);
    }
}


class ProceduresTreeCellRender extends JPanel implements TreeCellRenderer {

    private static final long serialVersionUID = -8567551314776829414L;

    /** オフセット値 */
    private static final int OFFSET = 2;

    /** 項目名 */
    private String item;

    /** INVALID? */
    private String invalid;

    /** DEBUG可能? */
    private String debug;

    /** 項目名の幅 */
    private int itemWidth;

    /** INVALID?の幅 */
    private int invalidWidth;

    /** DEBUG可能?の幅 */
    private int debugWidth;

    /** 項目名のフォント情報 */
    private Font itemFont;

    /** 属性(INVALID/DEBUG可)のフォント情報 */
    private Font attFont;

    /** 項目名のFontMetrics */
    private FontMetrics itemMetrics;

    /** 属性(INVALID/DEBUG可)のFontMetrics */
    private FontMetrics attMetrics;

    /** 選択状態か */
    private boolean selected;

    /** フォーカスあたっているか */
    private boolean hasFocus;

    public ProceduresTreeCellRender() {
        itemFont = new Font("monospaced", Font.PLAIN, 16);
        attFont = new Font("monospaced", Font.BOLD, 12);
        itemMetrics = this.getFontMetrics(itemFont);
        attMetrics = this.getFontMetrics(attFont);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        Object obj = node.getUserObject();
        if (obj instanceof String) {
            item = obj.toString();
            invalid = "";
            debug = "";
            itemWidth = itemMetrics.stringWidth(item);
            invalidWidth = 0;
            debugWidth = 0;
        }
        else if (obj instanceof ProceduresBean) {
            ProceduresBean bean = (ProceduresBean) obj;
            item = bean.getObjectName();
            if (bean.isValid()) {
                invalid = "";
            }
            else {
                invalid = "INVALID";
            }
            if (bean.isDebuggable()) {
                debug = "";
            }
            else {
                debug = "NoDEBUG";
            }
            itemWidth = itemMetrics.stringWidth(item) + 8;
            invalidWidth = invalid.length() == 0 ? 0 : attMetrics.stringWidth(invalid) + 8;
            debugWidth = debug.length() == 0 ? 0 : attMetrics.stringWidth(debug) + 8;
        }

        this.selected = selected;
        this.hasFocus = hasFocus;

        setBackground(tree.getBackground());
        setMinimumSize(new Dimension(itemWidth + invalidWidth + debugWidth + OFFSET * 2, 22));
        setPreferredSize(new Dimension(itemWidth + invalidWidth + debugWidth + OFFSET * 2, 22));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        if (selected) {
            g.setColor(UIManager.getColor("Tree.selectionBackground"));
            g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
        }

        if (hasFocus) {
            g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        g.setFont(itemFont);
        g.setColor(Color.BLACK);
        g.drawString(item, OFFSET, 18);

        g.setFont(attFont);
        g.setColor(Color.RED);
        g.drawString(invalid, itemWidth, 18);
        g.drawString(debug, OFFSET + itemWidth + invalidWidth, 18);
    }

}


class ProceduresTreeTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 2208478305401598055L;

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
        if (comp instanceof JTree) {
            JTree tree = (JTree) comp;
            TreePath path = tree.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object obj = node.getUserObject();
            if (obj instanceof String) {
                StringSelection ss = new StringSelection((String) obj);
                clip.setContents(ss, ss);
            }
            else if (obj instanceof ProceduresBean) {
                ProceduresBean bean = (ProceduresBean) obj;
                StringSelection ss = new StringSelection(bean.getObjectName());
                clip.setContents(ss, ss);
            }
        }
    }
}
