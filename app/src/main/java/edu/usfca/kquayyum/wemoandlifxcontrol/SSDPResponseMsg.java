package edu.usfca.kquayyum.wemoandlifxcontrol;

import edu.usfca.kquayyum.wemoandlifxcontrol.SSDPConstants;

/**
 * @author Victor Su
 */
public class SSDPResponseMsg {
    private String ST;       /* service type */
    private String USN;      /* unique service name */
    private String location; /* location */

    public SSDPResponseMsg(String ST, String USN, String location) {
        this.ST = ST;
        this.USN = USN;
        this.location = location;
    }

    public String getST() {
        return ST;
    }

    public void setST(String ST) {
        this.ST = ST;
    }

    public String getUSN() {
        return USN;
    }

    public void setUSN(String USN) {
        this.USN = USN;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();

        content.append(SSDPConstants.SL_OK).append(SSDPConstants.NEWLINE);
        content.append(SSDPConstants.HEADER_ST + ": " + ST).append(SSDPConstants.NEWLINE);
        content.append(SSDPConstants.HEADER_USN + ": " + USN).append(SSDPConstants.NEWLINE);
        content.append(SSDPConstants.HEADER_LOCATION + ": " + location).append(SSDPConstants.NEWLINE);
        content.append(SSDPConstants.NEWLINE);

        return content.toString();
    }
}