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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.db.DBException;
import com.github.tamurashingo.pdb.db.DBManager;
import com.github.tamurashingo.pdb.gui.GUIEventHandler;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Log.Level;
import com.github.tamurashingo.pdb.log.Logger;

public class SourceWindow extends JPanel {

    private static final long serialVersionUID = 556246568398527717L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private JTabbedPane mainPane;

    private GUIEventHandler eventHandler;

    private Map<String, SourceView> openedMap;

    public SourceWindow(GUIEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.openedMap = new HashMap<String, SourceView>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mainPane = new JTabbedPane();
        mainPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        add(mainPane);
    }

    public void addSource(String sourceName, String sourceType, String source) {
        log.trace("開始");

        String key = createKey(sourceName, sourceType);
        SourceView sourceView = openedMap.get(key);

        try {
            if (sourceView == null) {
                if (source == null) {
                    DBManager db = DBManager.getInstance();
                    source = db.getSource(sourceName, sourceType);
                }
                sourceView = new SourceView(eventHandler, sourceName, sourceType, source);
                JPanel tab = new JPanel(new BorderLayout());
                tab.setOpaque(false);
                JLabel label = new JLabel(sourceName);
                label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
                JButton button = new JButton(new javax.swing.plaf.metal.MetalIconFactory.PaletteCloseIcon());
                button.setMargin(new Insets(0, 0, 0, 0));
                tab.add(label, BorderLayout.WEST);
                tab.add(button, BorderLayout.EAST);
                tab.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));

                //			sourceView.updateBreakpoint();
                openedMap.put(key, sourceView);
                mainPane.addTab(null, sourceView);

                int index = mainPane.indexOfComponent(sourceView);

                mainPane.setTabComponentAt(index, tab);

                button.addActionListener(new CloseAction(key, index));
            }
        }
        catch (DBException ex) {
            log.printStackTrace(Level.WARN, ex);
            log.warn("ソース読み込みに失敗しました:%s.%s:%s", sourceName, sourceType, ex.getMessage());
        }

        mainPane.setSelectedIndex(mainPane.indexOfComponent(sourceView));

        log.trace("終了");
    }

    public void updateBreakPoint(String sourceName, String sourceType, List<BreakPointBean> list) {
        log.trace("開始");

        String key = createKey(sourceName, sourceType);
        SourceView view = openedMap.get(key);
        if (view != null) {
            view.updateBreakPoint(list);
        }

        log.trace("終了");
    }

    public void updateBreakPointSourceView(String sourceName, String sourceType, int line) {
        log.trace("開始");

        addSource(sourceName, sourceType, null);
        String key = createKey(sourceName, sourceType);
        SourceView view = openedMap.get(key);
        view.setView(line);

        log.trace("終了");
    }

    public void updateSourceView() {
        log.trace("開始");

        Component comp = mainPane.getSelectedComponent();
        if (comp != null) {
            comp.repaint();
        }

        log.trace("終了");
    }

    private String createKey(String sourceName, String sourceType) {
        log.trace("開始");

        StringBuilder buf = new StringBuilder();
        buf.append(sourceName);
        buf.append(":");
        buf.append(sourceType);

        log.trace("終了");
        return buf.toString();
    }

    private class CloseAction implements ActionListener {

        private String key;

        private int index;

        CloseAction(String key, int index) {
            this.key = key;
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            log.trace("開始");

            mainPane.removeTabAt(index);
            openedMap.remove(key);

            log.trace("終了");
        }
    }
}
