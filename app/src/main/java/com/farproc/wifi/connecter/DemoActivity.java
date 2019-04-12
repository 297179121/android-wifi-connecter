package com.farproc.wifi.connecter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static com.farproc.wifi.connecter.MainActivity.EXTRA_HOTSPOT;

public class DemoActivity extends AppCompatActivity {

    private WifiManager mWifiManager;
    private Map<String, ScanResult> mapScanResult = new HashMap<>();
    private final static String SSID = "flashair";

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if( SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()) ){
                List<ScanResult> list = mWifiManager.getScanResults();
                for (ScanResult result : list) {
                    if( result.SSID.contains(SSID) && !mapScanResult.containsKey(SSID) ){
                        mapScanResult.put(SSID, result);
                        intent.setClass(context, MainActivity.class);
                        intent.putExtra(EXTRA_HOTSPOT, result);
                        startActivity(intent);
                        return;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
    }

    public void starWifi(View view){
        if( mWifiManager.getConnectionInfo()!=null && mWifiManager.getConnectionInfo().getSSID().contains(SSID) ){
            Toast.makeText(this, "当前选在无线网络"+SSID, Toast.LENGTH_SHORT).show();
            return;
        }
        if( mapScanResult.isEmpty() ){
            mWifiManager.startScan();
        }else{
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            intent.putExtra(EXTRA_HOTSPOT, mapScanResult.get(SSID));
            startActivity(intent);
        }

    }
}
