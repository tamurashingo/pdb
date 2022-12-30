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

import com.github.tamurashingo.pdb.log.Log;

/**
 * ログ出力を行わないLogger。
 *
 * @author tamura shingo
 */
public class NullLogger implements Log {

    private static final Log thisInstance;

    public static Log getInstance() {
        return thisInstance;
    }

    static {
        thisInstance = new NullLogger();
    }

    @Override
    public void debug(String format, Object... args) {
    }

    /**
     * 常に{@code Level.NONE}を返す。
     */
    @Override
    public Level getLogLevel() {
        return Level.NONE;
    }

    @Override
    public void info(String format, Object... args) {
    }

    /**
     * 常に{@code false}を返す。
     */
    @Override
    public boolean isDebug() {
        return false;
    }

    /**
     * 常に{@code false}を返す。
     */
    @Override
    public boolean isInfo() {
        return false;
    }

    /**
     * 常に{@code false}を返す。
     */
    @Override
    public boolean isTrace() {
        return false;
    }

    /**
     * 常に{@code false}を返す。
     */
    @Override
    public boolean isWarn() {
        return false;
    }

    @Override
    public void setLogLevel(Level level) {
    }

    @Override
    public void trace(String format, Object... args) {
    }

    @Override
    public void warn(String format, Object... args) {
    }

    @Override
    public void printStackTrace(Level leve, Throwable ex) {
    }
}
