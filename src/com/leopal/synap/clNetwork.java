package com.leopal.synap;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

/**
 * This class define network information for Synap application
 * and provide device network information  
 * 
 * @author sylvain
 */

public class clNetwork {

	/**
     * Port for announcement messages 
     */
    public final int ANNOUNCEMENT_PORT=3738;

	/**
     * Port for NTP messages 
     */
	public final int NTP_PORT=3737;
	
	/**
     * Port for audio packets messages 
     */
	public final int DATA_PORT = 3123;
	
	/**
	 * Broadcast address (InetAdress)
	 * 
	 * @param lContext : activity context
	 */
	public InetAddress getBroadcastAdressInet(Context lContext)
	{
		WifiManager wifiManager = (WifiManager)lContext.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifiManager.getDhcpInfo();

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    try {
			return InetAddress.getByAddress(quads);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Broadcast address (string)
	 * 
	 * @param lContext : activity context
	 */
	public String getBroadcastAdress(Context applicationContext) {
		return getBroadcastAdressInet(applicationContext).toString();
	}

	/**
	 * Device IP address (string)
	 */
	public String getIPAdress()
	{
	        try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
				{
				    NetworkInterface intf = en.nextElement();
				    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
				    {
				        InetAddress inetAddress = enumIpAddr.nextElement();
				        if (!inetAddress.isLoopbackAddress()) 
				        	return inetAddress.getHostAddress();
				    }
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
	    return null;
	}

	/**
	 * Multicast address
	 */
	public String getMulticastAdress() {
		return "224.0.0.1";
	}
	
}
