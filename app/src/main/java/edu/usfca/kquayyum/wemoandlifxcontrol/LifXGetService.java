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


public class LifXGetService extends AsyncTask<String, Void, String> {
    private static final String TAG = "LifXGetService";
    private static final int LIFX_PORT = 56700;
    private static final int LIFX_WAIT_TIME_MS = 5000;
    private Context context = null;

    private WifiManager wifiM = null;
    private HashSet<String> lifXHosts;

    public LifXGetService(WifiManager wm, Context context) {
        this.wifiM = wm;
        this.context = context;
        this.lifXHosts = new HashSet<>();
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = wifiM.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
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
            InetAddress bCast = getBroadcastAddress();
            String messageString = "240000347B00000000000000000000000000000000000164000000000000000017000000";
            byte[] message = hexStringToByteArray(messageString);

            dsocket = new DatagramSocket();
            dsocket.setBroadcast(true);
            dsocket.setSoTimeout(LIFX_WAIT_TIME_MS);

            DatagramPacket packet = new DatagramPacket(message, message.length, bCast, LIFX_PORT);
            dsocket.send(packet);

            receiveResponse(dsocket);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!dsocket.isClosed()) {
                dsocket.close();
            }
        }

        System.out.println("Done: LifX Discovery");
        System.out.println(lifXHosts.size());
        if(lifXHosts.size()>0) {
            String[] str = new String[lifXHosts.size()];
            Intent intent = new Intent(context, DiscoverLightsActivity.class);
            int i = 0;
            for(String s: lifXHosts){
                str[i++] = s;
            }
            intent.putExtra("list", str);
            context.startActivity(intent);
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

                lifXHosts.add(lifxHost);
                System.err.println(lifxHost);
                System.err.println(packet.getPort());
                System.err.println(packet.getData()[32]);

                String p = new String(packet.getData()).trim();
                System.err.println(p);
            }

        }
        catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }
}
