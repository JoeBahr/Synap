package com.leopal.synap;

/**
 * Root class for content Playout
 * Usage:
 *   Setup play parameter
 *   Queue content
 *   Start play
 *
 * Creation information
 * @author nicolas
 * Date: 17/11/11
 * Time: 16:56
 */
public class clContentOut extends clContent {

    /**
     * Initialize the class and define the size of a block
     *
     * @param _blockLengthMs Size of a block in ms
     */
    public clContentOut(int _blockLengthMs) {
        super(_blockLengthMs);
    }

    /**
     * Play an audio block
     *
     * @param _data    A byte array containing raw audio data
     * @param _numberOfSample Number of sample in the buffer
     */
    public void queueAudioBlock(byte[] _data, int _numberOfSample)
    {
    }

    /**
     * Start immediate playout of the queue
     */
    public void start()
    {
    }

    /**
     * Stop playout and flush the queue
     */
    public void stop()
    {
    }

}
