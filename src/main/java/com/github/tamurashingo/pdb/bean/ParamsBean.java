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
 * プロシージャの引数を扱うBean。
 *
 * @author tamura shingo
 */
public class ParamsBean implements java.io.Serializable {

    private static final long serialVersionUID = -2476424866840352507L;

    /** 引数名 */
    private String argumentName;

    /** コンポジット型の引数のネスト深度 */
    private int dataLevel;

    /** 引数のデータ型 */
    private String dataType;

    /** 引数の方向 */
    private String inOut;

    /** 同じDataLevelの兄弟に対する本項目の位置 */
    private int position;

    /** 値 */
    private String value = "";

    /** パッケージ名 */
    private String typeName;

    /** typeNameで指定されたパッケージで宣言された型の名前 */
    private String typeSubname;


    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the argumentName
     */
    public String getArgumentName() {
        return argumentName;
    }

    /**
     * @param argumentName the argumentName to set
     */
    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    /**
     * @return the dataLevel
     */
    public int getDataLevel() {
        return dataLevel;
    }

    /**
     * @param dataLevel the dataLevel to set
     */
    public void setDataLevel(int dataLevel) {
        this.dataLevel = dataLevel;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the inOut
     */
    public String getInOut() {
        return inOut;
    }

    /**
     * @param inOut the inOut to set
     */
    public void setInOut(String inOut) {
        this.inOut = inOut;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the typeSubname
     */
    public String getTypeSubname() {
        return typeSubname;
    }

    /**
     * @param typeSubname the typeSubname to set
     */
    public void setTypeSubname(String typeSubname) {
        this.typeSubname = typeSubname;
    }
}
