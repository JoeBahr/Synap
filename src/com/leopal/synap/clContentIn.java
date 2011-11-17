package com.leopal.synap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: nicolas
 * Date: 17/11/11
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 *
 * The method is
 * -open inputstream
 * -getBuffer
 * -readAudioBlock with this buffer
 */
public class clContentIn extends clContent{

    public clContentIn(int _blockLengthMs) {
        super(_blockLengthMs);
    }

    /**
     * Open an audio file to extract chunk of Audio
     *
     * @param _file    An inputStream File to be opened
     */
    public void openAudioInputStream(InputStream _file)
    throws IOException {
    }

    /**
     * Read next one audio block
     *
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
