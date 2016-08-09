package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

/**
 * Created by kaynat on 8/8/16.
 */
public class WeMoTurnOff extends AsyncTask<String, Void, String> {
    WemoLightDevice w;
    public WeMoTurnOff(WemoLightDevice w){
        this.w = w;
    }
    @Override
    protected String doInBackground(String... strings) {
        w.turnOff();
        return "success";
    }
}
