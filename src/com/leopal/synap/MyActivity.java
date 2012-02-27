package com.leopal.synap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyActivity extends Activity { 
   
	private clStreamer synapStreamer;
	private clReceiver synapReceiver;

    private Button serverInitButton;
	private Button serverStartButton;
	private Button serverStopButton;
	private Button clientStartButton;
	private Button clientStopButton;

	private InputStream inputStream;
	
	private BufferedInputStream bufferedInputStream;
	
	private String mServerIP;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        EditText lTextIP = ((EditText)this.findViewById(R.id.server_ip));
        mServerIP = lTextIP.getText().toString();

        /* Button init server */
        serverInitButton = (Button)this.findViewById(R.id.server_init);
        serverInitButton.setOnClickListener(new View.OnClickListener() {

        	@Override
            public void onClick(View viewParam) {
        		File f = new File(Environment.getExternalStorageDirectory() 
                        + File.separator + "Music" + File.separator + "rhcp_atw.wav");
                InputStream lInputStream = null;
        		try {
        			lInputStream = new FileInputStream(f);
        		} catch (FileNotFoundException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
            
                bufferedInputStream = new BufferedInputStream(lInputStream);
                
                //inputStream = getResources().openRawResource(R.raw.rhcp_atw);
                clContentIn contentIn = new clContentInWaveFile(16);
                try {
                    contentIn.openAudioBufferedInputStream(bufferedInputStream);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                synapStreamer = new clStreamer();
                synapStreamer.setContentIn(contentIn);
                synapStreamer.setContentInet("224.0.0.1");
                //synapStreamer.start();

            }
        });

        /* Button select file */
        serverInitButton = (Button)this.findViewById(R.id.select_file);
        serverInitButton.setOnClickListener(new View.OnClickListener() {

        	@Override
            public void onClick(View viewParam) {
                Intent myIntent = new Intent(viewParam.getContext(), StreamerActivity.class);
                startActivityForResult(myIntent, 0);

            }
        });

        /* Button start server */
        serverStartButton = (Button)this.findViewById(R.id.server_start);
        serverStartButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View viewParam) {
				/*inputStream = getResources().openRawResource(R.raw.audio_44100_16bits_2channels_extract);
				clContentIn contentIn = new clContentInWaveFile(8);
		        try {
					contentIn.openAudioInputStream(inputStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        synapStreamer = new clStreamer();
		        synapStreamer.setContentIn(contentIn);
		        synapStreamer.setContentInet("224.0.0.1");*/
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
				clContentOut contentOut = new clContentOutAudioTrack(40);
		        String mcastIP = "224.0.0.1";

                contentOut.setPlayoutParameter(16,2,44100);

		        synapReceiver = new clReceiver();
		        synapReceiver.setContentInet(mcastIP);
		        synapReceiver.setServerInet(mServerIP);
		        synapReceiver.setContentOut(contentOut);

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