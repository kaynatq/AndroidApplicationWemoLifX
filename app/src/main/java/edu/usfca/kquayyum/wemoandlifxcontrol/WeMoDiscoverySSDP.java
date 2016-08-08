package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by kaynat on 8/5/16.
 */
public class WeMoDiscoverySSDP extends AsyncTask<String, Void, String> {
    WifiManager wm;
    Context ctx;

    public WeMoDiscoverySSDP(Context ctx, WifiManager wm){
        this.wm = wm;
        this.ctx = ctx;
    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            SSDPMainComponent ssdpMainComponent = new SSDPMainComponent(ctx, wm);
            ssdpMainComponent.initSSDPComponent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
