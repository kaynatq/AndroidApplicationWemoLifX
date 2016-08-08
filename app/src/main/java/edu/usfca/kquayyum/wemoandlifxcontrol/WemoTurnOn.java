package edu.usfca.kquayyum.wemoandlifxcontrol;

import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Turn on wemo
 */
public class WemoTurnOn extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL("http://192.168.1.92:49153/upnp/control/bridge1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "not executed";
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return "not executed";
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return "not executed";
        }
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("SOAPACTION", "\"urn:Belkin:service:bridge:1#SetDeviceStatus\"");
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                        + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
                        + "<s:Body>"
                        + "<u:SetDeviceStatus xmlns:u=\"urn:Belkin:service:bridge:1\">"
                        + "<DeviceStatusList>"
                        + "&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;DeviceStatus&gt;&lt;IsGroupAction&gt;NO&lt;/IsGroupAction&gt;&lt;DeviceID available=&quot;YES&quot;&gt;94103EA2B277FE87&lt;/DeviceID&gt;&lt;CapabilityID&gt;10006,10008&lt;/CapabilityID&gt;&lt;CapabilityValue&gt;1,250&lt;/CapabilityValue&gt;&lt;/DeviceStatus&gt;"
                        + "</DeviceStatusList>"
                        + "</u:SetDeviceStatus>"
                        + "</s:Body>"
                        + "</s:Envelope>";

        connection.setDoOutput(true);
        connection.setDoInput(true);


        try {
            OutputStream os = connection.getOutputStream();
            os.write(xml.getBytes());
            os.flush();
            os.close();
            int res = connection.getResponseCode();
            //System.out.println(res);
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            connection.disconnect();
        }
        catch (Exception e){
            return "not excuted";
        }
        return "executed";
    }
}
