package com.leopal.synap;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyActivity extends Activity { 
   
	private clReceiver synapReceiver;

    StreamerListAdapter streamerAdapter;
	int countStreamers;
	private ListView list;
	private LinearLayout emptyList;
	
	/** Called when the activity is first created. */
    
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /* Button select file */
        Button serverInitButton = (Button)this.findViewById(R.id.start_streamer);
        serverInitButton.setOnClickListener(new View.OnClickListener() {

        	@Override
            public void onClick(View viewParam) {
                Intent myIntent = new Intent(viewParam.getContext(), StreamerActivity.class);
                startActivityForResult(myIntent, 0);

            }
        });
        ((TextView)this.findViewById(R.id.title_streamer)).setBackgroundColor(Color.GRAY);
        ((TextView)this.findViewById(R.id.title_streamer)).setTextColor(Color.WHITE);
        init_streamers_grid();
        doBindService();
    }
    @Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void init_streamers_grid() {
		countStreamers = 0;
		streamerAdapter = new StreamerListAdapter();
		list = (ListView) findViewById(R.id.StreamersList);
		
		list.setAdapter(streamerAdapter);
		emptyList = (LinearLayout) findViewById(R.id.Search);
		
		if (list.getCount() == 0) {
			emptyList.setVisibility(View.VISIBLE);
		} else {
			emptyList.setVisibility(View.INVISIBLE);
		}
	}
	
	// Announcement Service connections
	private boolean serviceAnnouncementIsBound;
	private clAnnouncementService serviceAnnouncement;

	final clAnnouncementServiceListener streamerServiceListener = new clAnnouncementServiceListener() {
		public void dataChanged(Byte action) {
			MyActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (serviceAnnouncement.getSize() > 0 ) {
						streamerAdapter.addItem(serviceAnnouncement.getListItem(0));
						streamerAdapter.notifyDataSetChanged();
					}
				}
			});
		}
	};

	private ServiceConnection serviceAnnouncementConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			serviceAnnouncement = ((clAnnouncementService.LocalBinder) service)
					.getService();
			serviceAnnouncement.addListener(streamerServiceListener);
		}

		public void onServiceDisconnected(ComponentName className) {
			serviceAnnouncement = null;
		}
	};
	
	void doBindService() {
		// bindService(...) is not directly available in childs of TabHost.
		// requires usage of getApplicationContext()
		bindService(new Intent(MyActivity.this, clAnnouncementService.class),
				serviceAnnouncementConnection, Context.BIND_AUTO_CREATE);
		serviceAnnouncementIsBound = true;
	}

	void doUnbindService() {
		if (serviceAnnouncementIsBound) {
			getApplicationContext().unbindService(serviceAnnouncementConnection);
			serviceAnnouncementIsBound = false;
		}
	}

	public class StreamerListAdapter extends BaseAdapter {

		private ArrayList<clSynapEntity> mData = new ArrayList<clSynapEntity>();
		
		public StreamerListAdapter() {
			initialize();
		}
		
		public void initialize() {
			mData.clear();
			notifyDataSetChanged();
		}

		public void addItem(final clSynapEntity item) {
			emptyList.setVisibility(View.INVISIBLE);
			mData.add(item);
		}

		public void removeItem(final clSynapEntity item) {
			for (int i = 0; i < mData.size(); i++) {
				if (mData.get(i).equals(item)) {
					mData.remove(i);
				}
			}
		}

		public void removeItem(final int index) {
			mData.remove(index);
		}

		public int getCount() {
			return mData.size();
		}

		public clSynapEntity getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		private class StreamerListViewHolder {
			public ImageView image;
			public TextView texte;
			public ImageView play;
			//public ImageView append;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			System.gc();
			StreamerListViewHolder streamerListHolder;
			
			if (convertView == null) {
				// TODO : replace reuse of music Layout by a dedicated layout (or not)
				convertView = getLayoutInflater().inflate(R.layout.music,
						parent, false);
				streamerListHolder = new StreamerListViewHolder();
				streamerListHolder.image = (ImageView) convertView
						.findViewById(R.id.icon);
				streamerListHolder.texte = (TextView) convertView
						.findViewById(R.id.music);
				streamerListHolder.play = (ImageView) convertView
						.findViewById(R.id.cmd_play);
				//streamerListHolder.append = (ImageView) convertView
					//	.findViewById(R.id.cmd_append);
				streamerListHolder.play.setClickable(true);
				streamerListHolder.play.setOnClickListener(buttonPlayClickListener);
				/* not used
				 * streamerListHolder.append.setClickable(true);
				 * streamerListHolder.append
						.setOnClickListener(buttonAppendClickListener);
				 */
				
				convertView.setTag(streamerListHolder);
			} else {
				streamerListHolder = (StreamerListViewHolder) convertView.getTag();
			}
			clSynapEntity lSynapEntity = serviceAnnouncement.getListItem(position);;
			streamerListHolder.image.setImageResource(R.drawable.ic_menu_feed);
			String currentIP = "";
			if (synapReceiver != null) {
				// TODO : Comprendre pourquoi ce stop - commenté pour tester reception
				//synapReceiver.stop();
				currentIP = synapReceiver.getServerInet();
			}
			if (lSynapEntity.getIpAdress().compareTo(currentIP) == 0) {
				streamerListHolder.play.setImageResource(R.drawable.ic_menu_cancel);
			} else {
				streamerListHolder.play.setImageResource(R.drawable.ic_menu_play);
			}
			streamerListHolder.texte.setText(lSynapEntity.getStreamInfo());
			
			return convertView;
		}
	}
	private void setPlayIcon(ImageView play, String streamerIp) {
		
	}
	private OnClickListener buttonPlayClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			final int position = list.getPositionForView(v);
			clSynapEntity lSynapEntity = serviceAnnouncement.getListItem(position);
			String streamerIp = lSynapEntity.getIpAdress();
			ImageView play = (ImageView)list.findViewById(R.id.cmd_play);
			String currentIP = "";
			
			if (synapReceiver != null) {
				synapReceiver.stop();
				currentIP = synapReceiver.getServerInet();
			}
			if (streamerIp.compareTo(currentIP) == 0) {
				play.setImageResource(R.drawable.ic_menu_play);
			} else {
				clContentOut contentOut = new clContentOutAudioTrack(1000);
		        String mcastIP = "224.0.0.1";
                //contentOut.setPlayoutParameter(16,2,44100);
                contentOut.setPlayoutParameter(8,1,32000);
		        synapReceiver = new clReceiver();
		        synapReceiver.setContentInet(mcastIP);
		        synapReceiver.setServerInet(streamerIp);
		        synapReceiver.setContentOut(contentOut);
		        synapReceiver.start();
				play.setImageResource(R.drawable.ic_menu_cancel);
			}
			

		}
	};

}