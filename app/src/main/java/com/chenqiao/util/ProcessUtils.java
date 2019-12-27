package com.chenqiao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ProcessUtils {
    private ProcessUtils() {
    }
    
    public static class ProcessInfo {
        public final String user;
        public final int pid;
        public final int ppid;
        public final String name;


        public ProcessInfo(String user, int pid, int ppid, String name) {
            this.user = user;
            this.pid = pid;
            this.ppid = ppid;
            this.name = name;
        }

        @Override
        public String toString() {
            return user + " " + pid + " " + ppid + " " + name;
        }
    }

    /**
     * 获取name程序运行的进程id列表。使用ps程序实现，注意与录音冲突，需要在其他进程调用.
     * @param name
     * @return
     */
    public static ArrayList<ProcessInfo> getProcessInfosByName(String name) {
        try {
            ArrayList<ProcessInfo> processes = new ArrayList<>();
            Process ps = new ProcessBuilder("/system/bin/sh", "-c", "ps|grep \\ " + name+"$").start();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processes.add(parse(line));
                }
            }
            return processes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void kill(ProcessInfo info)  {
        android.os.Process.killProcess(info.pid);
    }
    
    public static int myPid() {
        return android.os.Process.myPid();
    }
    
    private static ProcessInfo parse(String line) {
        String[] tokens = line.split("\\s+");
        String user = tokens[0];
        int pid = StringUtils.toInt(tokens[1]);
        int ppid = StringUtils.toInt(tokens[2]);
        String name = tokens[tokens.length - 1];
        return new ProcessInfo(user, pid, ppid, name);
    }
    
}
