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

import com.github.tamurashingo.pdb.PDBException;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

public class Init {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    public static void init() throws PDBException {
        log.trace("開始");
        try {
            Initializer init = Initializer.getInstance();
            init.add("com.github.tamurashingo.pdb.log.LogInitializer");
            init.add("com.github.tamurashingo.pdb.EnvInitializer");
            init.add("com.github.tamurashingo.pdb.db.DbInitializer");
            init.add("com.github.tamurashingo.pdb.gui.GUIInitializer");

            init.addObserver(new SplashScreenObserver());

            log.info("初期化開始");
            init.init();
            log.info("初期化終了");
        } catch (InitializeException ex) {
            throw new PDBException("初期化失敗", ex);
        }
        log.trace("終了");
    }
}
