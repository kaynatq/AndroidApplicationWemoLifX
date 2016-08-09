package edu.usfca.kquayyum.wemoandlifxcontrol;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by kaynat on 8/7/16.
 */
public class WemoUpnpUtils {
    public static final int DEFAULT_HTTP_RECEIVE_TIMEOUT = 7000;

    public static final SAXParserFactory saxFactory = SAXParserFactory.newInstance();




    /**
     * Issues UPnP commands to a GatewayDevice that can be reached at the
     * specified <tt>url</tt>
     * <p/>
     * The command is identified by a <tt>service</tt> and an <tt>action</tt>
     * and can receive arguments
     *
     * @param parser  XMLParser for the output, can be 'null'
     * @param url     the url to use to contact the device
     * @param service the service to invoke
     * @param action  the specific action to perform
     * @param args    the command arguments
     * @throws IOException  on communication errors
     * @throws SAXException if errors occur while parsing the response
     */
    public static void simpleUPnPCommand(XMLReader parser,
                                         String url,
                                         String service,
                                         String action,
                                         Map<String, String> args)
            throws IOException, SAXException {
        String soapAction = "\"" + service + "#" + action + "\"";
        StringBuilder soapBody = new StringBuilder();

        soapBody.append("<?xml version=\"1.0\"?>\r\n" +
                "<SOAP-ENV:Envelope " +
                "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<SOAP-ENV:Body>" +
                "<m:" + action + " xmlns:m=\"" + service + "\">");

        if (args != null && args.size() > 0) {
            Set<Map.Entry<String, String>> entrySet = args.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                soapBody.append("<" + entry.getKey() + ">" + entry.getValue() +
                        "</" + entry.getKey() + ">");
            }

        }

        soapBody.append("</m:" + action + ">");
        soapBody.append("</SOAP-ENV:Body></SOAP-ENV:Envelope>");

        URL postUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) postUrl.openConnection();

        conn.setRequestMethod("POST");
        conn.setConnectTimeout(DEFAULT_HTTP_RECEIVE_TIMEOUT);
        conn.setReadTimeout(DEFAULT_HTTP_RECEIVE_TIMEOUT);

        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "text/xml");
        conn.setRequestProperty("SOAPAction", soapAction);
        conn.setRequestProperty("Connection", "Close");

        conn.setDoOutput(true);
        conn.setDoInput(true);

        byte[] soapBodyBytes = soapBody.toString().getBytes();

        conn.setRequestProperty("Content-Length",
                String.valueOf(soapBodyBytes.length));

        conn.getOutputStream().write(soapBodyBytes);

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            if (parser == null) {
                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine).append(" ");

                System.out.println(sb.toString());
                in.close();
            } else {
                parser.parse(new InputSource(conn.getInputStream()));
            }
        } else {
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine).append(" ");

            System.out.println(sb.toString());
            in.close();
        }

        conn.disconnect();
    }
}
