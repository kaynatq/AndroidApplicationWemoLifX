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
    private String host = null;
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public LifXSetPowerOn(String host){
        this.host = host;
    }
    @Override
    protected String doInBackground(String... strings) {
        try {

            int port = 56700;
            String messageString = "2A0000340000000000000000000000000000000000000000000000000000000075000000FFFF00040000";
            byte[] message = hexStringToByteArray(messageString);
            InetAddress address = InetAddress.getByName(host);
            DatagramSocket dsocket = new DatagramSocket();
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

