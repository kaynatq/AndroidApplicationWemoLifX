package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Set the color of light bulb according to your choice
 */
public class LifXSetColor extends AsyncTask<String, Void, String> {
    private static final int LIFX_PORT = 56700;
    private String colorString;
    private String host;

    /**
     * method that converts string to byte array
     * @param s
     * @return
     */
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Constructor
     * @param host
     * @param color
     */
    public LifXSetColor(String host, String color){
        this.host = host;
        this.colorString = color;
    }

    @Override
    protected String doInBackground(String... strings) {

        // Create a datagram socket, send the packet through it, close it.
        DatagramSocket dsocket = null;
        try {

            //This is the packet for setting a specific color to the light bulb.
            String messageString = "31000034000000000000000000000000000000000000000000000000000000006600000000" +
                    colorString +
                    "FFFFFFFFAC0D00040000";

            byte[] message = hexStringToByteArray(messageString);
            InetAddress address = InetAddress.getByName(host);
            dsocket = new DatagramSocket();
            dsocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, LIFX_PORT);
            dsocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dsocket != null) {
                dsocket.close();
            }
        }
        return "send successful";
    }
}

