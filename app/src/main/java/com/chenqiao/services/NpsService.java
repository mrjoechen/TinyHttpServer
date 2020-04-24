package com.chenqiao.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chenqiao.nps.NpsThread;
import com.chenqiao.server.TinyHttpd;

/**
 * Created by chenqiao on 2020/4/24.
 * e-mail : mrjctech@gmail.com
 */
public class NpsService extends Service {
    private static final String TAG = "NpsService";

    private NpsThread npsThread;
    private NpsBinder npsBinder;


    @Override
    public void onCreate() {
        super.onCreate();

        if (npsThread != null){
            if (npsThread.isAlive()){
                npsThread.stopNps();
                npsThread = null;
            }
        }

        npsThread = new NpsThread("101.200.200.248:8024", "123456");
        npsThread.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        npsBinder = new NpsBinder();
        return npsBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (npsThread != null){
            npsThread.stopNps();
        }
    }

//    @Override
//    public boolean onUnbind(Intent intent) {
//
//        if(npsBinder != null){
//            npsBinder.setNpsServiceLogListener(null);
//        }
//        return super.onUnbind(intent);
//    }

    public interface NpsServiceLogListener{
        void onNpsServiceLog(String log);
    }


    public class NpsBinder extends Binder {

        public boolean getStatus() {

            boolean serviceStatus = npsThread.isAlive();

            Log.d("TAG", "status: " + serviceStatus);

            return serviceStatus;
        }


        private NpsServiceLogListener npsServiceLogListener;

        public void setNpsServiceLogListener(final NpsServiceLogListener listener) {
            this.npsServiceLogListener = listener;

            if (npsServiceLogListener == null){
                npsThread.setNpsLogListener(null);
            }else {
                npsThread.setNpsLogListener(new NpsThread.NpsLogListener() {
                    @Override
                    public void onNpsLog(String log) {
                        if (npsServiceLogListener != null){
                            npsServiceLogListener.onNpsServiceLog(log);
                        }
                    }
                });
            }

        }

    }
}
