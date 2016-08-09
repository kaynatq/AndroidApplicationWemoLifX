package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class WeMoBulbAction extends AppCompatActivity {

    public void powerOnWemo(View view){
        String name = getIntent().getStringExtra("lightName");
        WemoLightDevice wemoLightDevice = MainActivity.lights.get(name);
        if(wemoLightDevice != null){
            WeMoTurnOn weMoTurnOn = new WeMoTurnOn(wemoLightDevice);
            weMoTurnOn.execute();
        }
    }

    public void powerOffWemo(View view){
        String name = getIntent().getStringExtra("lightName");
        WemoLightDevice wemoLightDevice = MainActivity.lights.get(name);
        if(wemoLightDevice != null){
            WeMoTurnOff weMoTurnOff = new WeMoTurnOff(wemoLightDevice);
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
