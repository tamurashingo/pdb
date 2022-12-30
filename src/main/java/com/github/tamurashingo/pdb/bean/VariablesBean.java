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

import java.util.ArrayList;
import java.util.List;

/**
 * プロシージャの変数を表すBean。
 *
 * @author tamura shingo
 */
public class VariablesBean implements java.io.Serializable {

    private static final long serialVersionUID = -3160248634896806546L;

    /** 変数名 */
    private String variableName;

    /** 変数種別 */
    private String variableType;

    /** 値 */
    private String value;

    /** 配列やPL/SQL RECORDの場合の子変数群 */
    private List<VariablesBean> children = new ArrayList<VariablesBean>();


//	TODO: 将来はツリーを展開したタイミングで子変数の解析を行う。
//	/** 再解析用オブジェクト */
//	transient private Object obj;

    public void setVariableName(String valName) {
        this.variableName = valName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void addVariable(VariablesBean bean) {
        children.add(bean);
    }

    public void setVariable(List<VariablesBean> list) {
        children = list;
    }

    public List<VariablesBean> getVariables() {
        return children;
    }

//	public void setObject( Object obj ) {
//		this.obj = obj;
//	}
//	
//	public Object getObject() {
//		return obj;
//	}

}


