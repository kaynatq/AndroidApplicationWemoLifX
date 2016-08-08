package edu.usfca.kquayyum.wemoandlifxcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by kaynat on 8/5/16.
 */

public class SSDPSocket {
    MulticastSocket ssdpSocket;

    public SSDPSocket() throws IOException {
        ssdpSocket = new MulticastSocket(SSDPConstants.PORT);
        InetAddress broadcastAddress = InetAddress.getByName(SSDPConstants.ADDRESS);
        ssdpSocket.joinGroup(broadcastAddress);
    }

    /* Used to send SSDP packet */
    public void send(final String data) throws IOException {
        send(SSDPConstants.ADDRESS, SSDPConstants.PORT, data);
    }

    public void send(final String address, final int port, final String data) throws IOException {
        DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(),
                InetAddress.getByName(address), port);

        ssdpSocket.send(dp);
    }

    /* Used to receive SSDP packet */
    public DatagramPacket receive() throws IOException {
        byte[] buf = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);

        ssdpSocket.receive(dp);

        return dp;
    }

    public void setTimeout(final int timeout) throws IOException {
        ssdpSocket.setSoTimeout(timeout);
    }

    public void close() {
        if (ssdpSocket != null) {
            ssdpSocket.close();
        }
    }
}
