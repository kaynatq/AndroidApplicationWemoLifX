package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class to Turn off the light
 */
public class LifXSetPowerOff extends AsyncTask<String, Void, String> {
    private String host = "";
    private static final int LIFX_PORT = 56700;
    private static final String POWER_OFF_MESSAGE_STRING =
            "2A0000340000000000000000000000000000000000000000000000000000000075000000000000040000";

    public LifXSetPowerOff(String host){
        this.host = host;
    }
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
            byte[] message = hexStringToByteArray(POWER_OFF_MESSAGE_STRING);
            InetAddress address = InetAddress.getByName(host);
            DatagramSocket dsocket = new DatagramSocket();
            dsocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, LIFX_PORT);
            dsocket.send(packet);
            dsocket.close();
            System.out.println(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "send successful";
    }
}

