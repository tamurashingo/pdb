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

import java.util.ArrayList;
import java.util.List;

/**
 * 初期化管理用クラス。
 * <p>
 * 初期化を行うには、{@link Initializable}を実装したクラスを、{@link #add(Initializable)}または {@link #add(String)}で登録し、
 * {@link #init()}を行う。
 * 初期化は登録順に実施される。
 * </p>
 * <p>
 * {@link InitializeObserver}を実装したクラスを、{@link #addObserver(InitializeObserver)}で登録することで、
 * 初期化開始時、終了時のクラス情報の通知を受けとることができる。
 * </p>
 * <p>
 * <code>
 * init = Initializer.getInstance();<br />
 * init.add( "some.package.InitClass" );&nbsp;&nbsp;// 初期化クラスの登録<br />
 * init.add( new some.package.AnotherClass() );&nbsp;&nbsp;// 初期化クラスの登録<br />
 * <br/>
 * init.addObserver( new SomeObserver() );&nbsp;&nbsp;// Observerクラスの登録<br />
 * init.addObserver( new AnotherObserver() );&nbsp;&nbsp;// Observerクラスの登録<br />
 * <br />
 * init.init();&nbsp;&nbsp;// 初期化の実行<br />
 * </code>
 * </p>
 *
 * @author tamura shingo
 */
public class Initializer {

    /**
     * シングルトン
     */
    private static final Initializer thisInstance;

    static {
        thisInstance = new Initializer();
    }

    /**
     * 初期化クラス
     */
    private List<Initializable> classes;
    /**
     * 初期化オブザーバ
     */
    private List<InitializeObserver> observers;


    /**
     * コンストラクタ。
     * 外部からのインスタンス化を禁止。
     */
    private Initializer() {
        classes = new ArrayList<Initializable>();
        observers = new ArrayList<InitializeObserver>();
    }

    /**
     * 初期化クラスのインスタンスを取得する。
     *
     * @return インスタンス
     */
    public static Initializer getInstance() {
        return thisInstance;
    }

    /**
     * 初期化対象クラスを管理対象に追加する。
     *
     * @param cls インスタンス化した初期化対象クラス
     */
    public void add(Initializable cls) {
        classes.add(cls);
    }

    /**
     * 初期化対象クラスをインスタンス化し、管理対象に追加する。
     *
     * @param clsName 初期化対象クラス名
     * @throws InitializeException 初期化対象クラスのインスタンス化に失敗
     */
    @SuppressWarnings("unchecked")
    public void add(String clsName) throws InitializeException {
        try {
            Class<Initializable> cls = (Class<Initializable>) Class.forName(clsName);
            Initializable inst = cls.newInstance();
            classes.add(inst);
        } catch (ClassNotFoundException ex) {
            throw new InitializeException(ex);
        } catch (InstantiationException ex) {
            throw new InitializeException(ex);
        } catch (IllegalAccessException ex) {
            throw new InitializeException(ex);
        }
    }

    /**
     * 初期化を行う。
     *
     * @throws InitializeException 初期化にて異常が発生
     */
    public void init() throws InitializeException {
        for (Initializable cls : classes) {
            for (InitializeObserver obs : observers) {
                obs.tellStart(cls.getClass().getName(), cls.getDesc());
            }
            cls.init();
            for (InitializeObserver obs : observers) {
                obs.tellEnd(cls.getClass().getName(), cls.getDesc());
            }

        }
    }

    /**
     * Observerを管理対象に追加する。
     *
     * @param obs Observer
     */
    public void addObserver(InitializeObserver obs) {
        observers.add(obs);
    }

}
