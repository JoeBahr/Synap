/**
 * Activity to create any tabs available in streamer mode 
 */
package com.leopal.synap;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

public class StreamerActivity extends  synapMainClass{

    LocalActivityManager tabHostLam;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.streamertabs);
        //tabHost = getTabHost();

        Resources res = getResources();
        tabHostLam = new LocalActivityManager(this, false);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

        tabHostLam.dispatchCreate(savedInstanceState);
        tabHost.setup(tabHostLam);
        TabHost.TabSpec tabSpec;

        Intent intentMusic = new Intent(this, MusicActivity.class);
        tabSpec = tabHost.newTabSpec("music").setIndicator("From music library").setContent(intentMusic);
        tabHost.addTab(tabSpec);

		// TODO : Intent intentFile = new Intent(this, FileActivity.class);
		Intent intentFile = new Intent(this, testClass.class);
		tabSpec = tabHost.newTabSpec("file").setIndicator("From file system").setContent(intentFile);
		tabHost.addTab(tabSpec);

		// TODO : Intent intentFile = new Intent(this, PlaylistActivity.class);
		Intent intentPlaylist = new Intent(this, testClass.class);
		tabSpec = tabHost.newTabSpec("list").setIndicator("My playlist").setContent(intentPlaylist);
		tabHost.addTab(tabSpec);
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

}
//http://www.helloandroid.com/tutorials/tabhost-outside-tabactivity
//http://thepseudocoder.wordpress.com/2011/10/04/android-tabs-the-fragment-way/
//http://wptrafficanalyzer.in/blog/creating-navigation-tabs-using-tabhost-and-fragments-in-android/

