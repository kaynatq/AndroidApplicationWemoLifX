package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.util.Log;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kaynat on 8/7/16.
 * Attribution: Some part of the code taken from https://github.com/bitletorg/weupnp
 *
 *
 */
public class WeMoBridgeDiscover {

    /**
     * The SSDP port
     */
    public static final int PORT = 1900;
    private Context ctx;

    /**
     * The broadcast address to use when trying to contact UPnP devices
     */
    public static final String IP = "239.255.255.250";
    public static HashSet<WeMoLightDevice> str = new HashSet<>();

    /**
     * The default timeout for the initial broadcast request
     */
    private static final int DEFAULT_TIMEOUT = 3000;

    /**
     * The timeout for the initial broadcast request
     */
    private int timeout = DEFAULT_TIMEOUT;

    /**
     * Search only "rootdevice" to get Wemo light switches
     */
    private String searchType = "upnp:rootdevice";

    /**
     * Tag for debug logs.
     */
    private static String DEBUG_TAG = "WeMoBridgeDiscover";
    public WeMoBridgeDiscover(Context ctx){
     this.ctx = ctx;
    }

    private Set<WeMoBridgeDevice> bridges = new HashSet<WeMoBridgeDevice>();


    /*
      *  Thread class for sending a search datagram and process the response.
      */
    private class SendDiscoveryThread extends Thread {
        InetAddress ip;
        String searchMessage;
        SendDiscoveryThread(InetAddress localIP, String searchMessage) {
            this.ip = localIP;
            this.searchMessage = searchMessage;
        }

        @Override
        public void run() {
            DatagramSocket ssdp = null;

            try {
                // Create socket bound to specified local address
                ssdp = new DatagramSocket(new InetSocketAddress(ip, 0));

                byte[] searchMessageBytes = searchMessage.getBytes();
                DatagramPacket ssdpDiscoverPacket = new DatagramPacket(searchMessageBytes, searchMessageBytes.length);
                ssdpDiscoverPacket.setAddress(InetAddress.getByName(IP));
                ssdpDiscoverPacket.setPort(PORT);

                ssdp.send(ssdpDiscoverPacket);
                ssdp.setSoTimeout(WeMoBridgeDiscover.this.timeout);

                boolean waitingPacket = true;
                while (waitingPacket) {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[1536], 1536);
                    try {
                        ssdp.receive(receivePacket);
                        byte[] receivedData = new byte[receivePacket.getLength()];
                        System.arraycopy(receivePacket.getData(), 0, receivedData, 0, receivePacket.getLength());

                        WeMoBridgeDevice bridgeDevice =
                                WeMoBridgeDevice.parseMSearchReply(receivedData);

                        // Only add the device if it is a Wemo Bridge
                        if (bridgeDevice.getUsn().startsWith("uuid:Bridge")) {
                            bridges.add(bridgeDevice);
                        }
                    } catch (SocketTimeoutException ste) {
                        Log.d(DEBUG_TAG, "Discovery Socket Timeout Reached.");
                        waitingPacket = false;
                    }
                }

                for (WeMoBridgeDevice b : bridges) {
                    Log.d(DEBUG_TAG, "Getting detailed Bridge information.");
                    b.loadBridgeInfo();

                    Log.d(DEBUG_TAG, "Getting Light Information");
                    List<WeMoLightDevice> lightList = b.getLights();

                    for (WeMoLightDevice l : lightList) {
                        if (l.getProductName().compareToIgnoreCase(WeMoLightDevice.productNameType) == 0) {
                            MainActivity.lights.put(l.getDeviceId(), l);
                            Log.d(DEBUG_TAG, l.toString());
                        }
                    }
                }
                if(MainActivity.lights.size() > 0)
                {
                    Iterator it = MainActivity.lights.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry e = (Map.Entry) it.next();
                        WeMoLightDevice l = (WeMoLightDevice) e.getValue();
                        str.add(l);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != ssdp) {
                    ssdp.close();
                }
            }
        }
    }

    /**
     * Discovers Gateway Devices on the network(s) the executing machine is
     * connected to.
     * <p/>
     * The host may be connected to different networks via different network
     * interfaces.
     * Assumes that each network interface has a different InetAddress and
     * returns a map associating every GatewayDevice (responding to a broadcast
     * discovery message) with the InetAddress it is connected to.
     *
     * @return a map containing a GatewayDevice per InetAddress
     * @throws SocketException
     * @throws UnknownHostException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */

    public void discover() throws IOException, SAXException, ParserConfigurationException {
        Collection<InetAddress> ips = getLocalInetAddresses();
        String searchMessage = "M-SEARCH * HTTP/1.1\r\n" +
                "HOST: " + IP + ":" + PORT + "\r\n" +
                "ST: " + searchType + "\r\n" +
                "MAN: \"ssdp:discover\"\r\n" +
                "MX: 2\r\n" +    // seconds to delay response
                "\r\n";

        // perform search requests for multiple network adapters concurrently
        Collection<SendDiscoveryThread> threads = new ArrayList<SendDiscoveryThread>();
        for (InetAddress ip : ips) {
            SendDiscoveryThread thread = new SendDiscoveryThread(ip, searchMessage);
            threads.add(thread);
            thread.start();
        }

        // wait for all search threads to finish
        for (SendDiscoveryThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // continue with next thread
            }
        }

        // return devices;
    }

    /**
     * Retrieves all local IPv4 address
     * @return Collection if {@link InetAddress}es
     */
    private List<InetAddress> getLocalInetAddresses() {
        List<InetAddress> arrayIPAddress = new ArrayList<>();

        // Get all network interfaces
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return arrayIPAddress;
        }

        if (networkInterfaces == null)
            return arrayIPAddress;

        // For every suitable network interface, get all IP addresses
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface card = networkInterfaces.nextElement();

            try {
                // skip devices, not suitable to search gateways for
                if (card.isLoopback() || card.isPointToPoint() || card.isVirtual() || !card.isUp()) {
                    continue;
                }
            } catch (SocketException e) {
                continue;
            }

            Enumeration<InetAddress> addresses = card.getInetAddresses();

            if (addresses == null)
                continue;

            while (addresses.hasMoreElements()) {
                InetAddress inetAddress = addresses.nextElement();
                if (Inet4Address.class.isInstance(inetAddress))
                    arrayIPAddress.add(inetAddress);
            }
        }

        return arrayIPAddress;
    }
}
