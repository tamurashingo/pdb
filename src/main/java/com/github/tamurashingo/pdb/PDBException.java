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

/**
 * 本アプリケーション内で発生するすべての例外の元。
 *
 * @author tamura shingo
 */
public class PDBException extends Exception {

    private static final long serialVersionUID = -5682556318055615515L;

    /**
     * 詳細メッセージに{@code null}を使用して、新規例外を構築する。
     */
    public PDBException() {
        super();
    }

    /**
     * 指定された詳細メッセージ使用して、新規例外を構築する。
     *
     * @param message 詳細メッセージ。
     */
    public PDBException(String message) {
        super(message);
    }

    /**
     * 指定された原因を持つ、新しい例外を構築する。
     *
     * @param cause 原因
     */
    public PDBException(Throwable cause) {
        super(cause);
    }

    /**
     * 指定された原因と詳細メッセージを持つ、新しい例外を構築する。
     *
     * @param message 詳細メッセージ
     * @param cause   原因
     */
    public PDBException(String message, Throwable cause) {
        super(message, cause);
    }

}
