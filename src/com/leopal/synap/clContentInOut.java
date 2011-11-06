package com.leopal.synap;

import android.util.Log;

import java.io.*;
import java.nio.*;

import android.media.AudioTrack;
import android.media.AudioFormat;
import 	android.media.AudioManager;

/**
 * Manage Audio In/Out
 */
public class clContentInOut {

    public clContentInOut(int _blockLengthMs)
    {
        pv_pcmFormat = new clPcmFormat();
        pv_blockLengthMs = _blockLengthMs;
    }

    /** Logging TAG*/
    private static final String TAG = "MyActivity";

    /*************************
     * WAVE FILE INFO
     */
    /**
     * Audio File pointer
     */
    private InputStream pv_audioFile;
    /**
     * Audio File position into this stream in byte
     */
    private int pv_audioFilePosition;
    /**
     * Audio File size
     */
    private int pv_audioFileDataLength;
    private static final String RIFF_HEADER = "RIFF";
    private static final String WAVE_HEADER = "WAVE";
    private static final String FMT_HEADER = "fmt ";
    private static final String DATA_HEADER = "data";
    private static final int HEADER_SIZE = 44;
    private static final String CHARSET = "ASCII";

    /*************************
     * AUDIO TRACK INFORMATION
     */
    private AudioTrack pv_audioTrack;

    /*************************
     * AUDIO PCM INFORMATION
     */
    /**
     * Length in milliseconds of an Audio block
     */
    private int pv_blockLengthMs;

    private clPcmFormat pv_pcmFormat;

    /**
     * Give a class access to the descitpion of the uncompressed audio characteristics
     * @return class clPCM Format that describe the linear audio
     */
    public clPcmFormat getPcmFormat() {
        return pv_pcmFormat;
    }

    /**
     * Play an audio block
     *
     * @param _data    A byte array containing raw audio data
     */
    public void queueAudioBlock(byte[] _data, int _numberOfSample)
    {
        pv_audioTrack.write(_data, 0, _numberOfSample*pv_pcmFormat.getOneSampleByteSize());
    }

    /**
     * Start immediate playout of the queue
     */
    public void startToPlay()
    {
        pv_audioTrack.play();
    }

    /**
     * Stop playout and flush the queue
     */
    public void stopToPlay()
    {
        pv_audioTrack.stop();
        pv_audioTrack.flush();
    }

    /**
     * Play an audio block WARNING THIS is a 100 block buffer
     *
     * @param _bitDepth     Number of bits per sample
     * @param _nChannels    Number of channels per sample
     * @param _SampleRate   Number of samples rate per second
     */
    public int configurePlayoutParameter(int _bitDepth, int _nChannels, int _SampleRate)
    {
        pv_pcmFormat.setNumberOfChannel(_nChannels);
        pv_pcmFormat.setBitDepth(_bitDepth);
        pv_pcmFormat.setSampleRate(_SampleRate);

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
     * Open an audio file to extract chunk of Audio
     *
     * @param _file    An inputStream File to be opened
     */
    public void openAudioInputStream(InputStream _file)
    throws IOException{
        AudioFileWave_Open(_file);
    }

    /**
     * Read next one audio block
     *
     * @return The number of sample that were read
     */
    public int readNextAudioBlock(byte[] _data)
    {
        //byte[] data = new byte[_numberOfMsToRead/1000*pv_pcmFormat.getOneSecondByteSize()];
        int sampleCount = 0;
        try {
            sampleCount = AudioFileWave_read(_data)/pv_pcmFormat.getOneSampleByteSize();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sampleCount;
    }

    /**
     * Prepare the main buffer that will be used for reading audio content
     *
     * @return The number of sample that were read
     */
    public byte[] getAudioBlockBuffer()
    {
        int bufferSize = pv_blockLengthMs/1000*pv_pcmFormat.getOneSecondByteSize();
        return new byte[bufferSize];
    }

    private void AudioFileWave_Open(InputStream _file)
     throws IOException {
        pv_audioFile = new BufferedInputStream(_file);
        AudioFileWave_ReadHeader(pv_audioFile);
    }

    /**
     * Read a wav Audio Header from an inputstream and store info in class (sample rate/depth)
     *
     * @param _waveInOutStream    A file inputStream pointing to the audio file
     */
    private void AudioFileWave_ReadHeader(InputStream _waveInOutStream)
            throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        _waveInOutStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());

        buffer.rewind();
        buffer.position(buffer.position() + 20);

        int format = buffer.getShort();
        checkInfo_LogErrors(format == 1, "Unsupported encoding: " + format); // 1 means Linear PCM

        int channels = buffer.getShort();
        checkInfo_LogErrors(channels == 1 || channels == 2, "Unsupported channels: " + channels);
        pv_pcmFormat.setNumberOfChannel(channels);

        int rate = buffer.getInt();
        checkInfo_LogErrors(rate <= 48000 && rate >= 11025, "Unsupported rate: " + rate);
        buffer.position(buffer.position() + 6);
        pv_pcmFormat.setSampleRate(rate);

        int bits = buffer.getShort();
        checkInfo_LogErrors(bits == 16, "Unsupported bits: " + bits);
        pv_pcmFormat.setBitDepth(bits);

        int dataSize = 0;
        while (buffer.getInt() != 0x61746164) { // "data" marker
          Log.d(TAG, "Skipping non-data chunk");
          int size = buffer.getInt();
          _waveInOutStream.skip(size);

          buffer.rewind();
          _waveInOutStream.read(buffer.array(), buffer.arrayOffset(), 8);
          buffer.rewind();
        }
        pv_audioFileDataLength = buffer.getInt();
        checkInfo_LogErrors(dataSize > 0, "wrong datasize: " + dataSize);

        //return new WavInfo(new FormatSpec(rate, channels == 2), dataSize);
    }

    /**
     * Read a wav content chunk
     *
     * @param _data    buffer for storing the result
     *
     * @return the number of read byte
     */
    //TODO To modify in order to manage right file end
    private int AudioFileWave_read(byte[] _data)
    throws IOException {
        int read = 0;
        read = pv_audioFile.read(_data, 0, _data.length);

        if ((pv_audioFilePosition+read>pv_audioFileDataLength) && (read !=0))
            if (pv_audioFilePosition<pv_audioFileDataLength)
                read=pv_audioFileDataLength-pv_audioFilePosition;
            else
                read=0;

        pv_audioFilePosition = pv_audioFilePosition + read;
        return read;
    }

    /**
     * Check the boolean type of parameter and log a message if an error occurs
     *
     * @param _toCheck         A boolean value to test
     * @param _errorMessage    The textual error message
     */
    private void checkInfo_LogErrors(Boolean _toCheck, String _errorMessage)
    {
        if (! _toCheck) {
            Log.e(TAG, _errorMessage);
        }
    }

}
