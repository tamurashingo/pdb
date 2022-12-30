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
 * ログレベルに応じて出力を制御するログ出力用インタフェース。
 * ログレベルは{@link Log.Level}にて定義されている。
 *
 * @author tamura shingo
 */
public interface Log {

    /**
     * スタックトレースを出力する。
     *
     * @param level ログレベル
     * @param ex
     */
    public void printStackTrace(Level level, Throwable ex);

    /**
     * {@code WARN}レベルのログを出力する。
     *
     * @param format フォーマット文字列
     * @param args   パラメータ
     */
    public void warn(String format, Object... args);

    /**
     * {@code INFO}レベルのログを出力する。
     *
     * @param format フォーマット文字列
     * @param args   パラメータ
     */
    public void info(String format, Object... args);

    /**
     * {@code DEBUG}レベルのログを出力する。
     *
     * @param format フォーマット文字列
     * @param args   パラメータ
     */
    public void debug(String format, Object... args);

    /**
     * {@code TRACE}レベルのログを出力する。
     *
     * @param format フォーマット文字列
     * @param args   パラメータ
     */
    public void trace(String format, Object... args);

    /**
     * ログレベルを設定する。
     *
     * @param level ログレベル
     */
    public void setLogLevel(Level level);

    /**
     * ログレベルを取得する。
     *
     * @return ログレベル
     */
    public Level getLogLevel();

    /**
     * 現在のログレベルで{@code WARN}レベルのログが出力できるかを検査する。
     *
     * @return {@code WARN}レベルのログが出力できる場合、{@code true}
     */
    public boolean isWarn();

    /**
     * 現在のログレベルで{@code INFO}レベルのログが出力できるかを検査する。
     *
     * @return {@code INFO}レベルのログが出力できる場合、{@code true}
     */
    public boolean isInfo();

    /**
     * 現在のログレベルで{@code DEBUG}レベルのログが出力できるかを検査する。
     *
     * @return {@code DEBUG}レベルのログが出力できる場合、{@code true}
     */
    public boolean isDebug();

    /**
     * 現在のログレベルで{@code TRACE}レベルのログが出力できるかを検査する。
     *
     * @return {@code TRACE}レベルのログが出力できる場合、{@code true}
     */
    public boolean isTrace();


    /**
     * ログレベル。
     *
     * @author Tamura Shingo
     */
    public enum Level {
        /**
         * 未出力。
         **/
        NONE,

        /**
         * WARNレベル。
         * ログレベルがWARNのみログ出力可能。
         **/
        WARN,

        /**
         * INFOレベル。
         * ログレベルがINFO、WARNのみログ出力可能。
         */
        INFO,

        /**
         * DEBUGレベル。
         * ログレベルがDEBUG、INFO、WARNのときにログ出力可能。
         */
        DEBUG,

        /**
         * TRACEレベル。
         * ログレベルがNONE以外のときにログ出力可能。
         */
        TRACE
    }
}

