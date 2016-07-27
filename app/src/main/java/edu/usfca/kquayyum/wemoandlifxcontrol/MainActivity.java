package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * The start page
 */
public class MainActivity extends AppCompatActivity {
    private static final Logger log= Logger.getLogger( MainActivity.class.getName() );
    public final static String EXTRA_MESSAGE = "List of devices";
    public  String message = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called when the user clicks the wemo button */
    public void discoverWemo(View view) throws IOException, InterruptedException, TimeoutException, ExecutionException {


        // Do something in response to button
        Intent intent = new Intent(this, DiscoverWemoActivity.class);

        WemoDiscovery wemoDiscovery = new WemoDiscovery();
        wemoDiscovery.execute();
        //EditText editText = (EditText) findViewById(R.id.edit_message);
    //    WemoTurnOn wemoTurnOn = new WemoTurnOn();

   //     wemoTurnOn.execute();
        message = "";
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);

    }



    //D0730512BE03
    /** Called when the user clicks the lifx button */
    public void discoverLifX(View view) {

        // Do something in response to button
        Intent intent = new Intent(this, DiscoverLifXActivity.class);
       // EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "List of lifx devices";

    /*    LFXNetworkContext localNetworkContext = LFXClient.getSharedInstance( this).getLocalNetworkContext();
        localNetworkContext.connect();
        localNetworkContext.getAllLightsCollection().setPowerState( LFXTypes.LFXPowerState.OFF);*/



    /*    LifXListener lifXListener = new LifXListener();
        lifXListener.execute();

        LifXSetPowerOn lifXSetColor = new LifXSetPowerOn();
        try {
            lifXSetColor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lifXListener.terminate();
        new Reminder(lifXListener, 5);
        lifXListener.cancel(true);
        lifXSetColor.cancel(true);*/

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
}
