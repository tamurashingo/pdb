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


import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import com.github.tamurashingo.pdb.bean.VariablesBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

import static com.github.tamurashingo.pdb.util.Util.nullToBlank;


/**
 * 変数を表示する
 *
 * @author tamura shingo
 */
public class VariableView extends JScrollPane {

    private static final long serialVersionUID = 7381282378365623314L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private VariableTreeCellRenderer renderer;

    public VariableView() {
        renderer = new VariableTreeCellRenderer();
    }

    public void updateVariable(List<VariablesBean> variables) {
        log.trace("開始");

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("variable");
        renderer.init();
        createNode(root, variables, 0);

        JTree tree = new JTree(root);
        tree.setCellRenderer(renderer);
        tree.setTransferHandler(new VariableTreeTransferHandler());

//		tree.addTreeWillExpandListener( new TreeWillExpandListener() {
//			@Override
//			public void treeWillExpand( TreeExpansionEvent event ) throws ExpandVetoException {
//			}
//			@Override
//			public void treeWillCollapse( TreeExpansionEvent event ) throws ExpandVetoException {
//			}
//		});

        JViewport viewPort = getViewport();
        viewPort.setView(tree);
        repaint();

        log.trace("終了");
    }

    private void createNode(DefaultMutableTreeNode root, List<VariablesBean> list, int level) {
        log.trace("開始");

        if (list != null) {
            for (VariablesBean bean : list) {

                String name = bean.getVariableName();
                String type = bean.getVariableType();
                List<VariablesBean> l = bean.getVariables();

                renderer.updateValue(name, type, bean.getValue(), level);
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(bean);
                root.add(child);

                if (!(l == null || l.isEmpty())) {
                    createNode(child, l, level + 1);
                }
            }
        }

        log.trace("終了");
    }
}

class VariableTreeCellRenderer extends JPanel implements TreeCellRenderer {

    private static final long serialVersionUID = -6311761163755924906L;

    private static final int MARGIN;

    private static final int LEFT_MARGIN;

    static {
        MARGIN = UIManager.getInt("Tree.leftChildIndent") + UIManager.getInt("Tree.rightChildIndent");
        LEFT_MARGIN = UIManager.getInt("Tree.leftChildIndent");
    }

    private Font font;

    private FontMetrics fontMetrics;

    private int maxVariablePos;

    private int maxTypePos;

    private int maxValuePos;

    private int maxVariableWidth;

    private int maxTypeWidth;

    private int maxValueWidth;

    private int maxWidth;


    private String variable;

    private String type;

    private String value;

    private int level;


    private boolean selected;

    private boolean hasFocus;

    public VariableTreeCellRenderer() {
        font = new Font("monospaced", Font.PLAIN, 14);
        fontMetrics = this.getFontMetrics(font);
    }

    public void init() {
        maxVariablePos = maxTypePos = maxValuePos = 0;
        maxVariableWidth = maxTypeWidth = maxValueWidth = 0;
        maxWidth = 0;
    }

    public void updateValue(String name, String type, String value, int level) {

        int variablePos = LEFT_MARGIN * level;
        int variableWidth = fontMetrics.stringWidth(nullToBlank(name));
        if (variablePos > maxVariablePos) {
            maxVariablePos = variablePos;
        }
        if (variableWidth > maxVariableWidth) {
            maxVariableWidth = variableWidth;
        }

        int typePos = maxVariablePos + MARGIN + maxVariableWidth;
        int typeWidth = fontMetrics.stringWidth(nullToBlank(type));
        if (typePos > maxTypePos) {
            maxTypePos = typePos;
        }
        if (typeWidth > maxTypeWidth) {
            maxTypeWidth = typeWidth;
        }

        int valuePos = maxTypePos + MARGIN + maxTypeWidth;
        int valueWidth = fontMetrics.stringWidth(nullToBlank(value));
        if (valuePos > maxValuePos) {
            maxValuePos = valuePos;
        }
        if (valueWidth > maxValueWidth) {
            maxValueWidth = valueWidth;
        }

        int width = maxValuePos + MARGIN + maxValueWidth + MARGIN;
        if (width > maxWidth) {
            maxWidth = width;
        }
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object val,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) val;

        TreeNode parent = node.getParent();
        level = -1;
        while (parent != null) {
            level++;
            parent = parent.getParent();
        }

        Object obj = node.getUserObject();
        if (obj instanceof String) {
            variable = nullToBlank(obj.toString());
            type = "";
            value = "";
        }
        else if (obj instanceof VariablesBean) {
            VariablesBean bean = (VariablesBean) obj;
            variable = nullToBlank(bean.getVariableName());
            type = nullToBlank(bean.getVariableType());
            value = nullToBlank(bean.getValue());
        }

        this.selected = selected;
        this.hasFocus = hasFocus;

        setBackground(tree.getBackground());
        setMinimumSize(new Dimension(maxWidth, 22));
        setPreferredSize(new Dimension(maxWidth, 22));

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

        g.setFont(font);
        g.setColor(Color.BLACK);
//		g.drawString( variable, maxVariablePos - (level * LEFT_MARGIN), 16 );
        g.drawString(variable, LEFT_MARGIN, 16);
        g.drawString(type, maxTypePos - (level * MARGIN), 16);
        g.drawString(value, maxValuePos - (level * MARGIN), 16);

    }
}


class VariableTreeTransferHandler extends TransferHandler {

    private static final long serialVersionUID = -1527874000034864248L;

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
            else if (obj instanceof VariablesBean) {
                VariablesBean bean = (VariablesBean) obj;
                StringSelection ss = new StringSelection(bean.getValue());
                clip.setContents(ss, ss);
            }
        }
    }
}
