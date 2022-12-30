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
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.gui.GUIEventHandler;
import com.github.tamurashingo.pdb.gui.GUIEventSelectBreakpoint;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;


public class SourceView extends JScrollPane {

    private static final long serialVersionUID = -8000433411650976799L;

    private static final Log log;

    private static final Polygon polygon;

    static {
        int[] xpoints = new int[] {
                0, 7, 7, 8, 15, 15, 8, 7, 7, 0
        };
        int[] ypoints = new int[] {
                6, 6, 0, 0, 7, 8, 15, 15, 9, 9
        };
        log = Logger.getLogger();
        polygon = new Polygon(xpoints, ypoints, xpoints.length);
    }

    private final GUIEventHandler eventHandler;

    private final String sourceName;

    private final String sourceType;

    private LineNumberView rowView;

    private final JTextPane textArea;

    public SourceView(GUIEventHandler eventHandler, String sourceName, String sourceType, String source) {
        super();
        log.trace("SourceView start");
        this.eventHandler = eventHandler;
        this.sourceName = sourceName;
        this.sourceType = sourceType;

        log.debug("sourceName:%s", sourceName);
        log.debug("sourceType:%s", sourceType);

        this.textArea = new JTextPane() {
            private static final long serialVersionUID = -2004005728035808305L;

            @Override
            public boolean getScrollableTracksViewportWidth() {
                Object parent = getParent();
                if (parent instanceof JViewport) {
                    JViewport port = (JViewport) parent;
                    int w = port.getWidth();
                    ComponentUI ui = getUI();
                    Dimension sz = ui.getPreferredSize(this);
                    if (sz.width < w) {
                        return true;
                    }
                }
                return false;
            }
        };
        this.textArea.setText(source);
        createComponent();
        doStyle();
        log.trace("SourceView end");
    }

    public void updateBreakPoint(List<BreakPointBean> list) {
        log.trace("開始");
        rowView.updateBreakPoint(list);
        log.trace("終了");
    }

