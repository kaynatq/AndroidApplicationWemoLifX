package edu.usfca.kquayyum.wemoandlifxcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Listener that responds if any lifX device exist in network
 */
public class LifXListener implements Runnable {
    Boolean listening = true;
    public void run(){
        try {
            startListener();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void terminate() {
        listening = false;
    }

    private void startListener() throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName("192.168.1.254");

        // Initialize a datagram packet with data and address
        int PORT = 56700;
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

            } catch (SocketTimeoutException e) {
                System.out.println("socket timeout");
            }
        }

        dsocket.disconnect();
        dsocket.close();

    }
}
