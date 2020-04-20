package com.chenqiao.tinyhttpserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chenqiao.nps.NpsThread;
import com.chenqiao.server.TinyHttpd;
import com.chenqiao.util.StringUtils;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.chenqiao.server.TinyHttpd.TINY_SERVR_PORT;

public class MainActivity extends AppCompatActivity implements TinyHttpd.OnServListener {

    private TextView textView_ip;
    private NpsThread npsThread;
    private JsonRecyclerView rv_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyHttpd.getInstance().setOnserveListener(this);
        setContentView(R.layout.activity_main);
        textView_ip = findViewById(R.id.tv_ip);

        rv_json = findViewById(R.id.rv_json);

        textView_ip.setText(textView_ip.getText() + getIP() + ":" + TINY_SERVR_PORT);

        TinyHttpd.getInstance().startServer();


        npsThread = new NpsThread("101.200.200.248:8024", "123456");
        npsThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TinyHttpd.getInstance().stopServer();
        npsThread.stopNps();
    }

    @Override
    public void onServe(String result) {
        if (!StringUtils.isEmpty(result)){
            rv_json.bindJson(result);
            Log.d("responce:" , result+"");
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
