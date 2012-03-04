package com.leopal.synap;

/**
 * Class for streamer / client descriptors 
 * to be provided by discovery service
 * for activities usage
 *
 */

public class clSynapEntity {
	private String Name;
	private Boolean isStreamer;
	private String StreamInfo;
	private String IpAdress;
	public void setName(String name) {
		Name = name;
	}
	public String getName() {
		return Name;
	}
	public void setStreamer(Boolean isStreamer) {
		this.isStreamer = isStreamer;
	}
	public Boolean isStreamer() {
		return isStreamer;
	}
	public void setStreamInfo(String streamInfo) {
		StreamInfo = streamInfo;
	}
	public String getStreamInfo() {
		return StreamInfo;
	}
	public void setIpAdress(String ipAdress) {
		IpAdress = ipAdress;
	}
	public String getIpAdress() {
		return IpAdress;
	}
}
