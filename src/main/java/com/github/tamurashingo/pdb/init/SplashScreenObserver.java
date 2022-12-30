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
package com.github.tamurashingo.pdb.init;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

/**
 * SplashScreenを指定して起動した場合に、SplashScreenに初期化状況を更新する。
 *
 * @author tamura shingo
 */
public class SplashScreenObserver implements InitializeObserver {

    private SplashScreen screen;
    private Graphics2D gr;
    private boolean flag;

    /**
     * コンストラクタ
     */
    public SplashScreenObserver() {
        try {
            screen = SplashScreen.getSplashScreen();
            if (screen != null) {
                screen.setImageURL(SplashScreenObserver.class.getClassLoader().getResource("images/logo.png"));
                gr = screen.createGraphics();
                flag = true;
                gr.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            } else {
                flag = false;
            }
        } catch (Exception ex) {
            // 何もしない
            flag = false;
        }
    }

    @Override
    public void tellStart(String clsName, String desc) {
        if (flag) {
            gr.setColor(Color.BLACK);
            gr.drawString("Loading " + clsName + "...", 38, 170);
            screen.update();
        }
    }

    @Override
    public void tellEnd(String clsName, String desc) {
        if (flag) {
            gr.setColor(Color.WHITE);
            FontMetrics fm = gr.getFontMetrics();
            int width = fm.stringWidth("Loading " + clsName + "...");
            int ascent = fm.getMaxAscent();
            int descent = fm.getMaxDescent();
            gr.fillRect(38, 170 - ascent, width, ascent + descent);
        }
    }

}
