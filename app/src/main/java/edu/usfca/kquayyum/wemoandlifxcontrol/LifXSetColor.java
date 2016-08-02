package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Set the color of light bulb according to your choice
 */
public class LifXSetColor extends AsyncTask<String, Void, String> {
    private static int LIFX_SEND_PORT = 10000;
    private String colorString = null;

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public LifXSetColor(String color){
        this.colorString = color;
    }
    @Override
    protected String doInBackground(String... strings) {
        // Create a datagram socket, send the packet through it, close it.
        DatagramSocket dsocket = null;

        try {
            String host = "192.168.1.93";
            int port = 56700;
            System.out.println("inside set color");
            String messageString = "31000034000000000000000000000000000000000000000000000000000000006600000000" +
                    colorString +
                    "FFFFFFFFAC0D00040000";

            byte[] message = hexStringToByteArray(messageString);
            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);
            System.out.println(address);
            System.out.println(colorString);
            // Initialize a datagram packet with data and address

            dsocket = new DatagramSocket(LIFX_SEND_PORT);
            dsocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, port);

            dsocket.send(packet);
          //  dsocket.close();
           // System.out.println(packet.getData());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dsocket != null) {
                dsocket.close();
            }
        }

        return "send successful";
    }

    @Override
    protected void onPostExecute(String s) {

    }
}

