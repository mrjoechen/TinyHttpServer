package com.chenqiao.tinyhttpserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chenqiao.server.TinyHttpd;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TinyHttpd.getInstance().startServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TinyHttpd.getInstance().stopServer();
    }
}
