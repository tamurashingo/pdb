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
package com.github.tamurashingo.pdb.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.github.tamurashingo.pdb.bean.ParamsBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;

public class ExecProcedure {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    private Connection conn;

    private String packageName;

    private String objectName;

    private String objectType;

    private List<ParamsBean> params;

    public ExecProcedure(String packageName, String objectName, String objectType, List<ParamsBean> params) {
        this.packageName = packageName;
        this.objectName = objectName;
        this.objectType = objectType;
        this.params = params;
        conn = DBManager.getInstance().getNativeConnection();

    }

    public void execute() throws DBException {
        log.trace("開始");

        CallableStatement stmt = null;
        try {
            String sql = parseMethod();
            stmt = conn.prepareCall(sql);
            parseInputArray(stmt);
            parseOutputArray(stmt);
            stmt.execute();
            getOutputArray(stmt);
        }
        catch (SQLException ex) {
            throw new DBException(ex);
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException ex) {
                    // クローズ時のSQLExceptionは何も行わない
                }
                stmt = null;
            }
        }

        log.trace("終了");
    }

    public String parseMethod() {
        log.trace("開始");

        StringBuilder buf = new StringBuilder();
        Iterator<ParamsBean> it = params.iterator();

        buf.append("begin ");

        if (!params.isEmpty() && params.get(0).getPosition() == 0) {
            buf.append(" ? := ");
            it.next();
        }

        if (!objectType.equals("PROCEDURE") && !objectType.equals("FUNCTION")) {
            buf.append(packageName);
            buf.append(".");
        }
        buf.append(objectName);
        buf.append("(");

        buf.append(makeArgs(it));

        buf.append(")");
        buf.append(";");
        buf.append(" end;");

        log.trace("終了");
        return buf.toString();
    }

    private String makeArgs(Iterator<ParamsBean> it) {
        log.trace("開始");

        StringBuilder buf = new StringBuilder();
        boolean cFlag = false;
        while (it.hasNext()) {
            ParamsBean bean = it.next();
            if (bean.getDataLevel() == 0) {
                if (cFlag == true) {
                    buf.append(",");
                }
                else {
                    cFlag = true;
                }
                buf.append(" ? ");
            }
        }

        log.trace("終了");
        return buf.toString();
    }

    public void parseInputArray(CallableStatement stmt) throws SQLException {
        int ix = 1;
        for (ListIterator<ParamsBean> it = params.listIterator(); it.hasNext(); ) {
            ParamsBean bean = it.next();
            if (bean.getInOut().equals("IN") || bean.getInOut().equals("IN/OUT")) {

                if (bean.getDataType().equals("VARRAY!!")) {
                    throw new SQLException("配列はサポートしていません。");
//					Object[] obj = parsePlsqlRecord( it, bean.getDataLevel() + 1 );
//					stmt.setPlsqlIndexTable( ix, obj, obj.length, obj.length, OracleTypes.CURSOR, 0 );
//					stmt.setObject( ix, obj );
                }
                else if (bean.getDataType().equals("PL/SQL RECORD")) {
                    throw new SQLException("PL/SQL RECORDはサポートしていません。");
                }
                else {
                    Object obj = bean.getValue();
//					stmt.setObject( ix, obj );
                    stmt.setString(ix, (String) obj);
                }
            }
            ix++;
        }
    }

    public void parseOutputArray(CallableStatement stmt) throws SQLException {
        log.trace("開始");

        int ix = 1;
        for (ListIterator<ParamsBean> it = params.listIterator(); it.hasNext(); ) {
            ParamsBean bean = it.next();
            if (bean.getInOut().equals("OUT") || bean.getInOut().equals("IN/OUT")) {
                stmt.registerOutParameter(ix, java.sql.Types.VARCHAR);
            }
            ix++;
        }

        log.trace("終了");
    }

    private void getOutputArray(CallableStatement stmt) throws SQLException {
        log.trace("開始");

        int ix = 1;
        for (ListIterator<ParamsBean> it = params.listIterator(); it.hasNext(); ) {
            ParamsBean bean = it.next();
            if (bean.getInOut().equals("OUT") || bean.getInOut().equals("IN/OUT")) {
                String ret = stmt.getString(ix);
                log.debug("結果[%d]:%s", ix, ret);
                bean.setValue(ret);
            }
            ix++;
        }

        log.trace("終了");
    }

