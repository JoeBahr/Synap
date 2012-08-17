package com.leopal.synap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class clAnnouncementService extends Service {

	private NotificationManager mNM;

	private int NOTIFICATION = R.string.local_service_started;

	private ArrayList<clAnnouncementServiceListener> listeners = null;

	private ArrayList<clSynapEntity> synapEntityList = new ArrayList<clSynapEntity>();
	
	public class LocalBinder extends Binder {
		clAnnouncementService getService() {
			return clAnnouncementService.this;
		}
	}
	
	private Timer timer = new Timer();
	private boolean first = true;
	private int i = 48;
	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		timer.scheduleAtFixedRate(
			      new TimerTask() {
			        public void run() {
		            	clSynapEntity object = new clSynapEntity();
		            	i++;
		            	String s = "192.168.0.";
		            	s = s + i;
		        		object.setIpAdress(s);
		        		object.setName("Nicolas");
		        		object.setStreamInfo("Nicolas music "+i);
		        		object.setStreamer(true);
		        		if (first) {
		        			first = false;
		        		} else {
		        			synapEntityList.add(object);
		        		}
		        		fireDataChanged((byte) 0);
			        }
			      },
			      0,
			      10000);
		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		return START_STICKY;
	}

	private void fireDataChanged(byte action) {
		if (listeners != null) {
			for (clAnnouncementServiceListener listener : listeners) {
				listener.dataChanged(action);
			}
		}
	}

	@Override
	public void onDestroy() {
		mNM.cancel(NOTIFICATION);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = getText(R.string.local_service_started);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.stat_sample,
				text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MusicActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,
				getText(R.string.local_service_label), text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
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
	
	public ArrayList<clSynapEntity> getList() {
		return synapEntityList;
	}
	
	public clSynapEntity getListItem(int position) {
		return synapEntityList.get(position);
	}
	
	public int getSize() {
		return synapEntityList.size();
	}

}
