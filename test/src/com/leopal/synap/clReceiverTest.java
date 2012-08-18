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

        clContentIn contentIn = new clContentInWaveFile(11);
        clContentOut contentOut = new clContentOutAudioTrack(400);
        contentIn.openAudioInputStream(inputStream);
        assertEquals(1,contentOut.setPlayoutParameter(16,2,44100));

        String mcastIP = "224.0.0.1";
        String ipServer = "127.0.0.1";

        classToTest.setContentInet(mcastIP);
        classToTest.setServerInet(ipServer);
        classToTest.setContentOut(contentOut);

        streamer.setContentIn(contentIn);
        streamer.setContentInet(mcastIP);

        (new Thread(classToTest)).start();
        streamer.start();

        Thread.sleep(15000);

        classToTest.cancel();
        streamer.stop();
    }

    public void testStart() throws Exception {
        clReceiver classToTest = new clReceiver();
        clContentOut contentOut = new clContentOutAudioTrack(500);
        String mcastIP = "224.0.0.1";
        String ipServer = "127.0.0.1";

        classToTest.setContentInet(mcastIP);
        classToTest.setServerInet(ipServer);
        classToTest.setContentOut(contentOut);
        contentOut.setPlayoutParameter(16,2,44100);

        new Thread(classToTest).start();
        //Thread.sleep(1000);
        classToTest.cancel();
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
        clContentOut contentOut = new clContentOutAudioTrack(500);

        classToTest.setContentOut(contentOut);
    }
}
