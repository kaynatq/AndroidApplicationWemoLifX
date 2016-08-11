package edu.usfca.kquayyum.wemoandlifxcontrol;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * A WeMo Bulb class with device id, device index, friendly name and the bridge it belongs to
 * Attribution: Concept taken from python API for WeMo (https://github.com/iancmcc/ouimeaux)
 */
public class WeMoLightDevice {
    private WeMoBridgeDevice rootDevice;
    private String deviceIndex = "";
    private String deviceId = "";
    private String friendlyName = "";

    public String getProductName() {
        return productName;
    }

    public String getDeviceId() {
        return deviceId;
    }
    private String productName = "";

    public static final String productNameType = "Lighting";


    public enum ParseState {
        NONE,
        IN_DEVICE_INFO,
    }

    public WeMoLightDevice(WeMoBridgeDevice rootDevice) {
        this.rootDevice = rootDevice;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[").append(deviceIndex).append("] ");
        sb.append("Name: ").append(friendlyName).append(" ");
        sb.append("Id: ").append(deviceId).append("  ");
        sb.append("\n");

        return sb.toString();
    }



    public static class LightDeviceHandler extends DefaultHandler {
        private WeMoBridgeDevice bridgeDevice = null;
        private List<WeMoLightDevice> lights = null;

        private WeMoLightDevice currentLight = null;
        private String currentElement = null;
        private ParseState parseState = ParseState.NONE;

        public LightDeviceHandler(WeMoBridgeDevice bridgeDevice, List<WeMoLightDevice> lights) {
            this.bridgeDevice = bridgeDevice;
            this.lights = lights;
        }

        /**
         * XML tag marking a particular device information.
         */
        private static final String DEVICE_INFO_TAG = "DeviceInfo";

        /**
         * Override handling of start of an element.
         * Attribution: org.xml.sax.ContentHandler#startElement
         */
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            currentElement = localName;
            if (currentElement.compareToIgnoreCase(DEVICE_INFO_TAG) == 0) {
                currentLight = new WeMoLightDevice(this.bridgeDevice);
                parseState = ParseState.IN_DEVICE_INFO;
            }
        }

        /**
         * Override handling of end of an element.
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (localName.compareToIgnoreCase(DEVICE_INFO_TAG) == 0) {
                lights.add(currentLight);
                currentLight = null;
                parseState = ParseState.NONE;
            }
            currentElement = null;
        }

        /**
         * Receive notification of character data inside an element.
         * Stores the characters as value, using {@link #currentElement} as a key
         * Attribution: org.xml.sax.ContentHandler#characters
         */

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (parseState == ParseState.IN_DEVICE_INFO
                    && currentLight != null) {
                String value = new String(ch,start,length);

                if (currentElement.compareToIgnoreCase("DeviceIndex") == 0) {
                    currentLight.deviceIndex = value;
                } else if (currentElement.compareToIgnoreCase("DeviceID") == 0) {
                    currentLight.deviceId = value;
                } else if (currentElement.compareToIgnoreCase("productName") == 0) {
                    currentLight.productName = value;
                } else if (currentElement.compareToIgnoreCase("friendlyName") == 0) {
                    currentLight.friendlyName = value;
                }
            }
        }
    }

    public static List<WeMoLightDevice> parseLightsFromDeviceListString(
            WeMoBridgeDevice bridge,
            XMLReader parser,
            String deviceListString) throws IOException, SAXException {
        List<WeMoLightDevice> lights = new ArrayList<>();
        LightDeviceHandler handler = new LightDeviceHandler(bridge, lights);

        parser.setContentHandler(handler);
        StringReader sr = new StringReader(deviceListString);
        parser.parse(new InputSource(sr));

        return lights;
    }

    private static final String DEVICE_XML_FORMAT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<DeviceStatus>" +
          "<IsGroupAction>NO</IsGroupAction>" +
          "<DeviceID available=\"YES\">%s</DeviceID>" +
          "<CapabilityID>10006,10008</CapabilityID>" +
          "<CapabilityValue>%s,%s</CapabilityValue>" +
        "</DeviceStatus>";

    public void changeLightStatus(String state, String dim) {
        Map<String, String> args = new HashMap<>();

        String newStatus = StringEscapeUtils.escapeXml10(
                String.format(DEVICE_XML_FORMAT, this.deviceId, state, dim));
        args.put("DeviceStatusList", newStatus);
        try {

            /**
             * Constructing the xml for device on/off
             */
            WeMoUpnpUtils.simpleUPnPCommand(
                    null,
                    this.rootDevice.getBridgeControlUrl(),
                    this.rootDevice.getBridgeServiceType(),
                    "SetDeviceStatus",
                    args);
        } catch (IOException|SAXException e) {
            e.printStackTrace();
        }
    }

    public void turnOn() {
        changeLightStatus("1", "255");
    }

    public void turnOff() {
        changeLightStatus("0", "0");
    }
}
