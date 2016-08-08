package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashSet;

/**
 * send UDP packet to network to discover lifx devices
 */


public class LifXDim extends AsyncTask<String, Void, String> {
    private static final String TAG = "LifXGetService";
    private static final int LIFX_PORT = 56700;
    private static final int LIFX_WAIT_TIME_MS = 5000;
    private Context context = null;

    private WifiManager wifiM = null;
    private String lifXHost;

    public LifXDim(String host) {
        this.lifXHost = host;
    }



    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    protected String doInBackground(String... strings) {
        System.out.println("Start: LifX Discovery");

        DatagramSocket dsocket = null;
        try {

            String messageString = "240000347B00000000000000000000000000000000000164000000000000000065000000";
            byte[] message = hexStringToByteArray(messageString);

            dsocket = new DatagramSocket();
            dsocket.setBroadcast(true);
            dsocket.setSoTimeout(LIFX_WAIT_TIME_MS);

            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName(lifXHost), LIFX_PORT);
            dsocket.send(packet);

            receiveResponse(dsocket);

        //    System.err.println(packet.getPort());
         //   System.err.println(packet.getData()[32]);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            }



        return "send successful";
    }


    void receiveResponse(DatagramSocket dsoc) throws IOException {
        byte[] buf = new byte[1024];

        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                dsoc.receive(packet);

                String lifxHost = ((InetSocketAddress) packet.getSocketAddress())
                        .getAddress()
                        .getHostAddress();

                //lifXHosts.add(lifxHost);
                System.err.println(lifxHost);

                System.err.println(packet.getPort());
                System.err.println(packet.getData()[32]);
                StringBuilder colorString = new StringBuilder();
                String c;
                for(int i = 33; i<=36; i++) {
                    int k = packet.getData()[i] & 0xFF;
                    c = Integer.toHexString(k);
                    if(c.length() == 1)
                        c =  c + "0";
                    colorString.append(c);
                    System.out.println(colorString);
                }
                for(int i = 37; i<=38; i++) {
                    int k = packet.getData()[i] & 0xFF;
               //     if(k > 200)
                 //       k = k - 50;
                    c = Integer.toHexString(k);
                    if(c.length() == 1)
                        c =  c + "0";
                    colorString.append(c);

                }
                System.out.println(colorString);
                String messageString = "31000034000000000000000000000000000000000000000000000000000000006600000000" +
                        colorString +
                        "AC0D00040000";

                byte[] message = hexStringToByteArray(messageString);
                // Get the internet address of the specified host
                InetAddress address = InetAddress.getByName(lifxHost);
           //     System.out.println(address);
           //     System.out.println(colorString);
                // Initialize a datagram packet with data and address


                packet = new DatagramPacket(message, message.length,
                        address, 56700);

                dsoc.send(packet);
            }

        }
        catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }
}
