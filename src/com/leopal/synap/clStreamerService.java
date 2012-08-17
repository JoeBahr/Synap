/**
 * 
 */
package com.leopal.synap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class clStreamerService extends Service {

	private NotificationManager mNM;

	private int NOTIFICATION = R.string.local_service_started;

	private List<clStreamerServiceListener> listeners = null;

	private BufferedInputStream bufferedInputStream;
	
	/** Logging TAG*/
    private static final String TAG = "clStreamerService";

    /**
     * The source where content will be read
     */
    private clContentIn pv_contentIn;

    /**
     * The NTP Server
     */
    private clSyncServerThread pv_syncServer;

    /**
     * The IP Transport
     */
    private clTransport pv_transport;

    /**
     * IP/Multicast address for audio packet
     */
    private String pv_contentStringInet;

    /**
     * Thread for main loop of streaming
     */
    private Thread pv_threadMainLoop;

    /**
     * Bitrate Control for main stream
     */
    private clRateLimiting pv_streamRateLimiting;

    /**
     * Pause is required for current stream
     * Note : global pause used as suspend process is deprecated...
     */
    private boolean streamPaused;

    /**
     * Playlist Service connections
     */
    
	private boolean servicePlaylistIsBound;
	private clPlaylistService servicePlaylist;

	final clPlaylistServiceListener playlistServiceListener = new clPlaylistServiceListener() {
		public void dataChanged(Byte action) {
			switch (action) {
			case clPlaylistService.PLAYLIST_PLAY:
				stopStream();
				setInputStream(servicePlaylist.getPlayed());
				startStream();
			break;
			case clPlaylistService.PLAYLIST_PAUSE:
				pauseStream();
			break;
			case clPlaylistService.PLAYLIST_RESUME:
				resumeStream();
			break;
			case clPlaylistService.PLAYLIST_STOP:
				stopStream();
			break;
			case clPlaylistService.PLAYLIST_UPDATE:
				/* nothing to do */
			break;
			}
		}
	};

	private ServiceConnection servicePlaylistConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			servicePlaylist = ((clPlaylistService.LocalBinder) service)
					.getService();
			servicePlaylist.addListener(playlistServiceListener);
		}

		public void onServiceDisconnected(ComponentName className) {
			servicePlaylist = null;
		}
	};

	void doBindService() {
		bindService(
				new Intent(clStreamerService.this, clPlaylistService.class),
				servicePlaylistConnection, Context.BIND_AUTO_CREATE);
		servicePlaylistIsBound = true;
	}

	void doUnbindService() {
		if (servicePlaylistIsBound) {
			getApplicationContext().unbindService(servicePlaylistConnection);
			servicePlaylistIsBound = false;
		}
	}
	
	public class LocalBinder extends Binder {
		clStreamerService getService() {
			return clStreamerService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        pv_syncServer = new clSyncServerThread();
        pv_transport = new clTransport();
        pv_syncServer.startServer();
        doBindService();
        setContentInet("224.0.0.1");
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
		doUnbindService();
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

	public void setContentInet(String _Inet) {
        this.pv_contentStringInet = _Inet;
    }

    public String getContentInet() {
        return pv_contentStringInet;
    }

    private void setInputStream(String file) {
		// TODO : Replace f initialisation as soon as streamer manage MP3.
    	//File f = new File(file);
		File f = new File(Environment.getExternalStorageDirectory() 
                + File.separator + "Music" + File.separator + "rhcp_atw.wav");
        InputStream lInputStream = null;
		try {
			lInputStream = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    
        bufferedInputStream = new BufferedInputStream(lInputStream);
        
        clContentIn contentIn = new clContentInWaveFile(30);
        try {
            contentIn.openAudioBufferedInputStream(bufferedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pv_contentIn = contentIn;
	}
	
    // TODO : Split this code to be able to test the streaming function independantly
    private void startStream() {
        pv_streamRateLimiting = new clRateLimiting();

        streamPaused = false;
        if (pv_transport.commLinkInit(pv_contentStringInet)==clTransport.COMM_LINK_OPEN) {
            if (!pv_contentIn.isEndOfContent()) {
                pv_threadMainLoop = new Thread(new Runnable() {
                    public void run() {
                        //Number of Sample read
                        int sampleCount;
                        //End request
                        boolean endRequested = false;
                        //End of file
                        boolean eOf = false;
                        //Prepare the buffer
                        byte[] buf = pv_contentIn.getAudioBlockBuffer();
                        int blockLength = pv_contentIn.getBlockLengthMs();

                        //Determine bitrate stream
                        pv_streamRateLimiting.setMaximumBitrate(pv_contentIn.getPcmFormat().getSampleRate());

                        //int sampleCount = pv_contentIn.getPcmFormat().
                        //Prepare playout in xx sec
                        long timeStamp = pv_syncServer.getTime()+3000; //TODO Transform this value as a parameter

                        //First Send
                        sampleCount = pv_contentIn.readNextAudioBlock(buf);
                        pv_streamRateLimiting.setDataSent(sampleCount);
                        pv_transport.sSendData(buf, timeStamp, true, sampleCount);
                        timeStamp+=blockLength;
                        //second Send to get advance
                        //pv_contentIn.readNextAudioBlock(buf);
                        //pv_transport.sSendData(buf, timeStamp, false);
                        //timeStamp += blockLength;

                        //Loop to read content while there is data
                        eOf = pv_contentIn.isEndOfContent();
                        while( (!eOf)
                                && (!endRequested) ) {
                            if (!streamPaused) {
	                        	sampleCount = pv_contentIn.readNextAudioBlock(buf);

                                if (Thread.interrupted()) {
                                    endRequested = true;
                                } else {
                                    if ( !pv_streamRateLimiting.isReadyToSend(sampleCount) )
                                        endRequested = true;
                                    else {
                                        pv_streamRateLimiting.setDataSent(sampleCount);
                                        pv_transport.sSendData(buf, timeStamp, false, sampleCount);
                                        timeStamp+=blockLength;
                                    }
                                }

                            }

                            //Log.i(TAG, "Send Samples " + sampleCount);
                            eOf = pv_contentIn.isEndOfContent();
                        }
                        if (eOf) {
                        	//Request next file to be played
                            servicePlaylist.moveNext();
                        }
                    }
                });
                pv_threadMainLoop.start();
            }
            else {
            	//Request next file to be played
                servicePlaylist.moveNext();
            }
        }
    }

    private void stopStream() {
        if (pv_threadMainLoop !=null) {
            pv_threadMainLoop.interrupt();
            try {
                pv_threadMainLoop.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        this.pv_transport.closeCommLink();
    }

    private void pauseStream() {
        //Note : method suspend is deprecated
    	streamPaused = true;
    }

    private void resumeStream() {
        //Note : method suspend is deprecated
    	streamPaused = false;
    }
}
