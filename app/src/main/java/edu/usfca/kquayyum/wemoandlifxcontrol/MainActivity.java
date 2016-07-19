package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
    public void discoverWemo(View view) throws IOException, InterruptedException {

        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        WemoDiscovery wemoDiscovery = new WemoDiscovery();
        Set<String> endpoints = null;

        //EditText editText = (EditText) findViewById(R.id.edit_message);
        WemoTurnOn wemoTurnOn = new WemoTurnOn();

        wemoTurnOn.execute();
        message = "Discovered devices";
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);

    }

    /** Called when the user clicks the lifx button */
    public void discoverLifX(View view) {

        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
       // EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "List of lifx devices";


        LifXSetColor lifXSetColor = new LifXSetColor();
        try {
            lifXSetColor.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
}