//	private Object[] parseVarray( ListIterator<ParamsBean> it, int base ) {
//		log.trace("開始");
//		
//		List<Object> list = new ArrayList<Object>();
//		
//		while ( it.hasNext() ) {
//			ParamsBean bean = it.next();
//			if ( bean.getDataLevel() != base ) {
//				break;
//			}
//			if ( bean.getDataType().equals( "VARRAY" ) ) {
//				Object[] obj = parseVarray( it, bean.getDataLevel() + 1 );
//				list.add( obj );
//			}
//			else if ( bean.getDataType().equals( "PL/SQL RECORD" ) ) {
//				Object[] obj = parsePlsqlRecord( it, bean.getDataLevel() + 1 );
//				list.add( obj );
//			}
//			Object obj = bean.getValue();
//			list.add( obj );
//		}
//		
//		log.trace("終了");
//		return list.toArray();
//	}
//	
//	private Object[] parsePlsqlRecord( ListIterator<ParamsBean> it, int base ) {
//		List<Object> list = new ArrayList<Object>();
//		
//		while ( it.hasNext() ) {
//			ParamsBean bean = it.next();
//			if ( bean.getDataLevel() != base ) {
//				break;
//			}
//			if ( bean.getDataType().equals( "VARRAY" ) ) {
//				Object[] obj = parseVarray( it, bean.getDataLevel() + 1 );
//				list.add( obj );
//			}
//			else if ( bean.getDataType().equals( "PL/SQL RECORD" ) ) {
//				Object[] obj = parsePlsqlRecord( it, bean.getDataLevel() + 1 );
//				list.add( obj );
//			}
//			Object obj = bean.getValue();
//			list.add( obj );
//		}
//		return list.toArray();
//	}
//	
//	public Object parse( ParamsBean bean, ListIterator<ParamsBean> it ) throws SQLException {
//		if ( bean.getDataType().equals( "VARRAY" ) ) {
//			SQLName name = new SQLName( bean.getTypeName(), bean.getTypeSubname(), conn);
////			ArrayDescriptor desc = ArrayDescriptor.createDescriptor( name, conn );
//			ArrayDescriptor desc = ArrayDescriptor.createDescriptor( bean.getTypeName(), conn );
//			Object[] obj = innerParse( it, bean.getDataLevel() + 1 );
//			ARRAY ary = new ARRAY( desc, conn, obj );
//			return ary;
//		}
//		else if ( bean.getDataType().equals( "PL/SQL RECORD" ) ) {
//			SQLName name = new SQLName( bean.getTypeName(), bean.getTypeSubname(), conn);
//			ArrayDescriptor desc = ArrayDescriptor.createDescriptor( name, conn );
//			Object[] obj = innerParse( it, bean.getDataLevel() + 1 );
//			ARRAY ary = new ARRAY( desc, conn, obj );
//			return ary;
//		}
//		else {
//			return bean.getValue();
//		}
//	}


//	private Object[] innerParse( ListIterator<ParamsBean> it, int base ) throws SQLException {
//		
//		List<Object> list = new ArrayList<Object>();
//		
//		while ( it.hasNext() ) {
//			ParamsBean bean = it.next();
//			
//			if ( bean.getDataLevel() != base ) {
//				it.previous();
//				break;
//			}
//
//			if ( bean.getDataType().equals( "VARRAY" ) ) {
//				SQLName name = new SQLName( bean.getTypeName(), bean.getTypeSubname(), conn );
//				ArrayDescriptor desc = ArrayDescriptor.createDescriptor( name, conn );
//				Object[] obj = innerParse( it, bean.getDataLevel() + 1 );
//				ARRAY ary = new ARRAY( desc, conn, obj );
//				list.add( ary );
//			}
//			else if ( bean.getDataType().equals( "PL/SQL RECORD" ) ) {
//				SQLName name = new SQLName( bean.getTypeName(), bean.getTypeSubname(), conn );
//				ArrayDescriptor desc = ArrayDescriptor.createDescriptor( name, conn );
//				Object[] obj = innerParse( it, bean.getDataLevel() + 1 );
//				ARRAY ary = new ARRAY( desc, conn, obj );
//				list.add( ary );
//			}
//			else {
//				list.add( bean.getValue() );
//			}
//		}
//
//		return list.toArray();
//	}

//	private void makeArrayData( Iterator<ParamsBean> it, int dataLevel ) {
//		List<Object> array = new ArrayList<Object>();
//		while ( it.hasNext() ) {
//			ParamsBean bean = it.next();
//			if ( bean.getDataLevel() != dataLevel ) {
//				break;
//			}
//			
//			
//		}
//	}
//	
//	private void prim( ParamsBean bean ) {
//		
//		
//		String type = bean.getDataType();
//		if ( type.equals( "NUMBER" ) ) {
//		}
//		else if ( type.equals( "CHAR" ) ) {
//		}
//		else if ( type.equals( "DATE" ) ) {
//		}
//		else if ( type.equals( "VARCHAR2" ) ) {
//		}
//		else if ( type.equals( "ROWID" ) ) {
//		}
//	}

}

