package com.leopal.synap;

import android.test.AndroidTestCase;
import android.content.res.Resources;

import java.io.InputStream;

/**
 * Test for clReceiver
 *
 * Creation information
 * @author nicolas
 *         Date: 24/11/11
 *         Time: 18:04
 */
public class clReceiverTest extends AndroidTestCase {
    public void testEndToEnd() throws Exception {
        Resources resources =  getContext().getResources();
        InputStream inputStream = resources.openRawResource(com.leopal.synap.R.raw.audio_44100_16bits_2channels_extract);

        clReceiver classToTest = new clReceiver();
        clStreamer streamer = new clStreamer();

        clContentIn contentIn = new clContentInWaveFile(16);
        clContentOut contentOut = new clContentOutAudioTrack(1000);
        contentIn.openAudioInputStream(inputStream);
        assertEquals(1,contentOut.setPlayoutParameter(16,2,44100));

        String mcastIP = "224.0.0.1";
        String ipServer = "127.0.0.1";

        classToTest.setContentInet(mcastIP);
        classToTest.setServerInet(ipServer);
        classToTest.setContentOut(contentOut);

        streamer.setContentIn(contentIn);
        streamer.setContentInet(mcastIP);

        classToTest.start();
        streamer.start();

        Thread.sleep(15000);

        classToTest.stop();
        streamer.stop();
    }

    public void testStart() throws Exception {
        clReceiver classToTest = new clReceiver();
        clContentOut contentOut = new clContentOutAudioTrack(15);
        String mcastIP = "224.0.0.1";
        String ipServer = "127.0.0.1";

        classToTest.setContentInet(mcastIP);
        classToTest.setServerInet(ipServer);
        classToTest.setContentOut(contentOut);
        contentOut.setPlayoutParameter(16,2,44100);

        classToTest.start();
        //Thread.sleep(1000);
        classToTest.stop();
    }

    public void testSetContentInet() throws Exception {
        clReceiver classToTest = new clReceiver();
        String mcastIP = "224.0.0.1";

        classToTest.setContentInet(mcastIP);
        assertEquals(mcastIP,classToTest.getContentInet());
    }

    public void testSetServerInet() throws Exception {
        clReceiver classToTest = new clReceiver();
        String ipServer = "127.0.0.1";

        classToTest.setServerInet(ipServer);
        assertEquals(ipServer,classToTest.getServerInet());
    }

    public void testSetContentOut() throws Exception {
        clReceiver classToTest = new clReceiver();
        clContentOut contentOut = new clContentOutAudioTrack(8);

        classToTest.setContentOut(contentOut);
    }
}
