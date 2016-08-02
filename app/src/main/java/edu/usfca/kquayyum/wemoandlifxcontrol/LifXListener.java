package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.BitSet;
import java.util.Timer;

/**
 * Listener that responds if any lifX device exist in network
 */
public class LifXListener implements Runnable{
    Boolean listening = true;

    public void terminate() {
        listening = false;
    }


    public void startListener() throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName("192.168.1.255");
        listening = true;
        // Create a datagram socket, send the packet through it, close it.
        DatagramSocket dsocket = new DatagramSocket(10000);
        dsocket.setBroadcast(true);


        //log.info("waiting to receive");
        while (listening)
        {
            byte[] buf = new byte[50];
            DatagramPacket input = new DatagramPacket(buf, buf.length, address, 56700);
            try {
                dsocket.receive(input);
                System.out.println(input.getAddress().toString().substring(1, input.getAddress().toString().length()));
                System.out.println(input.getPort());

            } catch (SocketTimeoutException e) {
                System.out.println("socket timeout");
            }
        }

        dsocket.disconnect();
        dsocket.close();

    }

    @Override
    public void run() {
        try {
            startListener();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
