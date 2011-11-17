package com.leopal.synap;

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
     * Prepare the main buffer that will be used for reading audio content
     *
     * @return a buffer to store audio data in
     */
    public byte[] getAudioBlockBuffer()
    {
        int bufferSize = pv_blockLengthMs/1000*pv_pcmFormat.getOneSecondByteSize();
        return new byte[bufferSize];
    }

}
