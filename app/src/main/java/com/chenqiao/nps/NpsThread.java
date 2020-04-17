package com.chenqiao.nps;

import android.os.Environment;
import android.util.Log;

import com.chenqiao.App;
import com.chenqiao.util.CommandExe;
import com.chenqiao.util.FileUtils;
import com.chenqiao.util.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by chenqiao on 2019-12-27.
 * e-mail : mrjctech@gmail.com
 */
public class NpsThread extends Thread {

    public final File ROOT = new File(App.getInstance().getFilesDir(), "nps");
    public final String BIN = ROOT.getAbsolutePath() + "/bin";

    private final String TAG = "NPS-Thread";

    private AtomicReference<Process> Nprocess = new AtomicReference<>();


    private final String NPS_Path = BIN + "/npc";

    @Override
    public void run() {
        super.run();

        startNPS();
    }

    private void startNPS(){

        FileUtils.copyAssets("bin", BIN);


        String command = NPS_Path + " -server=101.200.200.248:8024 -vkey=123456";

        Log.e(TAG, command);
        //阻塞
        CommandExe.execCommand(command, false);
        Log.e(TAG, "nps is terminated !!!");


//        try {
//            Process npc = new ProcessBuilder(NPS_Path).redirectErrorStream(true).start();
//            Nprocess.set(npc);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, "start npc failed!!!");
//
//        }

    }


    public void stopNps(){
        Process process = Nprocess.get();
        List<ProcessUtils.ProcessInfo> infos = ProcessUtils.getProcessInfosByName(NPS_Path);
        if (infos == null || infos.size() == 0) {

        } else {
            for (ProcessUtils.ProcessInfo info : infos) {
                int myPid = ProcessUtils.myPid();
                Log.i(TAG, "ProcessInfo pid : " + info.pid + " ppid: "+ info.ppid + " user: "+info.user + " name: "+ info.name);


                //kill -9 代表的信号是SIGKILL，表示进程被终止，需要立即退出；
                //kill -9表示强制杀死该进程，这个信号不能被捕获也不能被忽略。
                String killCommand = "kill -9 "+ info.pid;

                try {
                    Runtime.getRuntime().exec(killCommand);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // npc 进程的父进程 是否为app进程  CommandExe.execCommand(command, false); 是 sh 进程
                if (info.ppid != myPid) {
                    if (info.ppid == 1) {
                        Log.i(TAG, "killed process: " + info);
                    } else {
                        Log.i(TAG, "killed externally started npc process: " + info);
                    }
                } else {
                    Log.i(TAG, "npc has been stoded by this process: " + info);
                }
            }
        }
    }


}
