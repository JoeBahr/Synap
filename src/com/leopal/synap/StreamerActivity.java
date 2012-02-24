/**
 * Activity to create any tabs available in streamer mode 
 */
package com.leopal.synap;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class StreamerActivity extends TabActivity {

	private TabHost tabHost;
	private TabSpec tabSpec;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.streamertabs);
		tabHost = getTabHost();

		Intent intentMusic = new Intent(this, MusicActivity.class);
		tabSpec = tabHost.newTabSpec("music")
				.setIndicator("From music library").setContent(intentMusic);
		tabHost.addTab(tabSpec);

		// TODO : Intent intentFile = new Intent(this, FileActivity.class);
		Intent intentFile = new Intent(this, MusicActivity.class);
		tabSpec = tabHost.newTabSpec("file").setIndicator("From file system")
				.setContent(intentFile);
		tabHost.addTab(tabSpec);

		// TODO : Intent intentFile = new Intent(this, PlaylistActivity.class);
		Intent intentPlaylist = new Intent(this, MusicActivity.class);
		tabSpec = tabHost.newTabSpec("list").setIndicator("My playlist")
				.setContent(intentPlaylist);
		tabHost.addTab(tabSpec);
	}
}
