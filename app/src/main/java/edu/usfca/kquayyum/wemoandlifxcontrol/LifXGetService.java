package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * send UDP packet to network to discover lifx devices
 */


public class LifXGetService extends AsyncTask<String, Void, String> {
    private int numTry = 3;
    LifXListener lifXListener = null;
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
        DatagramSocket dsocket = null;
        String host = "0.0.0.0";
        lifXListener = new LifXListener();
        Thread listenerThread = new Thread(lifXListener);
        listenerThread.start();
        int port = 56700;
        try {
                InetAddress address = InetAddress.getByName("192.168.1.93");
                String messageString = "240000347B00000000000000000000000000000000000164000000000000000002000000";
                byte[] message = hexStringToByteArray(messageString);

                dsocket = new DatagramSocket(10000);
                dsocket.connect(address, 56700);
                dsocket.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
                dsocket.send(packet);

            } catch(Exception e){
                e.printStackTrace();
                lifXListener.terminate();
            }
            finally{
                if(dsocket.isConnected()) {
                    dsocket.disconnect();
                }
                if (!dsocket.isClosed()) {
                    dsocket.close();
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        new Reminder(lifXListener, 5);
        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "send successful";
    }
}

