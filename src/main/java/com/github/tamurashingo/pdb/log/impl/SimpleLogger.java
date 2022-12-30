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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.github.tamurashingo.pdb.log.AbstractLog;
import com.github.tamurashingo.pdb.log.Log;

/**
 * ログ出力の実装サンプル。
 * 標準出力にログを出力する。
 *
 * @author tamura shingo
 */
public class SimpleLogger extends AbstractLog {

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

    @Override
    public void log(Log.Level level, String format, Object... args) {
        Calendar cal = Calendar.getInstance();
        String date = "";
        synchronized (fmt) {
            date = fmt.format(cal.getTime());
        }
        String cont = String.format(format, args);
        String log = String.format(logFormat, date, map.get(level), cont);
        System.out.print(log);
        System.out.flush();
    }

    public static void main(String[] args) throws Exception {
        Log log = new SimpleLogger();
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
