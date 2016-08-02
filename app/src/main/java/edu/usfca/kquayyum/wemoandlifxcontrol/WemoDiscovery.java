package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * class to discover UPnP devices
 * @author kaynat
 *
 */
public class WemoDiscovery extends AsyncTask<String, Void, String>{
    Listener listener;
    Long msToListen;
    Set<String> endpoints = new HashSet<>();
    private static final int LISTENER_DELAY_SECONDS = 5;
    private static final int NUM_UPNP_RETRIES = 3;

    public WemoDiscovery() {
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            getEndpoints();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return endpoints.toString();
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

        Thread listenerThread = new Thread(listener);
        listenerThread.start();

        discover();

        new Reminder(listener, LISTENER_DELAY_SECONDS);

        listenerThread.join();
        return endpoints;
    }

    private void discover() throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);

        System.out.println("Start discovery");

        int numTry = NUM_UPNP_RETRIES;
        while (numTry > 0) {
            MulticastSocket socket = new MulticastSocket(1901);
            try {
                // socket.bind(new InetSocketAddress("192.168.1.90", 1901));
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
                e.printStackTrace();
                listener.terminate();
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

        System.out.println("end discovery");
    }
}