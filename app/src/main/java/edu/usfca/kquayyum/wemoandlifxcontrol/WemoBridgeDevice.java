package edu.usfca.kquayyum.wemoandlifxcontrol;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

/**
 * Created by kaynat on 8/7/16.
 */
public class WemoBridgeDevice {
    /**
     * The following are basic information of the bridge device
     * that is parsed from the MSearch discovery reply.
     * Attribution: Concept taken from python API for WeMo (https://github.com/iancmcc/ouimeaux)
     */
    private String location = "";
    private String st = "";
    private String usn = "";  // UniversalSerialNumber UDN::upnp:rootdevice

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUsn() {
        return usn;
    }

    /**
     * Parses the reply from UPnP devices
     *
     * @param reply the raw bytes received as a reply
     * @return the representation of a WemoBridgeDevice. Note that it does not have device details.
     */
    public static WemoBridgeDevice parseMSearchReply(byte[] reply) {
        WemoBridgeDevice device = new WemoBridgeDevice();

        String replyString = new String(reply);
        StringTokenizer st = new StringTokenizer(replyString, "\n");

        while (st.hasMoreTokens()) {
            String line = st.nextToken().trim();

            if (line.isEmpty())
                continue;

            if (line.startsWith("HTTP/1.") || line.startsWith("NOTIFY *"))
                continue;

            String key = line.substring(0, line.indexOf(':'));
            String value = line.length() > key.length() + 1 ? line.substring(key.length() + 1) : null;

            key = key.trim();
            if (value != null) {
                value = value.trim();
            }

            if (key.compareToIgnoreCase("location") == 0) {
                device.location = value;

            } else if (key.compareToIgnoreCase("st") == 0) {    // Search Target
                device.st = value;
            } else if (key.compareToIgnoreCase("USN") == 0) {
                device.usn = value;
            }
        }

        return device;
    }

    private enum ParseState {
        NONE,
        IN_DEVICE,
        IN_SERVICE
    }

    private static String DEVICE_TAG = "device";
    private static String SERVICE_TAG = "service";

    private class WemoBridgeHandler extends DefaultHandler {
        private WemoBridgeDevice device;

        public WemoBridgeHandler(WemoBridgeDevice device) {
            super();
            this.device = device;
        }

        /** state variables */
        private String currentElement;
        private ParseState state = ParseState.NONE;

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            currentElement = localName;

            if (currentElement.compareToIgnoreCase(DEVICE_TAG) == 0) {
                state = ParseState.IN_DEVICE;
            }

            if (currentElement.compareToIgnoreCase(SERVICE_TAG) == 0) {
                state = ParseState.IN_SERVICE;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            currentElement = "";
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (state == ParseState.IN_DEVICE) {
                String value = new String(ch, start, length);
                value = value.trim();

                if ("friendlyName".compareToIgnoreCase(currentElement) == 0) {
                    device.friendlyName = value;
                } else if ("serialNumber".compareToIgnoreCase(currentElement) == 0) {
                    device.serialNumber = value;
                } else if ("UDN".compareToIgnoreCase(currentElement) == 0) {
                    device.udn = value;
                } else if ("macAddress".compareToIgnoreCase(currentElement) == 0) {
                    device.mac = value;
                }
            } else if (state == ParseState.IN_SERVICE) {

            }
        }
    }

    private String baseUrl = "";
    private String friendlyName = "";
    private String serialNumber = "";
    private String udn = "";  // Unique Device Name, Generally "uuid:Bridge-1_0-$serialNumber$"
    private String mac = "";

    private String bridgeServiceType = "urn:Belkin:service:bridge:1";
    private String bridgeServiceId = "urn:Belkin:serviceId:bridge1";
    private String bridgeControlUrl = "";
    private String bridgeSCPDUrl = "";

    public String getBridgeControlUrl() {
        return bridgeControlUrl;
    }

    public String getBridgeServiceType() {
        return bridgeServiceType;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("location: ").append(this.location).append("\n");
        s.append("st: ").append(this.st).append("\n");
        s.append("USN: ").append(this.usn).append("\n");
        s.append("friendlyName: ").append(this.friendlyName).append("\n");
        s.append("serialNumber: ").append(this.serialNumber).append("\n");
        s.append("UDN: ").append(this.udn).append("\n");
        s.append("macAddress: ").append(this.mac).append("\n");

        return s.toString();
    }

    /**
     * Connects to the WemoBridgeDevice {@link #location} and parses the response
     * using a {@link WemoBridgeHandler} to populate the fields of this
     * class
     *
     * @throws SAXException if an error occurs while parsing the request
     * @throws IOException  on communication errors
     */
    public void loadBridgeInfo() throws SAXException, IOException, ParserConfigurationException {
        URL url = new URL(location);
        this.baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
        System.out.println(this.baseUrl);

        SAXParser saxParser = WemoUpnpUtils.saxFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        WemoBridgeHandler handler = new WemoBridgeHandler(this);
        xmlReader.setContentHandler(handler);

        xmlReader.parse(new InputSource(location));

        bridgeServiceType = "urn:Belkin:service:bridge:1";
        bridgeServiceId = "urn:Belkin:serviceId:bridge1";
        bridgeControlUrl = this.baseUrl + "/upnp/control/bridge1";
        bridgeSCPDUrl = this.baseUrl + "/bridgeservice.xml";

        System.out.println(this.toString());
    }

    public static class DeviceListsHandler extends DefaultHandler {
        private StringBuilder deviceListString = new StringBuilder();

        public String getDeviceListString() {
            return deviceListString.toString();
        }

        /**
         * The last read element
         */
        private String currentElement = null;

        /**
         * Override handling of start of an element.
         *
         * @see org.xml.sax.ContentHandler#startElement
         */
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            currentElement = localName;
        }

        /**
         * Override handling of end of an element.
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            currentElement = null;
        }

        /**
         * Receive notification of character data inside an element.
         *
         * Stores the characters as value, using {@link #currentElement} as a key
         *
         * @see org.xml.sax.ContentHandler#characters
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (currentElement != null
                    && currentElement.compareToIgnoreCase("DeviceLists") == 0) {
                deviceListString.append(new String(ch,start,length));
            }
        }
    }


    public List<WemoLightDevice> getLights() throws IOException, SAXException, ParserConfigurationException {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("DevUDN", this.udn);
        arguments.put("ReqListType", "PAIRED_LIST");

        SAXParser saxParser = WemoUpnpUtils.saxFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        DeviceListsHandler dlHandler = new DeviceListsHandler();
        xmlReader.setContentHandler(dlHandler);

        WemoUpnpUtils.simpleUPnPCommand(
                xmlReader,
                this.bridgeControlUrl,
                this.bridgeServiceType,
                "GetEndDevices",
                arguments);

        return WemoLightDevice.parseLightsFromDeviceListString(
                this,
                xmlReader,
                dlHandler.getDeviceListString());
    }
}
