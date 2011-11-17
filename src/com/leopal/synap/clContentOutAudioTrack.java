package com.leopal.synap;

import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by IntelliJ IDEA.
 * User: nicolas
 * Date: 17/11/11
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class clContentOutAudioTrack extends clContentOut{
    /**
     * Reference to audio track used for playout
     */
    private AudioTrack pv_audioTrack;

    /**
     * Initialize the class and define the size of a block
     *
     * @param _blockLengthMs Size of a block in ms
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
                pv_blockLengthMs/1000*5*pv_pcmFormat.getOneSecondByteSize(), //TODO Find a way to determine buffer size
                AudioTrack.MODE_STREAM
        );

        if (pv_audioTrack.getState()!=0)
            return 1;
        else
            return 0;
    }

    /**
     * Play an audio block
     *
     * @param _data    A byte array containing raw audio data
     */
    @Override
    public void queueAudioBlock(byte[] _data, int _numberOfSample)
    {
        pv_audioTrack.write(_data, 0, _numberOfSample*pv_pcmFormat.getOneSampleByteSize());
    }

    /**
     * Start immediate playout of the queue
     */
    @Override
    public void startToPlay()
    {
        pv_audioTrack.play();
    }

    /**
     * Stop playout and flush the queue
     */
    @Override
    public void stopToPlay()
    {
        pv_audioTrack.stop();
        pv_audioTrack.flush();
    }

}
