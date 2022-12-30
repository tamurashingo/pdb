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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.github.tamurashingo.pdb.log.impl.NullLogger;

/**
 * ログ管理クラス。
 * <p>
 * ログインスタンスの取得、設定を行う。
 * 初期のログインスタンスは{@code NullLogger}となっている。
 * このクラスが返すログインスタンスは、実際のログインスタンスのproxyである。
 * そのため、{@code Log}自体は{@code final}を付けていても、
 * {@code setLogger()}を行えばログインスタンスを変更することができる。
 * </p>
 *
 * @author tamura shingo
 */
public class Logger {

    /**
     * ログインスタンス。
     */
    private static final ProxyLogger logger;

    /**
     * ログクラスを{@code NullLogger}で初期化する。
     * ログレベルは{@code NONE}を指定する。
     */
    static {
        logger = new ProxyLogger();
    }

    /**
     * コンストラクタ。
     * インスタンス化禁止。
     */
    private Logger() {
    }

    /**
     * ログインスタンスを取得する。
     * <p>
     * 通常のログ出力を行う場合は、このメソッドを使用してログインスタンスを取得する。
     * </p>
     * <p>
     * ここで取得できるログインスタンスはProxyクラスであるため、
     * {@link #setLogger(Log)}で設定したインスタンスを取得したい場合は、
     * {@link #getRawLogger()}を使用すること。
     * </p>
     *
     * @return インスタンス
     */
    public static Log getLogger() {
        return logger;
    }


    /**
     * ログインスタンスを取得する。
     * <p>
     * {@link #setLogger(Log)}で設定したインスタンスを取得する場合は、このメソッドを使用する。
     * </p>
     * <p>
     * 通常のログ出力を行う場合は、{@link #getLogger()}を使用すること
     * </p>
     *
     * @return インスタンス
     */
    public static Log getRawLogger() {
        return logger.getLogger();
    }

    /**
     * ログインスタンスを設定する。
     *
     * @param logger インスタンス
     */
    public static void setLogger(Log logger) {
        Logger.logger.setLogger(logger);
    }

    /**
     * 実際のログインスタンスへの代理クラス。
     *
     * @author Tamura Shingo
     */
    private static class ProxyLogger implements Log {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        Lock readLock = lock.readLock();
        Lock writeLock = lock.writeLock();

        private Log log;

        public ProxyLogger() {
            log = NullLogger.getInstance();
        }

        public void setLogger(Log log) {
            writeLock.lock();
            try {
                this.log = log;
            } finally {
                writeLock.unlock();
            }
        }

        public Log getLogger() {
            return log;
        }

        @Override
        public void printStackTrace(Level level, Throwable ex) {
            readLock.lock();
            try {
                log.printStackTrace(level, ex);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void warn(String format, Object... args) {
            readLock.lock();
            try {
                log.warn(format, args);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void info(String format, Object... args) {
            readLock.lock();
            try {
                log.info(format, args);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void debug(String format, Object... args) {
            readLock.lock();
            try {
                log.debug(format, args);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void trace(String format, Object... args) {
            readLock.lock();
            try {
                log.trace(format, args);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public void setLogLevel(Level level) {
            readLock.lock();
            try {
                log.setLogLevel(level);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public Level getLogLevel() {
            readLock.lock();
            Level level = Level.NONE;
            try {
                level = log.getLogLevel();
            } finally {
                readLock.unlock();
            }
            return level;
        }

        @Override
        public boolean isWarn() {
            readLock.lock();
            boolean flag = false;
            try {
                flag = log.isWarn();
            } finally {
                readLock.unlock();
            }
            return flag;
        }

        @Override
        public boolean isInfo() {
            readLock.lock();
            boolean flag = false;
            try {
                flag = log.isInfo();
            } finally {
                readLock.unlock();
            }
            return flag;
        }

        @Override
        public boolean isDebug() {
            readLock.lock();
            boolean flag = false;
            try {
                flag = log.isDebug();
            } finally {
                readLock.unlock();
            }
            return flag;
        }

        @Override
        public boolean isTrace() {
            readLock.lock();
            boolean flag = false;
            try {
                flag = log.isTrace();
            } finally {
                readLock.unlock();
            }
            return flag;
        }
    }
}
