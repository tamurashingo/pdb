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
package com.github.tamurashingo.pdb.log;

/**
 * {@code Log}インタフェースのスケルトン実装。
 *
 * @author tamura shingo
 */
public abstract class AbstractLog implements Log {

    /**
     * ログレベル
     */
    protected Level level;

    /**
     * ログ出力を行う。
     * {@code warn}、{@code info}、{@code debug}、{@code trace}の各メソッドは、
     * ログ出力可能なログレベルであるかを検査し、このメソッドを呼び出す。
     *
     * @param level  ログレベル
     * @param format フォーマット
     * @param args   引数
     */
    public abstract void log(Level level, String format, Object... args);


    /**
     * {@inheritDoc}
     */
    @Override
    public void printStackTrace(Level level, Throwable ex) {
        if (outputp(level)) {
            StringBuilder buf = new StringBuilder();

            while (ex != null) {
                buf.append(ex.getClass().getName());
                buf.append(":");
                buf.append(ex.getMessage());
                buf.append(System.getProperty("line.separator"));
                for (StackTraceElement elm : ex.getStackTrace()) {
                    buf.append("    ");
                    buf.append(elm.toString());
                    buf.append(System.getProperty("line.separator"));
                }

                ex = ex.getCause();
            }

            log(level, "%s", buf.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String format, Object... args) {
        if (isWarn()) {
            log(Level.WARN, format, args);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String format, Object... args) {
        if (isInfo()) {
            log(Level.INFO, format, args);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String format, Object... args) {
        if (isDebug()) {
            log(Level.DEBUG, format, args);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String format, Object... args) {
        if (isTrace()) {
            log(Level.TRACE, format, args);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogLevel(Level level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    public Level getLogLevel() {
        return level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarn() {
        switch (level) {
            case NONE:
                return false;
            case WARN:
                return true;
            case INFO:
                return true;
            case DEBUG:
                return true;
            case TRACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfo() {
        switch (level) {
            case NONE:
                return false;
            case WARN:
                return false;
            case INFO:
                return true;
            case DEBUG:
                return true;
            case TRACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebug() {
        switch (level) {
            case NONE:
                return false;
            case WARN:
                return false;
            case INFO:
                return false;
            case DEBUG:
                return true;
            case TRACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTrace() {
        switch (level) {
            case NONE:
                return false;
            case WARN:
                return false;
            case INFO:
                return false;
            case DEBUG:
                return false;
            case TRACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * 指定されたログレベルが出力可能かどうかを検査する。
     *
     * @param level
     * @return trueの場合、出力可
     */
    private boolean outputp(Level level) {
        switch (level) {
            case NONE:
                return false;
            case WARN:
                return isWarn();
            case INFO:
                return isInfo();
            case DEBUG:
                return isDebug();
            case TRACE:
                return isTrace();
            default:
                return false;
        }
    }
}
