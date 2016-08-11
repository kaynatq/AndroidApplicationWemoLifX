package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

/**
 * Class that do the thurning off for wemo in a separate thread
 */
public class WeMoTurnOff extends AsyncTask<String, Void, String> {
    WeMoLightDevice w;
    public WeMoTurnOff(WeMoLightDevice w){
        this.w = w;
    }
    @Override
    protected String doInBackground(String... strings) {
        w.turnOff();
        return "success";
    }
}
