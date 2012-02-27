/**
 * Tab activity available in streamer mode to select music from music library
 */
package com.leopal.synap;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicActivity extends ListActivity {

	// Components for display views
	MusicAdapter musicAdapter;
	TextView musiclist;
	
	// List of musics
	Cursor musiccursor;
	int countMusic;
	int musicDisplayedName_column_index;
	int musicFileName_column_index;
	String musicPath;
	ListView musicListView;
	
	private class musicAdapterViewHolder {
		public ImageView image;
		public TextView texte;
		public ImageView play;
		public ImageView append;
	}

	// playlist Service connections
	private boolean servicePlaylistIsBound;
	private clPlaylistService servicePlaylist;

	final clPlaylistServiceListener playlistServiceListener = new clPlaylistServiceListener() {
		public void dataChanged(Byte action) {
			MusicActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					resetPlayImages(getListView());
					resetPlayList(getListView());
				}
			});
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

	// Streamer Service connections
	private boolean serviceStreamerIsBound;
	private clStreamerService serviceStreamer;

	final clStreamerServiceListener streamerServiceListener = new clStreamerServiceListener() {
		public void dataChanged(Byte action) {
			MusicActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					resetPlayImages(getListView());
					resetPlayList(getListView());
				}
			});
		}
	};

	private ServiceConnection serviceStreamerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			serviceStreamer = ((clStreamerService.LocalBinder) service)
					.getService();
			serviceStreamer.addListener(streamerServiceListener);
		}

		public void onServiceDisconnected(ComponentName className) {
			serviceStreamer = null;
		}
	};
	
	void doBindService() {
		// bindService(...) is not directly available in childs of TabHost.
		// requires usage of getApplicationContext()
		getApplicationContext().bindService(
				new Intent(MusicActivity.this, clPlaylistService.class),
				servicePlaylistConnection, Context.BIND_AUTO_CREATE);
		getApplicationContext().bindService(
				new Intent(MusicActivity.this, clStreamerService.class),
				serviceStreamerConnection, Context.BIND_AUTO_CREATE);
		servicePlaylistIsBound = true;
		serviceStreamerIsBound = true;
	}

	void doUnbindService() {
		if (servicePlaylistIsBound) {
			getApplicationContext().unbindService(servicePlaylistConnection);
			servicePlaylistIsBound = false;
		}
		if (serviceStreamerIsBound) {
			getApplicationContext().unbindService(serviceStreamerConnection);
			serviceStreamerIsBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doBindService();
		init_phone_music_grid();
	}

	private void init_phone_music_grid() {
		System.gc();
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE };
		musicPath = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.getPath();
		musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);
		countMusic = musiccursor.getCount();
		musicDisplayedName_column_index = musiccursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
		musicFileName_column_index = musiccursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		musicAdapter = new MusicAdapter();
		setListAdapter(musicAdapter);
		if (serviceStreamer != null) {
			Toast.makeText(this, serviceStreamer.synou(), Toast.LENGTH_SHORT).show();
		}
	}

	public class MusicAdapter extends BaseAdapter {

		public int getCount() {
			return countMusic;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			System.gc();
			musicAdapterViewHolder musicHolder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.music,
						parent, false);
				musicHolder = new musicAdapterViewHolder();
				musicHolder.image = (ImageView) convertView
						.findViewById(R.id.icon);
				musicHolder.texte = (TextView) convertView
						.findViewById(R.id.music);
				musicHolder.play = (ImageView) convertView
						.findViewById(R.id.cmd_play);
				musicHolder.append = (ImageView) convertView
						.findViewById(R.id.cmd_append);
				musicHolder.play.setClickable(true);
				musicHolder.play.setOnClickListener(buttonPlayClickListener);
				musicHolder.append.setClickable(true);
				musicHolder.append
						.setOnClickListener(buttonAppendClickListener);
				convertView.setTag(musicHolder);
			} else {
				musicHolder = (musicAdapterViewHolder) convertView.getTag();
			}
			musiccursor.moveToPosition(position);
			String filename = musiccursor.getString(musicFileName_column_index);
			musicHolder.image.setImageResource(R.drawable.ic_menu_music);
			updatePlayImage(filename, musicHolder.play);
			updatePlayListEntry(filename, musicHolder.append, convertView);
			String displayname = musiccursor
					.getString(musicDisplayedName_column_index);
			musicHolder.texte.setText(displayname);
			return convertView;
		}
	}

	private OnClickListener buttonPlayClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final int position = getListView().getPositionForView(v);
			musiccursor.moveToPosition(position);
			String filename = musiccursor.getString(musicFileName_column_index);
			if (servicePlaylist != null) {
				servicePlaylist.setPlay(filename);
			}
		}
	};

	private void updatePlayImage(String filename, ImageView play) {
		boolean played = false;
		if (servicePlaylist != null) {
			played = servicePlaylist.isPlayed(filename);
		}
		if (played) {
			play.setImageResource(R.drawable.ic_menu_pause);
		} else {
			play.setImageResource(R.drawable.ic_menu_play);
		}
	}

	private void resetPlayImages(ListView listView) {
		int numberChild = listView.getChildCount();
		int first = listView.getFirstVisiblePosition();
		int i;
		ImageView play;
		for (i = 0; i < numberChild; i++) {
			musiccursor.moveToPosition(i + first);
			String filename = musiccursor.getString(musicFileName_column_index);
			play = (ImageView) listView.getChildAt(i).findViewById(
					R.id.cmd_play);
			updatePlayImage(filename, play);
		}
	}

	private OnClickListener buttonAppendClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Boolean inList = false;
			final int position = getListView().getPositionForView(v);
			musiccursor.moveToPosition(position);
			String filename = musiccursor.getString(musicFileName_column_index);
			if (servicePlaylist != null) {
				inList = servicePlaylist.isInPlayList(filename);
			}
			if (inList) {
				servicePlaylist.removeFilePlayList(filename);
			} else {
				servicePlaylist.appendFilePlayList(filename);
			}
			

		}
	};

	private void updatePlayListEntry(String filename, ImageView append,
			View entry) {
		Boolean inList = false;
		if (servicePlaylist != null) {
			inList = servicePlaylist.isInPlayList(filename);
		}
		if (inList) {
			append.setImageResource(R.drawable.ic_menu_remove);
			entry.setBackgroundColor(Color.GRAY);
		} else {
			append.setImageResource(R.drawable.ic_menu_add);
			entry.setBackgroundColor(Color.BLACK);
		}
	}

	private void resetPlayList(ListView listView) {
		int first = listView.getFirstVisiblePosition();
		int numberChild = listView.getChildCount();
		int i;
		ImageView append;
		View child;
		for (i = 0; i < numberChild; i++) {
			musiccursor.moveToPosition(i + first);
			String filename = musiccursor.getString(musicFileName_column_index);
			append = (ImageView) listView.getChildAt(i).findViewById(
					R.id.cmd_append);
			child = listView.getChildAt(i);
			updatePlayListEntry(filename, append, child);
		}
	}

}
