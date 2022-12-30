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
package com.github.tamurashingo.pdb.bean;

/**
 * プロシージャの情報を表すBean。
 *
 * @author tamura shingo
 */
public class ProceduresBean implements java.io.Serializable {

    private static final long serialVersionUID = -6358574734372898189L;

    /** ストアド名 */
    private String objectName;

    /** ストアドの種別 */
    private String objectType;

    /** ストアドの状態 */
    private boolean valid;

    /** デバッグ可能かどうか */
    private boolean debuggable;

    /**
     * ストアドの状態を返す。
     * @return VALIDの場合true
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * ストアドの状態を設定する。
     * @param valid VALIDの場合true
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the objectName
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * @param objectName the objectName to set
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * DEBUG可能かどうかを返す。
     * @return DEBUG可能の場合true
     */
    public boolean isDebuggable() {
        return debuggable;
    }

    /**
     * DEBUG可能かどうかを設定する。
     * @param debuggable DEBUG可能の場合true
     */
    public void setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
    }

}
