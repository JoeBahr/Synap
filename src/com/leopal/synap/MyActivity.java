package com.leopal.synap;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SynapActivity extends Activity {
   
	private clStreamer synapStreamer;
	private clReceiver synapReceiver;
	
	private Button serverStartButton;
	private Button serverStopButton;
	private Button clientStartButton;
	private Button clientStopButton;
	
	private InputStream inputStream;
	
	private String mServerIP;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        EditText lTextIP = ((EditText)this.findViewById(R.id.server_ip));
        mServerIP = lTextIP.getText().toString();
        
        /* Button start server */
        serverStartButton = (Button)this.findViewById(R.id.server_start);
        serverStartButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View viewParam) {
				inputStream = getResources().openRawResource(R.raw.audio_44100_16bits_2channels_extract);
				clContentIn contentIn = new clContentInWaveFile(8);
		        try {
					contentIn.openAudioInputStream(inputStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        synapStreamer = new clStreamer();
		        synapStreamer.setContentIn(contentIn);
		        synapStreamer.setContentInet("224.0.0.1");
		        synapStreamer.start();
		        
        	}
        });
        
        /* button stop server */
        serverStopButton = (Button)this.findViewById(R.id.server_stop);
        serverStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View viewParam) {
				synapStreamer.stop();
		        try {
					inputStream.reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
        	}
        });
        
        /* Button start client */
        clientStartButton = (Button)this.findViewById(R.id.client_start);
        clientStartButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View viewParam) {
				clContentOut contentOut = new clContentOutAudioTrack(15);
		        String mcastIP = "224.0.0.1";
		        
		        synapReceiver = new clReceiver();
		        synapReceiver.setContentInet(mcastIP);
		        synapReceiver.setServerInet(mServerIP);
		        synapReceiver.setContentOut(contentOut);
		        contentOut.setPlayoutParameter(16,2,44100);

		        synapReceiver.start();
		        
        	}
        });
        
        /* button stop server */
        clientStopButton = (Button)this.findViewById(R.id.client_stop);
        clientStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View viewParam) {
				synapReceiver.stop();
		        
        	}
        });
    }

}