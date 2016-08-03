package edu.usfca.kquayyum.wemoandlifxcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

public class Listener implements Runnable{
    Boolean listening = true;
    Set<String> endpoints;

    public void run(){
        //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
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

    private void processPacket(DatagramPacket packet) {
        String originaldata = new String(packet.getData());
        byte[] packetData = packet.getData();
        //System.out.println(packetData);
        //System.out.println(originaldata.getBytes());
        System.out.println(originaldata);
        if (originaldata.contains("urn:Belkin:device:controllee") || originaldata.contains("urn:Belkin:device:lightswitch")) {
            if (originaldata.toLowerCase().indexOf("location:") > -1) {
                String location = originaldata.substring(originaldata.toLowerCase().indexOf("location:"));
                location = location.substring(0, location.indexOf("\n"));
                location = location.substring(location.indexOf(":") + 1, location.length());
                endpoints.add(location.trim());
            }
        }
    }

    private void startListener() throws IOException, InterruptedException {
        System.out.println("Start listening");

        Set<String> endpoints = new HashSet<>();
        MulticastSocket recSocket = new MulticastSocket(1901);
        // recSocket.bind(new InetSocketAddress("192.168.1.82", 1901));
        recSocket.setTimeToLive(10);
        recSocket.setSoTimeout(1000);
        recSocket.joinGroup(InetAddress.getByName("239.255.255.250"));

        //log.info("waiting to receive");
        while (listening)
        {
            byte[] buf = new byte[2048];
            DatagramPacket input = new DatagramPacket(buf, buf.length);
            try {
                recSocket.receive(input);
                processPacket(input);

            } catch (SocketTimeoutException e) {
                System.out.println("socket timeout");
            }
        }
        System.out.println("done listening" );

        recSocket.leaveGroup(InetAddress.getByName("239.255.255.250"));
        recSocket.disconnect();
        recSocket.close();
        for(String ep: endpoints){
            System.out.println(ep);
        }
    }
}