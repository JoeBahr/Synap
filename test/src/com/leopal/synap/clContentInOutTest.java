package com.leopal.synap;

import android.content.res.Resources;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: nicolas
 * Date: 05/11/11
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class clContentInOutTest extends android.test.AndroidTestCase {
    public void testOpenAudioFile() throws Exception {
        clContentIn classToTest = new clContentInWaveFile(1000);

        assertEquals(false, classToTest.isContentAvailable());
        assertEquals(true, classToTest.isEndOfContent());

        Resources resources =  getContext().getResources();
        InputStream inputStream = resources.openRawResource(com.leopal.synap.R.raw.audio_44100_16bits_2channels_extract);
        classToTest.openAudioInputStream(inputStream);
        clPcmFormat detectedFormat = classToTest.getPcmFormat();

        assertEquals(2,detectedFormat.getNumberOfChannel());
        assertEquals(16,detectedFormat.getBitDepth());
        assertEquals(44100,detectedFormat.getSampleRate(),44100);
        assertEquals(true, classToTest.isContentAvailable());
        assertEquals(false, classToTest.isEndOfContent());
    }

    public void testReadNextAudioBlock() throws Exception {
        clContentIn classToTest = new clContentInWaveFile(8);
        Resources resources =  getContext().getResources();
        InputStream inputStream = resources.openRawResource(com.leopal.synap.R.raw.audio_44100_16bits_2channels_extract);

        classToTest.openAudioInputStream(inputStream);
        clPcmFormat detectedFormat = classToTest.getPcmFormat();

        byte[] AudioBuff = classToTest.getAudioBlockBuffer();
        int sampleCount;
        int totalSampleCount=0;
        do{
            sampleCount = classToTest.readNextAudioBlock(AudioBuff);
            totalSampleCount = totalSampleCount + sampleCount;
        } while (sampleCount!=0);

        assertEquals(441000,totalSampleCount);
        assertEquals(false, classToTest.isContentAvailable());
        assertEquals(true, classToTest.isEndOfContent());
    }

    /**
     * Read buffer then send it for play
     * @throws Exception
     */
    public void testStartToPlay() throws Exception {
        int bufferLength = 20;
        clContentIn audioReader = new clContentInWaveFile(bufferLength);
        clContentOut classToTest = new clContentOutAudioTrack(bufferLength);

        Resources resources =  getContext().getResources();
        InputStream inputStream = resources.openRawResource(com.leopal.synap.R.raw.audio_44100_16bits_2channels_extract);

        audioReader.openAudioInputStream(inputStream);
        clPcmFormat detectedFormat = audioReader.getPcmFormat();

        int confResult = classToTest.setPlayoutParameter(detectedFormat.getBitDepth(),detectedFormat.getNumberOfChannel(),detectedFormat.getSampleRate());
        assertEquals(1,confResult);
        classToTest.start();

        byte[] AudioBuff = audioReader.getAudioBlockBuffer();
        int sampleCount;
        int totalSampleCount=0;
        do{
            sampleCount = audioReader.readNextAudioBlock(AudioBuff);
            classToTest.queueAudioBlock(AudioBuff, sampleCount);
            Thread.sleep(bufferLength-3);
            totalSampleCount = totalSampleCount + sampleCount;
        } while (sampleCount!=0);
        //} while(totalSampleCount<441000);

        Thread.sleep(13000);
        classToTest.stop();
    }
}
