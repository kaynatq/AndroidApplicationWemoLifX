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

import lifx.java.android.network_context.LFXNetworkContext;


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
    private class WemoDiscovery extends AsyncTask<String, Void, String>{
        Listener listener;
        Long msToListen;
        Set<String> endpoints = new HashSet<>();
        private static final int LISTENER_DELAY_SECONDS = 5;

        public WemoDiscovery() {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return getEndpoints().toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }



        private class Reminder {
            Timer timer;

            private Reminder(Listener listener, int seconds) {
                timer = new Timer();
                timer.schedule(new RemindTask(listener), seconds*1000);
            }

            class RemindTask extends TimerTask {
                Listener listener;
                RemindTask(Listener listener) {
                    this.listener = listener;
                }
                public void run() {
                    listener.terminate();
                    timer.cancel(); //Terminate the timer thread
                }
            }
        }
        public Set<String> getEndpoints() throws IOException, InterruptedException {
            listener = new Listener();
            listener.doInBackground(null);
            Thread listenerThread = new Thread(listener);
            listenerThread.start();

            discover();

            new Reminder(listener, LISTENER_DELAY_SECONDS);

            listenerThread.join();
            return endpoints;
        }

        private void discover() throws IOException {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);

            int numTry = 3;
            while (numTry > 0) {
                MulticastSocket socket = new MulticastSocket(null);
                try {
                    socket.bind(new InetSocketAddress("192.168.1.90", 1901));
                    StringBuilder packet = new StringBuilder();
                    packet.append("M-SEARCH * HTTP/1.1\r\n");
                    packet.append("HOST: 239.255.255.250:1900\r\n");
                    packet.append("MAN: \"ssdp:discover\"\r\n");
                    packet.append("MX: 2").append("\r\n");
                    packet.append("ST: ").append("ssdp:rootdevice").append("\r\n").append("\r\n");
                    packet.append("ST: ").append("urn:Belkin:device:controller:1").append("\r\n").append("\r\n");
                    byte[] data = packet.toString().getBytes();
                    System.out.println("sending discovery packet");
                    socket.send(new DatagramPacket(data, data.length, socketAddress));
                } catch (IOException e) {
                    listener.terminate();
                    throw e;
                } finally {
                    socket.disconnect();
                    socket.close();
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                numTry--;
            }
        }
    }

    /** Called when the user clicks the wemo button */
    public void discoverWemo(View view) throws IOException, InterruptedException {

        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        WemoDiscovery wemoDiscovery = new WemoDiscovery();
        Set<String> endpoints = null;

        //EditText editText = (EditText) findViewById(R.id.edit_message);
        WemoControl wemoControl = new WemoControl();

        wemoControl.execute();
        message = "Wemo turned on";
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);

    }

    /** Called when the user clicks the lifx button */
    public void discoverLifX(View view) {

        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
       // EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "List of lifx devices";

        LFXNetworkContext localNetworkContext = null;
        LifXClient lifXClient = new LifXClient();
        try {
            lifXClient.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for(int i =0; i<5; i++) {
            localNetworkContext = LFXClient.getSharedInstance(this).getLocalNetworkContext();

            localNetworkContext.connect();


            localNetworkContext.getAllLightsCollection().setPowerState(LFXTypes.LFXPowerState.OFF);
            localNetworkContext.getAllLightsCollection().getLights().size();
        }*/
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }
}
