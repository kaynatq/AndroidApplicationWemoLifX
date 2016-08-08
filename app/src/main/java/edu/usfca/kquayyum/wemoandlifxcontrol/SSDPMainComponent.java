package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kaynat on 8/5/16.
 */
public class SSDPMainComponent {
    private SSDPSocket multicastSocket;
    private SSDPSocket listenerSocket;
    private Context ctx;
    private boolean isMulticastSocketEnabled = false;
    private static final long M_SEARCH_BROADCASTING_INTERVAL = 60000; // In milliseconds, every 60 seconds

    Thread socketInitThread;
    WifiManager wm;

    private static String TAG = "SSDPMainComponent";

    public void initSSDPComponent() {
        // Acquire multicast lock to broadcast udp packets to SSDP address
        WifiManager.MulticastLock multicastLock = wm
                .createMulticastLock("multicastLock");
        multicastLock.acquire();

        Log.d(TAG, "Init: Acquired Lock");


        // Start receiving M-SEARCH and NOTIFY messages sent by other
        // devices

        // Start receiving device information sent directly from other
        // devices
        // upon sending M-SEARCH messages from this device
        //startReceivingSSDPMessages();
        startReceivingSSDPDeviceInfo();
        Log.d(TAG, "Receive : Started");

        // Start sending M-SEARCH messages to SSDP broadcasting address
        // periodically
        startPeriodicMSearch();
        Log.d(TAG, "SendSearch : Done");

        // Start sending NOTIFY messages to SSDP broadcasting address
        // periodically
    }
    public SSDPMainComponent(Context context, WifiManager wm) throws IOException {
        this.wm = wm;
        /*
        ROOT_DEVICE_URL = "http://" + InetAddress.getLocalHost().getHostAddress()
                        + ":" + 10000
                + ROOT_DEVICE_XML_URL_PATH;
                 */
        socketInitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Socket need to be created on a separate thread.
                    multicastSocket = new SSDPSocket();
                    listenerSocket = new SSDPSocket();
                } catch (IOException e) {
                    Log.e("SSDPMainComponent", "SSDPMainComponent IOException", e);
                }
            }
        });
        socketInitThread.start();
        try {
            socketInitThread.join();
        } catch (InterruptedException e) {
            Log.e("SSDPMainComponent", "SSDPMainComponent InterruptedException", e);
        }
        ctx = context;
    }

    public void startPeriodicMSearch() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMSearchMessage();
            }
        }, 0, M_SEARCH_BROADCASTING_INTERVAL);
    }

    public void startReceivingSSDPDeviceInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveSSDPDeviceInfo();
            }
        }).start();
    }

    public void startReceivingSSDPMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveSSDPMessages();
            }
        }).start();
    }

    public void sendMSearchMessage() {
        SSDPResponseMsg searchRootDevice = new SSDPResponseMsg(SSDPConstants.HEADER_ST, SSDPConstants.HEADER_USN, SSDPConstants.HEADER_LOCATION);

        Log.d(TAG, searchRootDevice.toString());
        try {
            Log.d(TAG, searchRootDevice.toString());
            StringBuilder packet = new StringBuilder();
            packet.append("M-SEARCH * HTTP/1.1\r\n");
            packet.append("HOST: 239.255.255.250:1900\r\n");
            packet.append("MAN: \"ssdp:discover\"\r\n");
            packet.append("MX: 2").append("\r\n");
            packet.append("ST: ").append("ssdp:rootdevice").append("\r\n").append("\r\n");
            packet.append("ST: ").append("urn:Belkin:device:controller:1").append("\r\n").append("\r\n");
            byte[] data = packet.toString().getBytes();
            System.out.println("sending discovery packet");
            multicastSocket.send((new DatagramPacket(data, data.length)).toString());

         //   multicastSocket.send(searchRootDevice.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("SSDPMainComponent", "SendMSearchMessage IOException", e);
        }
    }

    private void receiveSSDPMessages() {
        while (true) {
            DatagramPacket dp;
            try {
                dp = listenerSocket.receive();
                String packetData = new String(dp.getData(), 0, dp.getLength());
                /*
                ByteArrayInputStream byteIn = new ByteArrayInputStream (dp.getData
                        (), 0, dp.getLength ());
                 */
                System.out.println(packetData.toCharArray());

                /*
                ObjectInputStream oin = new ObjectInputStream(byteIn);
                Object obj = oin.readObject();
                String recvString = obj.toString();
                DataInputStream dataIn = new DataInputStream (byteIn);



                recvString = dataIn.readLine();
                Log.i("ReceiveSSDPMessages", recvString);
                */

              /*  SSDPDiscoveryPacketParser packetParser = new SSDPDiscoveryPacketParser(packetData);
                if (!packetParser.isValidDiscoveryPacket())
                    continue;

                if (packetParser.isNotifyMessage()) {
                    //TODO: Add received device information to DB
                }
                else {
                    //Respond with this device's information back to the requested device
                    SendMSearchResponseMsg(packetParser.getMX(),dp.getAddress(),dp.getPort());
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
/*
    private void sendMSearchResponseMsg(final int mx, final InetAddress destAddr, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mx * 1000);
                    SSDPSearchMsg responseMsg = new SSDPSearchMsg(ROOT_DEVICE_URL);
                    Log.i("MSearchResponse","DEST ADDR: " + destAddr.getHostAddress() + ":" + Integer.toString(port));
                    listenerSocket.send(destAddr.getHostAddress(), port, responseMsg.getmST());
                } catch (InterruptedException e) {
                    Log.e("SSDPMainComponent", "SendMSearchResponseMsg InterruptedException",e);
                } catch (IOException e) {
                    Log.e("SSDPMainComponent", "SendMSearchResponseMsg IOException",e);
                }
            }
        }).start();
    }
*/
    private void receiveSSDPDeviceInfo() {
        if (multicastSocket == null || isMulticastSocketEnabled) {
            return;
        }
        isMulticastSocketEnabled = true;
        while (true) {
            try {
                DatagramPacket dp;
                dp = listenerSocket.receive();
                String packetData = new String(dp.getData(), 0, dp.getLength());
                Log.v("SSDP PACKET DATA", packetData.toString());

               /* SSDPDevicePacketParser packetParser = new SSDPDevicePacketParser(packetData);

                if (!packetParser.requestXML())
                    continue;
                DeviceDbAdapter deviceDbAdapter = DeviceDbAdapter.getInstance();
                ServiceApiDbAdapter serviceApiDbAdapter = ServiceApiDbAdapter.getInstance();

                try {
                    deviceDbAdapter.open();
                    DeviceInfoStruct deviceStruct = packetParser.getDeviceInfo();
                    //If Invalid XML Info (with respect to LG protocol including Port/Device ID in location xml), go to next loop
                    if (deviceStruct == null || TextUtils.isEmpty(deviceStruct.getServerPort()) || TextUtils.isEmpty(deviceStruct.getDeviceID()) ||
                            TextUtils.isEmpty(deviceStruct.getDeviceID()) || TextUtils.isEmpty(deviceStruct.getManufacturer()))
                        continue;

                    serviceApiDbAdapter.open();
                    ServiceStruct serviceStruct = packetParser.getServiceAPI();

                    //Invalid XML Info for services, go to next loop
                    if (serviceStruct == null || TextUtils.isEmpty(serviceStruct.getDeviceID()) || serviceStruct.getAPICount() <= 0)
                        continue;

                    if (deviceDbAdapter.hasDevice(deviceStruct.getDeviceID())) {
                        deviceDbAdapter.updateDevice(deviceStruct);
                        Log.i("ReceiveSSDPDeviceInfo", "Updated Device: " + deviceStruct.getServerIP() + ":" + deviceStruct.getServerPort());
                    }
                    else {
                        deviceDbAdapter.insertDevice(deviceStruct);
                        Log.i("ReceiveSSDPDeviceInfo", "Inserted Device: " + deviceStruct.getServerIP() + ":" + deviceStruct.getServerPort());
                    }


                    ArrayList<APIStruct> apiList = serviceStruct.getAPIStructList();
                    String deviceID = serviceStruct.getDeviceID();
                    for (int i = 0; i < apiList.size(); i++) {
                        String apiType = apiList.get(i).getApiType();
                        ArrayList<String> apiNameList = apiList.get(i).getApiNameList();
                        for (int j = 0; j < apiNameList.size(); j++) {
                            String apiName = apiNameList.get(j);
                            if (!serviceApiDbAdapter.hasServiceAPI(deviceID, apiType, apiName))
                                serviceApiDbAdapter.insertServiceAPI(deviceID,apiType,apiName);
                        }
                    }
*/
                } catch (IOException e) {
                    Log.e("SSDPMainComponent", "ReceiveSSDPDeviceInfo SAXException", e);
                } /*catch (ParserConfigurationException e) {
                    Log.e("SSDPMainComponent", "ReceiveSSDPDeviceInfo ParserConfigurationException", e);
                }
                finally {
                    if (deviceDbAdapter.isOpen())
                        deviceDbAdapter.close();

                    if (serviceApiDbAdapter.isOpen())
                        serviceApiDbAdapter.close();
                }
            } catch (IOException e) {
                Log.e("SSDPMainComponent", "ReceiveSSDPDeviceInfo IOException", e);
            }*/
        }
    }

}
