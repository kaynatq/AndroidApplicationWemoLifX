package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

/**
 * Class that calls the turn on method over a Light object
 */
public class WeMoTurnOn extends AsyncTask<String, Void, String> {
    WeMoLightDevice w;
    public WeMoTurnOn(WeMoLightDevice w){
        this.w = w;
    }
    @Override
    protected String doInBackground(String... strings) {
        w.turnOn();
        return "success";
    }
}
