package com.leopal.synap;

import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: nicolas
 * Date: 17/11/11
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class clContent {
    /** Logging TAG*/
    private static final String TAG = "MyActivity";

    /**
     * Holds the PCM audio details
     */
    protected clPcmFormat pv_pcmFormat;
    /**
     * Size of an audio block in ms
     */
    protected int pv_blockLengthMs;

    /**
     * Initialize the class and define the size of a block
     *
     * @param _blockLengthMs    Size of a block in ms
     */
    public clContent(int _blockLengthMs)
    {
        pv_pcmFormat = new clPcmFormat();
        pv_blockLengthMs = _blockLengthMs;
    }

    /**
     * Give a class access to the description of the uncompressed audio characteristics
     * @return class clPCM Format that describe the linear audio
     */
    public clPcmFormat getPcmFormat() {
        return pv_pcmFormat;
    }

    /**
     * Configure which type of audio sample will go out
     *
     * @param _bitDepth     Number of bits per sample (one channel)
     * @param _nChannels    Number of channels per sample
     * @param _SampleRate   Number of samples rate per second
     */
    public int setPlayoutParameter(int _bitDepth, int _nChannels, int _SampleRate)
    {
        pv_pcmFormat.setNumberOfChannel(_nChannels);
        pv_pcmFormat.setBitDepth(_bitDepth);
        pv_pcmFormat.setSampleRate(_SampleRate);

        return 1;
    }

    /**
     * Check the boolean type of parameter and log a message if an error occurs
     *
     * @param _toCheck         A boolean value to test
     * @param _errorMessage    The textual error message
     */
    protected void checkInfo_LogErrors(Boolean _toCheck, String _errorMessage)
    {
        if (! _toCheck) {
            Log.e(TAG, _errorMessage);
        }
    }

}
