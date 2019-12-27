package com.chenqiao.tinyhttpserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chenqiao.nps.NpsThread;
import com.chenqiao.server.TinyHttpd;
import com.chenqiao.util.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements TinyHttpd.OnServListener {

    private TextView textView;
    private TextView textView_ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyHttpd.getInstance().setOnserveListener(this);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
        textView_ip = findViewById(R.id.tv_ip);
        textView_ip.setText(textView_ip.getText() + getIP());
        TinyHttpd.getInstance().startServer();


        new NpsThread().start();
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
}
