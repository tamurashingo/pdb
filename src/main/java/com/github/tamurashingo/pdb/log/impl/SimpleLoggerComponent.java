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
package com.github.tamurashingo.pdb.log.impl;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.github.tamurashingo.pdb.log.AbstractLog;
import com.github.tamurashingo.pdb.log.Log;

/**
 * ログ出力の実装サンプル。
 * ログ出力用コンポーネントを作成し、そこにログを出力する。
 * {@code TRACE}レベルはクラス名、メソッド名、発生行数を出力する。
 *
 * @author tamura shingo
 */
public class SimpleLoggerComponent extends JPanel implements Log {

    /**
     * ログ出力I/F
     */
    transient private Log log;

    /**
     * ログ出力エリア
     */
    private JTextArea logHistory;

    /**
     * ログ出力時のフォーマット
     */
    private static final String logFormat;
    /**
     * 日付フォーマット
     */
    private static final SimpleDateFormat fmt;
    /**
     * ログレベルを文字に変換するためのマップ
     */
    private static final Map<Log.Level, String> map;

    static {
        logFormat = "%s [%s]: %s" + System.getProperty("line.separator");
        fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        map = new HashMap<Log.Level, String>() {
            {
                put(Level.WARN, "重要");
                put(Level.INFO, "通知");
                put(Level.DEBUG, "詳細");
                put(Level.TRACE, "調査");
            }
        };
    }

    /**
     * コンストラクタ
     */
    public SimpleLoggerComponent() {
        super();
        log = new AbstractLog() {
            @Override
            public void log(Level level, String format, Object... args) {
                appendLog(level, format, args);
            }
        };
        log.setLogLevel(Level.NONE);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        logHistory = new JTextArea(0, 0);
        logHistory.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logHistory);

        add(scrollPane);
    }

    /**
     * ログをログ出力エリアへ追加する。
     *
     * @param level  ログレベル
     * @param format フォーマット
     * @param args   パラメータ
     */
    private void appendLog(Log.Level level, String format, Object... args) {
        Calendar cal = Calendar.getInstance();
        String date = "";
        synchronized (fmt) {
            date = fmt.format(cal.getTime());
        }
        String cont = String.format(format, args);
        String log = String.format(logFormat, date, map.get(level), cont);
        logHistory.append(log);
        logHistory.setCaretPosition(logHistory.getText().length());
    }

    @Override
    public void printStackTrace(Level level, Throwable ex) {
        log.printStackTrace(level, ex);
    }

    @Override
    public void warn(String format, Object... args) {
        log.warn(format, args);
    }

    @Override
    public void info(String format, Object... args) {
        log.info(format, args);
    }

    @Override
    public void debug(String format, Object... args) {
        log.debug(format, args);
    }

    @Override
    public void trace(String format, Object... args) {
        if (log.getLogLevel().equals(Log.Level.TRACE)) {
            StringBuilder buf = new StringBuilder();
            buf.append("[%s.%s(%d)] ");
            buf.append(format);
            Throwable t = new Throwable();
            StackTraceElement[] elm = t.getStackTrace();

            Object[] obj = new Object[args.length + 3];

            /* 0 : ここ
             * 1 : ProxyLogger
             * 2 : 呼び出し元
             * なので配列の２番目を取得しておく。
             */
            obj[0] = elm[2].getClassName();
            obj[1] = elm[2].getMethodName();
            obj[2] = elm[2].getLineNumber();
            System.arraycopy(args, 0, obj, 3, args.length);

            log.trace(buf.toString(), obj);
        }
    }

    @Override
    public void setLogLevel(Level level) {
        log.setLogLevel(level);
    }

    @Override
    public Level getLogLevel() {
        return log.getLogLevel();
    }

    @Override
    public boolean isWarn() {
        return log.isWarn();
    }

    @Override
    public boolean isInfo() {
        return log.isInfo();
    }

    @Override
    public boolean isDebug() {
        return log.isDebug();
    }

    @Override
    public boolean isTrace() {
        return log.isTrace();
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        SimpleLoggerComponent log = new SimpleLoggerComponent();
        frame.add(log);

        frame.setVisible(true);

        log.setLogLevel(Level.TRACE);
        log.trace("%s", "トレース");
        log.debug("%s", "デバッグ");
        Thread.sleep(1000);
        log.info("%s", "インフォ");
        log.warn("%s", "わーん");

        log.setLogLevel(Level.DEBUG);
        Thread.sleep(1000);
        log.trace("%s", "トレース");
        log.debug("%s", "デバッグ");
        Thread.sleep(1000);
        log.info("%s", "インフォ");
        log.warn("%s", "わーん");

    }
}
