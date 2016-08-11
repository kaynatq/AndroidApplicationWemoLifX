package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Activity which is launched when we click on a discovered WeMo light
 */
public class WeMoBulbAction extends AppCompatActivity {

    public void powerOnWemo(View view){
        String name = getIntent().getStringExtra("lightName");
        WeMoLightDevice weMoLightDevice = MainActivity.lights.get(name);
        if(weMoLightDevice != null){
            WeMoTurnOn weMoTurnOn = new WeMoTurnOn(weMoLightDevice);
            weMoTurnOn.execute();
        }
    }

    public void powerOffWemo(View view){
        String name = getIntent().getStringExtra("lightName");
        WeMoLightDevice weMoLightDevice = MainActivity.lights.get(name);
        if(weMoLightDevice != null){
            WeMoTurnOff weMoTurnOff = new WeMoTurnOff(weMoLightDevice);
            weMoTurnOff.execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wemo_bulb_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
