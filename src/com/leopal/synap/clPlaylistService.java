/**
 * Service used to manage current music played and current playlist
 * Generate events for listners (UPDATE, PLAY, PAUSE, RESUME)
 * Accessible services to play, pause a music and services to add, remove a playlist 
 */
package com.leopal.synap;

import java.util.ArrayList;
import java.util.List;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class clPlaylistService extends Service {

	/** Broadcast : update */
	public static final byte PLAYLIST_UPDATE = 0x01;
	/** Broadcast : play */
	public static final byte PLAYLIST_PLAY = 0x02;
	/** Broadcast : pause */
	public static final byte PLAYLIST_PAUSE = 0x03;
	/** Broadcast : resume */
	public static final byte PLAYLIST_RESUME = 0x04;
	/** Broadcast : stop */
	public static final byte PLAYLIST_STOP = 0x05;

	private List<clPlaylistServiceListener> listeners = null;

	public class LocalBinder extends Binder {
		clPlaylistService getService() {
			return clPlaylistService.this;
		}
	}

	@Override
	public void onCreate() {
		filenamePlaying = "";
		playPaused = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	private void fireDataChanged(byte action) {
		if (listeners != null) {
			for (clPlaylistServiceListener listener : listeners) {
				listener.dataChanged(action);
			}
		}
	}

	@Override
	public void onDestroy() {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

	public void addListener(clPlaylistServiceListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<clPlaylistServiceListener>();
		}
		listeners.add(listener);
		// fireDataChanged is required to update activity display once service
		// and activity connected
		fireDataChanged(PLAYLIST_UPDATE);
	}

	public void removeListener(clPlaylistServiceListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public String synou() {
		return "coucou";
	}

	/* Playlist of musics available for streamer */
	private List<String> playList;

	public void appendFilePlayList(String file) {
		if (playList == null) {
			playList = new ArrayList<String>();
		}
		playList.add(file);

		if (listeners != null) {
			fireDataChanged(PLAYLIST_UPDATE);
		}
	}

	public void removeFilePlayList(String file) {
		if (playList != null) {
			playList.remove(file);
		}
		if (listeners != null) {
			fireDataChanged(PLAYLIST_UPDATE);
		}
	}

	public boolean isInPlayList(String file) {
		boolean found = false;
		if (playList != null) {
			for (String nameInPlayList : playList) {
				if (nameInPlayList.compareTo(file) == 0) {
					found = true;
				}
			}
		}
		return (found);
	}

	public int findInPlayList(String file) {
		int found = -1;
		int i = 0;
		if (playList != null) {
			for (String nameInPlayList : playList) {
				if (nameInPlayList.compareTo(file) == 0) {
					found = i;
				}
				i++;
			}
		}
		return (found);
	}

	/* Music currently available for streamer */
	private String filenamePlaying;
	private int filePlayingIndex;
	private boolean playPaused;

	public void setPlay(String file) {
		if (playList == null) {
			playList = new ArrayList<String>();
		}
		Byte action;
		if (filenamePlaying == file) {
			if (playPaused) {
				action = PLAYLIST_RESUME;
				playPaused = false;
			} else {
				action = PLAYLIST_PAUSE;
				playPaused = false;
			}
		} else {
			filePlayingIndex = findInPlayList(file);
			if (filePlayingIndex == -1) {
				filePlayingIndex = playList.size();
				appendFilePlayList(file);
			}
			filenamePlaying = file;
			action = PLAYLIST_PLAY;
		}
		if (listeners != null) {
			fireDataChanged(action);
		}
	}

	public boolean isPlayed(String file) {
		return (filenamePlaying == file);
	}

	public String getPlayed() {
		return filenamePlaying;
	}
	
	public String getNext() {
		filePlayingIndex++;
		if (filePlayingIndex == playList.size()) {
			filePlayingIndex = 0;
		}
		return playList.get(filePlayingIndex);
	}
}
