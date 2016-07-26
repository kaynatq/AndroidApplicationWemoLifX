package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

/**
 * Class to Turn on the light
 */
public class LifXSetPowerOn extends AsyncTask<String, Void, String> {
    private static int LIFX_SEND_PORT = 10000;

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String host = "192.168.1.93";
            int port = 56700;

            String messageString = "31000034000000000000000000000000000000000000000000000000000000002100000000FFFFFFFFFFFFAC0D00040000";
            byte[] message = messageString.getBytes();

            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);



            // Create a datagram socket, send the packet through it, close it.
            DatagramSocket dsocket = new DatagramSocket(10000);
            dsocket.setBroadcast(true);

            // Initialize a datagram packet with data and address
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

