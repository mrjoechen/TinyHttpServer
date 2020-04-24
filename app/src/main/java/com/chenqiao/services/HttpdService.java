package com.chenqiao.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chenqiao.server.TinyHttpd;

/**
 * Created by chenqiao on 2020/4/24.
 * e-mail : mrjctech@gmail.com
 */
public class HttpdService extends Service {

    private static final String TAG = "HttpdService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TinyHttpd.getInstance().startServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new HttpBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TinyHttpd.getInstance().stopServer();
    }


    class HttpBinder extends Binder {

        public boolean getStatus() {

            boolean serviceStatus = TinyHttpd.getInstance().getServiceStatus();

            Log.d("TAG", "status: " + serviceStatus);

            return serviceStatus;
        }

    }

}
