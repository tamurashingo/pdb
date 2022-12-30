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
package com.github.tamurashingo.pdb.debugger;

import java.util.List;

import com.github.tamurashingo.pdb.bean.BreakPointBean;


/**
 * デバッガへ通知するリクエスト
 *
 * @author tamura shingo
 */
public interface DbgRequestHandler {

    /**
     * デバッガ情報の初期化
     * @param handler コールバック用インスタンス
     */
    public void init(DbgEventHandler handler);

    /**
     * デバッガの開始
     */
    public void startDebugger();

    /**
     * デバッガの終了
     */
    public void stopDebugger();

    /**
     * 設定済みのブレイクポイントを取得する。
     * @return 設定済みのブレイクポイント
     */
    public List<BreakPointBean> getBreakPoints();

    /**
     * ブレイクポイントを設定する。
     * @param bean
     * @return
     */
    public boolean setBreakPoint(BreakPointBean bean);

    /**
     * ブレイクポイントを削除する。
     * @param bean
     * @return
     */
    public boolean removeBreakPoint(BreakPointBean bean);

    /**
     * ブレイクポイントを削除する。
     * @param sourceName
     * @param line
     * @return
     */
    public boolean removeBreakPoint(String sourceName, int line);

    /**
     * RESUME実行する。
     */
    public void resume();

    /**
     * STEP実行する。
     */
    public void step();

    /**
     * STEP OVER実行する。
     */
    public void stepOver();

    /**
     * STEP OUT実行する。
     */
    public void stepOut();


    /**
     * STEP実行用の設定をクリアする。
     */
    public void clearStep();
}
