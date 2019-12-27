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

/**
 * Created by chenqiao on 2019-12-27.
 * e-mail : mrjctech@gmail.com
 */
public class NpsThread extends Thread {

    public final File ROOT = new File(App.getInstance().getFilesDir(), ".nps");
    public final String BIN = ROOT.getAbsolutePath() + "/bin";

    private final String TAG = "NPS-Thread";



    private final String NPS_Path = BIN + "/npc";

    @Override
    public void run() {
        super.run();

        startNPS();
    }

    private void startNPS(){

//        FileUtils.delete(BIN);
        FileUtils.copyAssets("bin", BIN);


        String command = "." + BIN + "/npc -server=101.200.200.248:8024 -vkey=macos";
        Log.e(TAG, command);
        CommandExe.execCommand(command, false);


//        try {
//            Process nps = new ProcessBuilder(NPS_Path).redirectErrorStream(true).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            Log.e(TAG, "start npc failed!!!");
//
//        }
//        List<ProcessUtils.ProcessInfo> infos = ProcessUtils.getProcessInfosByName(NPS_Path);
//        if (infos == null || infos.size() == 0) {
//            Log.e(TAG, "start npc failed!!!");
//        } else {
//            Log.i(TAG, "npc started, process info: " + infos.get(0));
//        }

    }


}
