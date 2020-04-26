package com.chenqiao.tinyhttpserver;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chenqiao.server.TinyHttpd;
import com.chenqiao.services.HttpdService;
import com.chenqiao.services.NpsService;
import com.chenqiao.util.StringUtils;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements TinyHttpd.OnServListener, NpsService.NpsServiceLogListener {

    private TextView textView_ip;
    private JsonRecyclerView rv_json;
    private ImageView ivStatus;
    private Button btnStart;
    private TextView tvLog;


    private NpsService.NpsBinder npsBinder;
    private ServiceConnection npsConn;
    private ScrollView scrollView;
    private EditText etVkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyHttpd.getInstance().setOnserveListener(this);
        setContentView(R.layout.activity_main);
        textView_ip = findViewById(R.id.tv_ip);

        rv_json = findViewById(R.id.rv_json);

        ivStatus = findViewById(R.id.iv_status);

        btnStart = findViewById(R.id.btn_start);

        textView_ip.setText(textView_ip.getText() + getIP() + ":" +TinyHttpd.getInstance().getListeningPort());

        tvLog = findViewById(R.id.tv_log);

        scrollView = findViewById(R.id.sc);

        etVkey = findViewById(R.id.et_vkey);


        npsConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                npsBinder = (NpsService.NpsBinder) service;

                if (npsBinder != null){
                    npsBinder.setNpsServiceLogListener(MainActivity.this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                npsBinder.setNpsServiceLogListener(null);

            }
        };

        if (TinyHttpd.getInstance().isAlive()){
            etVkey.setEnabled(false);
            ivStatus.setImageResource(R.mipmap.radio_button_select_green);
            btnStart.setText("Stop");

            bindService(new Intent(MainActivity.this, NpsService.class), npsConn, BIND_AUTO_CREATE);

        }else {
            etVkey.setEnabled(true);
            ivStatus.setImageResource(R.mipmap.radio_button_select_red);
            btnStart.setText("Start");
        }


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TinyHttpd.getInstance().isAlive()){

                    etVkey.setEnabled(false);
                    startService(new Intent(MainActivity.this, HttpdService.class));
                    Intent serviceIntent = new Intent(MainActivity.this, NpsService.class);
                    serviceIntent.putExtra("vkey", etVkey.getText().toString());

                    bindService(new Intent(MainActivity.this, NpsService.class), npsConn, BIND_AUTO_CREATE);

                    startService(serviceIntent);

                    textView_ip.setText(getIP() + ":" +TinyHttpd.getInstance().getListeningPort());

                    btnStart.setText("Stop");
                    ivStatus.setImageResource(R.mipmap.radio_button_select_green);
                }else {

                    etVkey.setEnabled(true);


                    if (npsConn != null){
                        unbindService(npsConn);
                    }

                    stopService(new Intent(MainActivity.this, HttpdService.class));
                    stopService(new Intent(MainActivity.this, NpsService.class));

                    ivStatus.setImageResource(R.mipmap.radio_button_select_red);
                    btnStart.setText("Start");
                }

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        TinyHttpd.getInstance().setOnserveListener(null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (npsConn != null){
            unbindService(npsConn);
        }
    }

    @Override
    public void onServe(String result) {
        if (!StringUtils.isEmpty(result)){
            rv_json.bindJson(result);
            Log.d("response:" , result+"");
        }
    }

    public String getIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void onNpsServiceLog(String log) {
        tvLog.setText(tvLog.getText().toString() + "\n" +log);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }
}
