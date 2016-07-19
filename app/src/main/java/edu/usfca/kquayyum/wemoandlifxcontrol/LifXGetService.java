package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

/**
 * send UDP packet to network to discover lifx devices
 */


public class LifXGetService extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        try {
            String host = "192.168.1.254";
            int port = 56700;

            String messageString = "310000340000000000000000000000000000000000000000000000000000000002000000005555FFFFFFFFAC0D00040000";
            byte[] message = messageString.getBytes();
            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);

            // Initialize a datagram packet with data and address

            // Create a datagram socket, send the packet through it, close it.
            DatagramSocket dsocket = new DatagramSocket(port);
            dsocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, port);

            dsocket.send(packet);
            dsocket.close();
            System.out.println(packet);
        } catch (Exception e) {
            System.err.println(e);
        }
        return "send successful";
    }
}

