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

public class ConnectConfig {

    private static final ConnectConfig thisInstance;

    private ConnectConfig() {
    }

    static {
        thisInstance = new ConnectConfig();
    }

    private String userid;

    private String password;

    private String server;

    private String port;

    private String dbname;

    private boolean connected;

    public static ConnectConfig getInstance() {
        return thisInstance;
    }

    public String getUserid() {
        return userid;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public String getPort() {
        return port;
    }

    public String getDbname() {
        return dbname;
    }

    public void init(String userid, String password, String server, String port, String dbname) {
        this.userid = userid;
        this.password = password;
        this.server = server;
        this.port = port;
        this.dbname = dbname;
        this.connected = false;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean getConnected() {
        return connected;
    }

}
