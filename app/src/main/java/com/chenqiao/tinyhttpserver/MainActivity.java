package com.chenqiao.tinyhttpserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chenqiao.server.TinyHttpd;
import com.chenqiao.util.StringUtils;

public class MainActivity extends AppCompatActivity implements TinyHttpd.OnServListener {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyHttpd.getInstance().setOnserveListener(this);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
        TinyHttpd.getInstance().startServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TinyHttpd.getInstance().stopServer();
    }

    @Override
    public void onServe(String result) {
        if (!StringUtils.isEmpty(result)){
            textView.setText(""+result);
        }
    }
}
