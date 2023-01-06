/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2010, 2023 tamura shingo
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
package com.github.tamurashingo.pdb.util;

import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtil {

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    @Data
    @RequiredArgsConstructor
    public static class Network {
        private final String interfaceName;
        private final NetworkType networkType;
        private final String address;

        public Network(String interfaceName, InetAddress inetAddress) {
            this.interfaceName = interfaceName;
            this.address = inetAddress.getHostAddress();
            if (inetAddress instanceof java.net.Inet4Address) {
                this.networkType = NetworkType.IPV4;
            } else if (inetAddress instanceof java.net.Inet6Address) {
                this.networkType = NetworkType.IPV6;
            } else {
                this.networkType = NetworkType.UNKNOWN;
            }
        }
    }

    @AllArgsConstructor
    public enum NetworkType {
        IPV4("IPv4"),
        IPV6("IPv6"),
        UNKNOWN("unknown")
        ;

        @Getter
        private String name;
    }

    public static List<Network> getNetworkAddress() {
        log.trace("開始");
        List<Network> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            Collections.list(interfaces).stream()
                    .forEach(i -> {
                        Collections.list(i.getInetAddresses()).stream()
                                .forEach(a -> {
                                    result.add(new Network(i.getDisplayName(), a));
                                });
            });
        } catch (java.net.SocketException ex) {
            // 例外が発生した場合はログを出力して終了する
            log.printStackTrace(Log.Level.DEBUG, ex);
        }

        log.trace("終了");
        return result;
    }
 }
