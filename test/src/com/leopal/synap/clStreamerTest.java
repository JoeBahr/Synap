package com.leopal.synap;

import android.test.AndroidTestCase;
import android.content.res.Resources;

import java.io.InputStream;

/**
 * Test for clStreamer
 *
 * Creation information
 * @author nicolas
 * Date: 17/11/11
 * Time: 20:32
 */
public class clStreamerTest extends AndroidTestCase {
    private Resources resources;
    private InputStream inputStream;

    protected void setUp() throws Exception {
        super.setUp();
        resources = getContext().getResources();
        inputStream = resources.openRawResource(R.raw.audio_44100_16bits_2channels_extract);
    }

    public void testStart() throws Exception {
        //Prepare test content
        clContentIn contentIn = new clContentInWaveFile(8);
        contentIn.openAudioInputStream(inputStream);
        assertEquals(false,contentIn.isEndOfContent());

        clStreamer classToTest = new clStreamer();
        classToTest.setContentIn(contentIn);
        classToTest.setContentInet("239.1.1.1");
        classToTest.start();
        Thread.sleep(10000);
        classToTest.stop();

        inputStream.reset();
    }

    /**
     * Test stop before end of send
     * @throws Exception
     */
    public void testStop() throws Exception {
        clContentIn contentIn = new clContentInWaveFile(1000);
        contentIn.openAudioInputStream(inputStream);
        assertEquals(false,contentIn.isEndOfContent());

        clStreamer classToTest = new clStreamer();
        classToTest.setContentIn(contentIn);
        classToTest.setContentInet("239.1.1.1");
        classToTest.start();
        Thread.sleep(2000);
        classToTest.stop();
        inputStream.reset();
    }
}
