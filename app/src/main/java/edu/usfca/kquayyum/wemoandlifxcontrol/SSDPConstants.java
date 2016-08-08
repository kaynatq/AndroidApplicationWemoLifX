package edu.usfca.kquayyum.wemoandlifxcontrol;


public class SSDPConstants {
    /* New line definition */
    public static final String NEWLINE = "\r\n";

    public static final String ADDRESS = "239.255.255.250";
    public static final int PORT = 1900;

    /* Definitions of headers */
    public static final String HEADER_HOST = "HOST";
    public static final String HEADER_LOCATION = "LOCATION";
    public static final String HEADER_MAN = "MAN";
    public static final String HEADER_MX = "MX";
    public static final String HEADER_NT = "NT";
    public static final String HEADER_NTS = "NTS";
    public static final String HEADER_ST = "ST";
    public static final String HEADER_USN = "USN";

    /* Host header */
    public static final String HOST = HEADER_HOST + ": " + SSDPConstants.ADDRESS + ":" + SSDPConstants.PORT;

    /* Definitions of start line */
    public static final String SL_NOTIFY = "NOTIFY * HTTP/1.1";
    public static final String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
    public static final String SL_OK = "HTTP/1.1 200 OK";

    /* Definitions of search targets */
    public static final String ST_RootDevice = "ST:rootdevice";
    public static final String ST_ContentDirectory = "ST:urn:schemas-upnp-org:service:ContentDirectory:1";

    /* Definitions of notification sub type */
    public static final String SSDP_ALIVE = "ssdp:alive";
    public static final String SSDP_ALL = "ssdp:all";
    public static final String SSDP_BYE = "ssdp:byebye";
    public static final String SSDP_DISCOVER = "ssdp:discover";
    public static final String SSDP_UPDATE = "ssdp:update";
}
