package com.leopal.synap;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Class for Playout on a local Android device through AudioTrack
 *
 * Creation information
 * @author nicolas
 * Date: 17/11/11
 * Time: 17:00
 */
public class clContentOutAudioTrack extends clContentOut{
    private static final String TAG = "clContentOutAudioTrack";

    /**
     * Reference to audio track used for playout
     */
    private AudioTrack pv_audioTrack;

    /**
     * Initialize the class and define the size of a block
     *
     * @param _blockLengthMs Size of a block in ms (Advise to take 1000ms of cache)
     */
    public clContentOutAudioTrack(int _blockLengthMs) {
        super(_blockLengthMs);
    }

    @Override
    public int setPlayoutParameter(int _bitDepth, int _nChannels, int _SampleRate)
    {
        super.setPlayoutParameter(_bitDepth, _nChannels, _SampleRate);

        pv_audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                pv_pcmFormat.getSampleRate(),
                pv_pcmFormat.getAndroidChannelConfig(),
                pv_pcmFormat.getAndroidEncodingFormat(),
                pv_blockLengthMs*5*pv_pcmFormat.getOneMsByteSize(), //TODO Find a way to determine buffer size
                AudioTrack.MODE_STREAM
        );

        if (pv_audioTrack.getState()>0) {
            return 1;
        } else {
            Log.e(TAG, "AudioTrack initialisation Error Getstate="+pv_audioTrack.getState());
            return 0;
        }
    }

    /**
     * Play an audio block
     *
     * @param _data    A byte array containing raw audio data
     */
    @Override
    public boolean queueAudioBlock(byte[] _data, int _numberOfSample)
    {
        if (_numberOfSample>0) {
            Log.v(TAG, "queueAudioBlock sample="+_numberOfSample);
            switch(pv_audioTrack.write(_data, 0, _numberOfSample*pv_pcmFormat.getOneSampleByteSize())) {
                case AudioTrack.ERROR_INVALID_OPERATION:
                    Log.e(TAG, "Audiotrack.write ERROR_INVALID_OPERATION");
                    return false;
                case AudioTrack.ERROR_BAD_VALUE:
                    Log.e(TAG, "Audiotrack.write ERROR_BAD_VALUE");
                    return false;
                default:
                    return true;
            }
        }
        return true;
    }

    /**
     * Start immediate playout of the queue
     */
    @Override
    public void start()
    {
        if (pv_audioTrack!=null)
            pv_audioTrack.play();
    }

    /**
     * Stop playout and flush the queue
     */
    @Override
    public void stop()
    {
        if (pv_audioTrack!=null) {
            pv_audioTrack.stop();
            pv_audioTrack.flush();
        }
    }

}
