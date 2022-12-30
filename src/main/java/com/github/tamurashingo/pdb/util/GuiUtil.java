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
package com.github.tamurashingo.pdb.util;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUIユーティリティ関連。
 *
 * @author tamura shingo
 */
public class GuiUtil {

    private GuiUtil() {
    }


    /**
     * 指定したウィンドウを画面の中央に表示させる。
     *
     * @param window 中央に表示するwindow
     */
    public static void showCenter(java.awt.Window window) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (int) ((d.getWidth() - window.getWidth()) / 2);
        int y = (int) ((d.getHeight() - window.getHeight()) / 2);
        window.setLocation(x, y);

        window.setVisible(true);
    }

    /**
     * {@code Throwable}のスタックトレースをダイアログで表示する。
     *
     * @param parent 親ウィンドウ
     * @param modal  trueならモーダル
     * @param ex     表示する{@code Throwable}情報
     */
    public static void showException(Frame parent, boolean modal, Throwable ex) {
        final JDialog dialog = new JDialog(parent, modal);
        JScrollPane scrollPane = new JScrollPane();
        dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
        dialog.setTitle("StackTrace");
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        dialog.getContentPane().add(button, BorderLayout.SOUTH);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("StackTrace");
        while (ex != null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(ex.getClass().getName() + ":" + ex.getMessage());
            for (StackTraceElement elm : ex.getStackTrace()) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(elm.toString());
                node.add(child);
            }
            root.add(node);
            ex = ex.getCause();
        }

        JTree tree = new JTree(root);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setView(tree);

        dialog.pack();

        showCenter(dialog);

    }


    /**
     * 警告メッセージを表示する。
     *
     * @param title   ウィンドウタイトル
     * @param message 警告メッセージ
     */
    public static void showAlert(String title, String message) {
        JOptionPane alert = new JOptionPane(message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = alert.createDialog(null, title);
        showCenter(dialog);
    }

    /**
     * メッセージを表示する。
     *
     * @param title   ウィンドウタイトル
     * @param message メッセージ
     */
    public static void showMessage(String title, String message) {
        JOptionPane msg = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = msg.createDialog(null, title);
        showCenter(dialog);
    }

}
