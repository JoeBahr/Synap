package com.leopal.synap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class for Audio Wave File reading
 *
 * Creation information
 * @author nicolas
 * Date: 17/11/11
 * Time: 16:41
 */
public class clContentInWaveFile extends clContentIn {

    /* ************************
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
    private int pv_audioFileDataLength=0;
    private static final String RIFF_HEADER = "RIFF";
    private static final String WAVE_HEADER = "WAVE";
    private static final String FMT_HEADER = "fmt ";
    private static final String DATA_HEADER = "data";
    private static final int HEADER_SIZE = 44;
    private static final String CHARSET = "ASCII";

    public clContentInWaveFile(int _blockLengthMs) {
        super(_blockLengthMs);
    }

    @Override
    public void openAudioInputStream(InputStream _file)
    throws IOException {
        pv_audioFile = new BufferedInputStream(_file);
        AudioFileWave_ReadHeader(pv_audioFile);
    }

    @Override
    public void openAudioBufferedInputStream(BufferedInputStream _file)
    throws IOException {
        pv_audioFile = new BufferedInputStream(_file);
        AudioFileWave_ReadHeader(pv_audioFile);
    }

    @Override
    public int readNextAudioBlock(byte[] _data)
    {
        int sampleCount = 0;
        try {
            sampleCount = AudioFileWave_read(_data)/pv_pcmFormat.getOneSampleByteSize();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sampleCount;
    }

    @Override
    public boolean isContentAvailable() {
        return (!isEndOfContent() && (pv_audioFileDataLength!=0));
    }

    @Override
    public boolean isEndOfContent() {
        return (pv_audioFilePosition >= pv_audioFileDataLength);
    }

    /**
     * Read a wav content chunk
     *
     * @param _data    buffer for storing the result
     *
     * @return the number of read byte
     */
    private int AudioFileWave_read(byte[] _data)
    throws IOException {
        int read = pv_audioFile.read(_data, 0, _data.length);

        if ((pv_audioFilePosition+read>pv_audioFileDataLength) && (read !=0))
            if (pv_audioFilePosition<pv_audioFileDataLength)
                read=pv_audioFileDataLength-pv_audioFilePosition;
            else
                read=0;

        pv_audioFilePosition = pv_audioFilePosition + read;
        return read;
    }

    /**
     * Read a wav Audio Header from an Inputstream and store info in class (sample rate/depth)
     *
     * @param _waveInOutStream    A file inputStream pointing to the audio file
     *
     * @throws java.io.IOException
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
          //Log.d(TAG, "Skipping non-data chunk");
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

}
