package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Timer;

/**
 * Listener that responds if any lifX device exist in network
 */
public class LifXListener extends AsyncTask<String, Void, String>{
    Boolean listening = true;

    public void terminate() {
        listening = false;
    }

    public void startListener() throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName("192.168.1.90");
        listening = true;
        // Initialize a datagram packet with data and address
        int PORT = 9999;
        // Create a datagram socket, send the packet through it, close it.
        DatagramSocket dsocket = new DatagramSocket(PORT);
        dsocket.setBroadcast(true);


        //log.info("waiting to receive");
        while (listening)
        {
            byte[] buf = new byte[2048];
            DatagramPacket input = new DatagramPacket(buf, buf.length, address, PORT);
            try {
                dsocket.receive(input);
                System.out.println(input.getData());

            } catch (SocketTimeoutException e) {
                System.out.println("socket timeout");
            }
        }

        dsocket.disconnect();
        dsocket.close();

    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            startListener();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "executed";
    }
}
