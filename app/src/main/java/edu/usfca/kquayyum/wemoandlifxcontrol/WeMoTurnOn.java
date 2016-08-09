package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

/**
 * Created by kaynat on 8/8/16.
 */
public class WeMoTurnOn extends AsyncTask<String, Void, String> {
    WemoLightDevice w;
    public WeMoTurnOn(WemoLightDevice w){
        this.w = w;
    }
    @Override
    protected String doInBackground(String... strings) {
        w.turnOn();
        return "success";
    }
}
