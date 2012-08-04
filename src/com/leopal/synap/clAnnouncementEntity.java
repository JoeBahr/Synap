package com.leopal.synap;

/**
 * This class store the synap entity definition and information 
 * of each know entity
 *
 * @author sylvain
 */

public class clAnnouncementEntity {

	/**
     * Default time to live for an entity is REFRESH_CYCLE seconds 
     */
    private static final int REFRESH_CYCLE = 10;

    /**
     * Time to live (time before entity is considered no more connected) 
     */
    private int aliveCounter;
    
    /**
     * Synap entity definition 
     */
    private clSynapEntity synapEntity;
	
    /**
     * Retrieve Synap entity
     */
    public clSynapEntity getEntity() {
		return synapEntity;
	}
	
    /**
     * Update time to live
     * When entity information received
     */
    public void refresh() {
		aliveCounter = REFRESH_CYCLE;
	}
	
    /**
     * Remove one second of life
     */
    public void decrement() {
		if (aliveCounter > 0 ) {
			aliveCounter = aliveCounter - 1;
		}
	}
	
    /**
     * Time to live not expired ?
     */
    public boolean isAlive(){
		return (aliveCounter > 0);
	}
	
    /**
     * Time to live expired ?
     */
    public boolean isDead(){
		return (aliveCounter == 0);
	}
	
    /**
     * Compare a synap entity to this one
     * 
     * @param compare : synap entity to be compared to this one 
     */
    public boolean isEntity(clSynapEntity compare) {
		return (compare.getIpAdress().compareTo(synapEntity.getIpAdress()) == 0);
	}
	
    /**
     * Update current synap entity definition
     * 
     * @param in : synap entity information for update 
     */
    public boolean update(clSynapEntity in) {
		boolean updated = false;
		if (synapEntity.getName().compareTo(in.getName()) != 0) {
			synapEntity.setName(in.getName());
			updated = true;
		}
		if (synapEntity.isStreamer() != in.isStreamer()) {
			synapEntity.setStreamer(in.isStreamer());
			updated = true;
		}
		if (synapEntity.getStreamInfo().compareTo(in.getStreamInfo()) != 0) {
			synapEntity.setStreamInfo(in.getStreamInfo());
			updated = true;
		}
		return updated;
	}
	
    /**
     * Constructor
     * 
     * @param in : synap entity to be handled 
     */
    public clAnnouncementEntity (clSynapEntity in) {
		synapEntity = new clSynapEntity();
		synapEntity.setIpAdress(in.getIpAdress());
		synapEntity.setName(in.getName());
		synapEntity.setStreamer(in.isStreamer());
		synapEntity.setStreamInfo(in.getStreamInfo());
		refresh();
	}
}