    public void setView(int line) {
        log.trace("開始");

        final JScrollBar vbar = this.getVerticalScrollBar();
        int maxLine = getMaxLine();
        int maxSize = vbar.getMaximum();
        int pos = (line - getViewLine() / 2) * maxSize / maxLine;

        if (pos < vbar.getMinimum()) {
            pos = vbar.getMinimum() + 1;
        }
        else if (pos > vbar.getMaximum()) {
            pos = vbar.getMaximum() - 1;
        }

        final int newPos = pos;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                vbar.setValue(newPos);
                rowView.repaint();
            }
        });

        log.trace("終了");
    }

    public int getMaxLine() {
        log.trace("開始");
        Document doc = textArea.getDocument();
        log.trace("終了");
        return doc.getDefaultRootElement().getElementIndex(doc.getLength()) + 1;
    }

    /**
     * 現在のViewが何行表示できているか
     * @return
     */
    public int getViewLine() {
        log.trace("開始");

        int height = getHeight();
        FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
        int fontHeight = fontMetrics.getHeight();

        log.trace("終了");
        return height / fontHeight;
    }

    // TODO: 将来のために
    public void showBreak(int line) {
    }

    // TODO: 将来のために
    public void hideBreak() {
    }

    private void createComponent() {
        log.trace("開始");

        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        rowView = new LineNumberView(textArea);

        setViewportView(textArea);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setRowHeaderView(rowView);

        textArea.setCaretPosition(0);
        textArea.setEditable(false);

        log.trace("終了");
    }

    private void doStyle() {
        log.trace("開始");

        FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
        int charWidth = fontMetrics.charWidth('m');
        int tabLength = charWidth * 4;
        TabStop[] tabs = new TabStop[10];
        for (int ix = 0; ix < tabs.length; ix++) {
            tabs[ix] = new TabStop((ix + 1) * tabLength);
        }
        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setTabSet(attrs, tabSet);
        int len = textArea.getDocument().getLength();
        textArea.getStyledDocument().setParagraphAttributes(0, len, attrs, false);

        log.trace("終了");
    }


    private class LineNumberView extends JComponent {

        private static final long serialVersionUID = -3208462538612139143L;

        private static final int MARGIN = 5;

        private static final int BREAKPOINT_WIDTH = 10;

        private static final int CURRENT_WIDTH = 16;


        private final JTextPane textArea;

        private final FontMetrics fontMetrics;

        private final int fontHeight;

        private final int fontAscent;

        private final int topInset;

        private final Color BREAK = new Color(0x800000);

        private Map<Integer, Boolean> breakPoints;

        public LineNumberView(JTextPane textArea) {
            this.textArea = textArea;
            this.fontMetrics = getFontMetrics(textArea.getFont());
            this.fontHeight = fontMetrics.getHeight();
            this.fontAscent = fontMetrics.getAscent();
            this.topInset = textArea.getInsets().top;

            this.breakPoints = new HashMap<Integer, Boolean>();

            textArea.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent e) {
                    repaint();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    repaint();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            this.textArea.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent event) {
                    revalidate();
                    repaint();
                }
            });

            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        int line = getLineAtPoint(event.getPoint().y) + 1;
                        GUIEventSelectBreakpoint guiEvent = new GUIEventSelectBreakpoint();
                        guiEvent.setSourceName(sourceName);
                        guiEvent.setSourceType(sourceType);
                        guiEvent.setLine(line);
                        eventHandler.processGuiEvent(GUIEventHandler.ID.SELECT_BREAKPOINT, guiEvent);
                    }
                }
            });
        }

        public void updateBreakPoint(List<BreakPointBean> list) {
            if (list != null) {
                breakPoints.clear();
                for (Iterator<BreakPointBean> it = list.iterator(); it.hasNext(); ) {
                    BreakPointBean bean = it.next();
                    if (bean.isValid()) {
                        breakPoints.put(bean.getLine(), true);
                        log.warn("breakpoint:%s:%s:%d", sourceName, sourceType, bean.getLine());
                    }
                }
                repaint();
            }
        }

        private int getComponentWidth() {
            Document doc = textArea.getDocument();
            Element root = doc.getDefaultRootElement();
            int lineCount = root.getElementIndex(doc.getLength());
            int maxDigits = Math.max(3, String.valueOf(lineCount).length());
            return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2 + BREAKPOINT_WIDTH + CURRENT_WIDTH;
        }

        private int getLineAtPoint(int y) {
            Element root = textArea.getDocument().getDefaultRootElement();
            int pos = textArea.viewToModel(new Point(0, y));
            return root.getElementIndex(pos);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(getComponentWidth(), textArea.getHeight());
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D gr = (Graphics2D) g;
            boolean breakFlag = false;
            int breakLine = -1;
            if (sourceName.equals(Globals.getBreakSource()) && sourceType.equals(Globals.getBreakType())) {
                breakFlag = true;
                breakLine = Globals.getBreakLine();
            }
            Rectangle clip = gr.getClipBounds();
            gr.setColor(getBackground());
            gr.fillRect(clip.x, clip.y, clip.width, clip.height);
            gr.setColor(getForeground());
            int base = clip.y - topInset;
            int start = getLineAtPoint(base);
            int end = getLineAtPoint(base + clip.height);
            int y = topInset - fontHeight + fontAscent + start * fontHeight;
            for (int ix = start; ix <= end; ix++) {
                String text = String.valueOf(ix + 1);
                int x = getComponentWidth() - MARGIN - fontMetrics.stringWidth(text);

                Boolean flag = breakPoints.get(ix + 1);
                if (flag != null && flag == true) {
//					gr.fillRect( MARGIN + CURRENT_WIDTH, y + 2, BREAKPOINT_WIDTH, fontHeight - 2 );
                    gr.setColor(BREAK);
                    gr.fillOval(MARGIN + CURRENT_WIDTH, y + 2, BREAKPOINT_WIDTH, fontHeight - 2);
                    gr.setColor(getForeground());
                }
                if (breakFlag == true && breakLine == (ix + 1)) {
                    gr.setColor(Color.RED);
                    polygon.translate(MARGIN, (y + 2));
                    gr.fillPolygon(polygon);
                    polygon.translate(-MARGIN, -(y + 2));
                    gr.setColor(getForeground());
                }

                y = y + fontHeight;
                gr.drawString(text, x, y);
            }
        }
    }
}
