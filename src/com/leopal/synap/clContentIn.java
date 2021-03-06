package com.leopal.synap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Root class for content reading
 * The usage of such derived class are
 * -open inputstream
 * -getBuffer
 * -readAudioBlock with this buffer
 *
 * Creation information
 * @author nicolas
 * Date: 17/11/11
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class clContentIn extends clContent{

    public clContentIn(int _blockLengthMs) {
        super(_blockLengthMs);
    }

    /**
     * Open an audio file to extract chunk of Audio
     *
     * @param _file    An inputStream File to be opened
     * @throws java.io.IOException
     */
    public void openAudioInputStream(InputStream _file)
    throws IOException {
    }

    public void openAudioBufferedInputStream(BufferedInputStream _file)
    throws IOException {
    }

    /**
     * Read next one audio block
     *
     * @param _data The buffer where to store read data
     * @return The number of sample that were read
     */
    public int readNextAudioBlock(byte[] _data)
    {
        return 0;
    }

    /**
     * Say if a client can read data
     *
     * @return True if there are data available
     */
    public boolean isContentAvailable() {
        return false;
    }

    /**
     * Say if a client if the end of stream has been reached
     *
     * @return True if there are data available
     */
    public boolean isEndOfContent() {
        return true;
    }

    /**
     * Prepare the main buffer that will be used for reading audio content
     *
     * @return a buffer to store audio data in
     */
    public byte[] getAudioBlockBuffer()
    {
        int bufferSize = pv_blockLengthMs*pv_pcmFormat.getOneMsByteSize();
        return new byte[bufferSize];
    }

}
