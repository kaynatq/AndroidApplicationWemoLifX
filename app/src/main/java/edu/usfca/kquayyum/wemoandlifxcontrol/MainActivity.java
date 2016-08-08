package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

/**
 * The start page
 */
public class MainActivity extends AppCompatActivity {
    private static final Logger log= Logger.getLogger( MainActivity.class.getName() );
    public final static String EXTRA_MESSAGE = "List of devices";
    public  String message = "";

    private WifiManager wifiM;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    public void discoverLights(View view) {
        wifiM = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifiM
                .createMulticastLock("multicastLock");
        multicastLock.acquire();

        WemoBridgeDiscover d = new WemoBridgeDiscover();
        try {
            d.discover();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        //WeMoDiscoverySSDP weMoDiscovery = new WeMoDiscoverySSDP(this, wifiM);
        //weMoDiscovery.execute();

        /*
        WeMoSDKContext weMoSDKContext = new WeMoSDKContext(this.getBaseContext());
        ArrayList<String> upnp = weMoSDKContext.getListOfWeMoDevicesOnLAN();

        for(String s: upnp){
            System.out.println(s);
        }*/

      //  System.out.println(WeMoDevice.LIGHT_SWITCH);
   //     LifXGetService lifXX = new LifXGetService(wifiM, this);
     //   lifXX.execute();

    }
}
