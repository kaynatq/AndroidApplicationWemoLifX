# Android Application for WeMo and LifX Light Bulbs

This is a simple client application that can discover smart lights within our local network. Once the application is launched, it starts discovering all lights. Upon discovery, it displays the list of lights. From the list, we can perform several actions like power on/off, or make other changes based on what type of light we have discovered. For example, we can set different colors on the light if it is lifX. If there is multiple WeMo bulbs which belong to different WeMo switch(Bridge), we can discover and control each of them separately. 

## Getting Started

If the user wants to discover and control smart lights using this app, at first they will have to on-board the bulbs with the existing Android apps for WeMo and LifX. Here is how to do this:

* When the bulbs and WeMo switch are plugged in, they broadcast a wifi network. Find that network in Settings -> Wi-Fi and choose the Bulb network(WeMo/LifX).  
* Once we are connected to that wifi, we can try to add the bulb or device to the app. A window is shown with the available networks, choose your network and connect the bulb with it. The bulbs are now on-board. Details can be found in [LifX] (https://support.lifx.com/hc/en-us/categories/200238164-Getting-Started) and [WeMo] (http://www.belkin.com/us/support-article?articleNum=116173) sites.


## How it works?
* When the user clicks the "Find Lights" button, the application starts searching the network for available devices. If no device is found, an Intent is launched showing "No device found!". Otherwise, a list of available light bulbs are shown.

* It may happen that all the devices are not discovered in the first attempt. Therefore, we will have to press the back button and try again for discovery.

* If the connected lights are found on the network, those are displayed on the screen. From the list, we can choose what we want to do.


## Detailed Workflow for WeMo
* Discovery for WeMo light starts with the discovery for UPnP devices. The `getLocalInetAddresses()` method finds all the local IP that we have in our network. Then a multicast message is sent to the designated IP and PORT for M-Search message in a separate thread `SendDiscoveryThread`.
* When we receive a response from network, `parseMSearchReply` method is used to parse the XML data and get the device info. If it is a WeMo bridge, we add it to our Arraylist. We get all the "Lighting" product using the `parseLightsFromDeviceListString` method.
* Method `simpleUPnPCommand` is used to build a SOAP message and send an HTTP POST request. From this we change the state of a bulb. This is the general XML format for changing bulb status:
```
    <?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<DeviceStatus>" +
          "<IsGroupAction>NO</IsGroupAction>" +
          "<DeviceID available=\"YES\">%s</DeviceID>" +
          "<CapabilityID>10006,10008</CapabilityID>" +
          "<CapabilityValue>%s,%s</CapabilityValue>" +
        "</DeviceStatus>
```
We just put the device ID and CapabilityValue to this XML.

## Protocol for LifX

Here we have used the LifX LAN protocol. Details for this protocol can be found [here] (https://lan.developer.lifx.com/docs/introduction). We have specific header description and payload message for every request. 

* When a broadcast is sent to the LIFX PORT 56700, if a LifX bulb is there, it responds with a state service message. Then we store the IP of that bulb in our system, and control it using other LAN commands like `SetColor`, `SetPowerOn`, `SetPowerOff` etc.
* The frame header include size, origin, tagged bit, addressable bit, protocol number and source. For example, our frame header to Power Off the light is: `28 00 00 34 7B`.
* The frame address include target, ack_required bit, res_required bit and a sequence number for a unique device.
* Finally we built the payload message which is only required when we are setting the color. This includes the HSBK information of the power level of the bulb. We have used a color picker library for android, `com.pes.materialcolorpicker:library:1.0.+`, which takes the color as RGB format. We have a method `RGBtoHSV(double r, double g, double b)` that is used for conversion.



