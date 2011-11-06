package com.leopal.synap;

import android.media.AudioFormat;

/**
 * This class store the PCM non linear characterictics and
 * allow to exchange between physical detail and android representation of audio
 * Mainly for Audiotrack and WAV Files
 */
public class clPcmFormat {
    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public int getNumberOfChannel() {
        return numberOfChannel;
    }

    public void setNumberOfChannel(int numberOfChannel) {
        this.numberOfChannel = numberOfChannel;
    }

    /**
     * Audio sample rate in number of sample per seconds
     */
    private int sampleRate;
    /**
     * Audio audio depth
     */
    private int bitDepth;
    /**
     * Audio number of channels
     */
    private int numberOfChannel;

    public int getAndroidChannelConfig()
    {
        int retValue=0;

        if (getNumberOfChannel()==1) retValue=AudioFormat.CHANNEL_CONFIGURATION_MONO;
        if (getNumberOfChannel()==2) retValue=AudioFormat.CHANNEL_CONFIGURATION_STEREO;

        return retValue;
    }

    public int getAndroidEncodingFormat()
    {
        int retValue=0;

        if (getBitDepth()==16) retValue=AudioFormat.ENCODING_PCM_16BIT;
        if (getBitDepth()==8)  retValue=AudioFormat.ENCODING_PCM_8BIT;

        return retValue;
    }

    public int getOneSecondByteSize()
    {
        return getSampleRate()*getOneSampleByteSize();
    }

    public int getOneSampleByteSize()
    {
        return getBitDepth()/8*getNumberOfChannel();
    }
}
