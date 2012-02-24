/**
 * 
 */
package com.leopal.synap;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class clStreamerService extends Service {

	private NotificationManager mNM;

	private int NOTIFICATION = R.string.local_service_started;

	private List<clStreamerServiceListener> listeners = null;

	private clStreamer synapStreamer;
	private InputStream inputStream;
	private BufferedInputStream bufferedInputStream;
	private String mServerIP;
	
	public class LocalBinder extends Binder {
		clStreamerService getService() {
			return clStreamerService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		return START_STICKY;
	}

	private void fireDataChanged(byte action) {
		if (listeners != null) {
			for (clStreamerServiceListener listener : listeners) {
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

	public void addListener(clStreamerServiceListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<clStreamerServiceListener>();
		}
		listeners.add(listener);
		fireDataChanged((byte) 0);
	}

	public void removeListener(clStreamerServiceListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public String synou() {
		return "coucou";
	}

}
