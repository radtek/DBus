/*-
 * <<
 * DBus
 * ==
 * Copyright (C) 2016 - 2019 Bridata
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */


package com.creditease.dbus.canal.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.creditease.dbus.canal.utils.FileUtils.writeAndPrint;

/**
 * User: 王少楠
 * Date: 2018-08-10
 * Desc:
 */
public class CanalUtils {

    public static void start(String canalPath) throws Exception {

        writeAndPrint("starting canal.....");

        try {
            String startPath = canalPath + "/bin/" + "startup.sh";
            String stopPath = canalPath + "/bin/" + "stop.sh";

            String cmd = "sh " + stopPath;
            writeAndPrint("exec: " + cmd);
            //停止已存在
            exec(cmd);

            cmd = "sh " + startPath;
            writeAndPrint("exec: " + cmd);
            exec(cmd);
        } catch (Exception e) {
            writeAndPrint("************************************* START CANAL FAIL ************************************** ");
            throw e;
        }
    }

    public static void stop(String canalPath) throws Exception {
        writeAndPrint("stopping canal.....");
        try {
            String stopPath = canalPath + "/bin/" + "stop.sh";
            String cmd = "sh " + stopPath;
            writeAndPrint("exec: " + cmd);

            //停止已存在
            exec(cmd);
        } catch (Exception e) {
            writeAndPrint("************************************* STOP CANAL FAIL ***************************************");
            throw e;
        }
    }

    public static void copyLogfiles(String canalPath, String dsName) throws Exception {
        try {
            //copy log file
            String cmd = "rm -f canal.log";
            writeAndPrint("exec: " + cmd);
            exec(cmd);
            cmd = "ln -s " + canalPath + "/logs/canal/canal.log " + dsName + "_canal.log";
            writeAndPrint("exec: " + cmd);
            exec(cmd);
            cmd = "rm -f " + dsName + ".log";
            writeAndPrint("exec: " + cmd);
            exec(cmd);
            cmd = "ln -s " + canalPath + "/logs/" + dsName + "/" + dsName + ".log " + dsName + ".log";
            writeAndPrint("exec: " + cmd);
            exec(cmd);
        } catch (Exception e) {
            throw e;
        }
    }

    public static String exec(Object cmd) throws Exception {
        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        StringBuilder result = new StringBuilder();

        try {
            if (cmd instanceof String) {
                process = Runtime.getRuntime().exec(((String) cmd));
            } else {
                String[] cmd2 = (String[]) cmd;
                process = Runtime.getRuntime().exec(cmd2);
            }

            int exitValue = process.waitFor();

            if (0 != exitValue) {
                bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
                String line = null;
                while ((line = bufrError.readLine()) != null) {
                    result.append(line).append("\n");
                }
                bufrError.close();
                writeAndPrint("exec: " + exitValue);
                throw new RuntimeException(line);
            } else {
                // 读取输出
                // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
                bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = bufrIn.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        } finally {
            if (bufrIn != null) {
                bufrIn.close();
            }
            if (process != null) {
                process.destroy();
            }
        }

    }
}
