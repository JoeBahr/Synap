package com.leopal.synap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This class is a service 
 * 
 * This service :
 * - declare the running Synap application over the network
 * - provide the running Synap application with the list of known Synap applications over the network
 *
 * @author sylvain
 */
public class clAnnouncementService extends Service {

	/**
     * Running synap entity definition 
     */
    private clSynapEntity MySynapEntity;
	
	/**
     * list of known Synap entities
     */
	private ArrayList<clAnnouncementEntity> announcementEntityList = new ArrayList<clAnnouncementEntity>();
	
    /**
     * Network definition to retrieve IPs 
     */
    private clNetwork networkInfo = new clNetwork();
	
	/**
	 * Initialization of service with 
	 * 1 timer for alive status of know synap entities
	 * and 2 services for announcement and reception of announcements
	 */
	@Override
	public void onCreate() {
		Timer timer = new Timer();
		MySynapEntity = new clSynapEntity();
		MySynapEntity.setName("");
		MySynapEntity.setStreamInfo("Music " + networkInfo.getIPAdress());
		MySynapEntity.setIpAdress(networkInfo.getIPAdress());
		MySynapEntity.setStreamer(false);
		/* timer for alive status of know synap entities */
		timer.scheduleAtFixedRate(
			      new TimerTask() {
			        public void run() {
			        	boolean inform = false;
						for (clAnnouncementEntity announcementEntity : announcementEntityList) {
							announcementEntity.decrement();
							if (announcementEntity.isDead()){
								inform = true;
								announcementEntityList.remove(announcementEntity);
							}
						}
						if (inform) {
							fireDataChanged((byte) 0);
						}
			        }
			      },
			      0,
			      10000);
		/* process for announcement */
		new Thread(new Runnable() {
        	public void run() {
        		Boolean rtn;
        		clNetwork networkInfo = new clNetwork();
        		DatagramSocket s = null;
				try {
					s = new DatagramSocket();
					rtn = true;
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					rtn = false;
				}
                while(rtn) {
                	try {
    					ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                    ObjectOutputStream oos = new ObjectOutputStream(baos);
	                    oos.writeObject(MySynapEntity);
	                    oos.flush();
	                    byte[] buf= baos.toByteArray();
	                    DatagramPacket psend = new DatagramPacket(buf,
	                    		buf.length, 
	                    		networkInfo.getBroadcastAdressInet(getApplicationContext()),
	                    		networkInfo.ANNOUNCEMENT_PORT);
                    	s.send(psend);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        }).start();
		/* process for reception of announcements */
		new Thread(new Runnable() {
        	public void run() {
        		Boolean rtn;
        		clSynapEntity synapEntity;
        		DatagramSocket s = null;
				try {
					s = new DatagramSocket(networkInfo.ANNOUNCEMENT_PORT);
					rtn = true;
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					rtn = false;
				}

                while (rtn) {
                	try {
                		byte[] bufrcv = new byte[500];
                        DatagramPacket preceive = new DatagramPacket(bufrcv, bufrcv.length);
                        s.receive(preceive);
	                	ByteArrayInputStream baos = new ByteArrayInputStream(bufrcv);
	                    ObjectInputStream oos = new ObjectInputStream(baos);
                		synapEntity = (clSynapEntity)oos.readObject();
                		handleReceivedSynapEntity(synapEntity);
					} catch (OptionalDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
        	}
        }).start();
	}

	/**
     * Handle reception of announcement :
     * - check if synap entity known
     * - if yes, refresh and update synap entity
     * - if no, add it in list
     */
    private void handleReceivedSynapEntity(clSynapEntity rcv) {
		Boolean found = false;
		clAnnouncementEntity newEntity = new clAnnouncementEntity(rcv);
		if (listeners != null) {
			for (clAnnouncementEntity announcementEntity : announcementEntityList) {
				if (announcementEntity.isEntity(rcv)) {
					found = true;
					announcementEntity.refresh();
					if (announcementEntity.update(rcv)) {
						fireDataChanged((byte) 0);
					}
				}
			}
		}
		if (found == false) {
			announcementEntityList.add(newEntity);
			fireDataChanged((byte) 0);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
	
	}

	/**
	 *  service dedicated functions and objects
	 */
	private ArrayList<clAnnouncementServiceListener> listeners = null;

	private void fireDataChanged(byte action) {
		if (listeners != null) {
			for (clAnnouncementServiceListener listener : listeners) {
				listener.dataChanged(action);
			}
		}
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		clAnnouncementService getService() {
			return clAnnouncementService.this;
		}
	}
	
	public void addListener(clAnnouncementServiceListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<clAnnouncementServiceListener>();
		}
		listeners.add(listener);
		fireDataChanged((byte) 0);
	}

	public void removeListener(clAnnouncementServiceListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public String synou() {
		return "coucou";
	}
	
	/**
	 * service for list of synap entities display : get list of entities
	 */
	public ArrayList<clSynapEntity> getList() {
		ArrayList<clSynapEntity> synapList = new ArrayList<clSynapEntity>();
		for (clAnnouncementEntity announcementEntity : announcementEntityList) {
			synapList.add(announcementEntity.getEntity());
		}
		return synapList;
	}
	
	/**
	 * service for list of synap entities display : retrieve entity
	 * 
	 * @param position : index of entity to be retrieved
	 */
	public clSynapEntity getListItem(int position) {
		return announcementEntityList.get(position).getEntity();
	}
	
	/**
	 * service for list of synap entities display : get number of known entities
	 */
	public int getSize() {
		return announcementEntityList.size();
	}
	
	/**
	 * service for list of synap entities display : get list of streamer entities
	 */
	public ArrayList<clSynapEntity> getStreamerList() {
		ArrayList<clSynapEntity> synapList = new ArrayList<clSynapEntity>();
		for (clAnnouncementEntity announcementEntity : announcementEntityList) {
			if (announcementEntity.getEntity().isStreamer()) {
				synapList.add(announcementEntity.getEntity());
			}
		}
		return synapList;
	}
	
	/**
	 * service for list of synap entities display : retrieve streamer entity
	 * 
	 * @param position : index of streamer entity to be retrieved
	 */
	public clSynapEntity getStreamerListItem(int position) {
		int nbStreamer = 0;
		for (clAnnouncementEntity announcementEntity : announcementEntityList) {
			if (announcementEntity.getEntity().isStreamer()) {
				if (nbStreamer == position) {
					return announcementEntity.getEntity();
				}
				nbStreamer = nbStreamer +1;
			}
		}
		return null;
	}
	
	/**
	 * service for list of synap entities display : get number of known streamer entities
	 */
	public int getStreamerSize() {
		int nbStreamer = 0;
		for (clAnnouncementEntity announcementEntity : announcementEntityList) {
			if (announcementEntity.getEntity().isStreamer()) {
				nbStreamer = nbStreamer +1;
			}
		}
		return nbStreamer;
	}
	
	/**
	 * service for device announcement : define current entity as streamer
	 */
	public void setStreamer(){
		MySynapEntity.setStreamer(true);
	}

	/**
	 * service for device announcement : define current entity as not streamer
	 */
	public void resetStreamer(){
		MySynapEntity.setStreamer(false);
	}
	
	/**
	 * service for device announcement : define stream information
	 * 
	 * @param streamInfo : stream information to be broadcasted 
     */
	public void setStreamInfo(String streamInfo){
		MySynapEntity.setStreamInfo(streamInfo);
	}

}
