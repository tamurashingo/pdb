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
package com.github.tamurashingo.pdb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.github.tamurashingo.pdb.controller.BreakPointManager;
import com.github.tamurashingo.pdb.init.Initializable;
import com.github.tamurashingo.pdb.init.InitializeException;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

import static com.github.tamurashingo.pdb.SystemConstants.logoURL;


/**
 * アプリケーション全体で使用する共通設定の初期化を行う。
 * <li>起動しているマシンのアドレス（IPアドレス／ホスト名）
 * <li>ブレイクポイント管理
 * <li>Oracleとのデバッグ接続用ポート
 *
 * @author tamura shingo
 */
public class EnvInitializer implements Initializable {

    /**
     * 初期化内容
     */
    private static final String desc =
            "初期値の設定";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws InitializeException {
        Log log = Logger.getLogger();
        try {
            SystemConstants.config.readConfig(SystemConstants.CONFIG_FILE_NAME);
            log.setLogLevel(SystemConstants.config.getLogLevel());
            log.warn("ログレベルを%sに変更しました。", SystemConstants.config.getLogLevel());

            Globals.setBreakPointManager(new BreakPointManager());
            Globals.setIpaddress(getIpAddress());

            SystemConstants.logo.readIcon(logoURL);
        } catch (IOException ex) {
            throw new InitializeException("読み込みに失敗", ex);
        }
    }

    /**
     * 自マシンのアドレスを取得する。
     *
     * @return アドレス
     * @throws InitializeException アドレス取得に失敗
     */
    private String getIpAddress() throws InitializeException {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException ex) {
            throw new InitializeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDesc() {
        return desc;
    }
}
