package com.leopal.synap;

import android.util.Log;

/**
 * ******************************************
 * THIS CLASS IS REPLACED BY StreamerService but still used for TEST
 *
 * This class manage the streaming of a contentIn
 *
 * @author nicolas
 *         Date: 17/11/11
 *         Time: 17:39
 */
public class clStreamer {
    /** Logging TAG*/
    private static final String TAG = "clStreamer";

    /**
     * The source where content will be read
     */
    private clContentIn pv_contentIn;

    /**
     * The NTP Server
     */
    private clSyncServerThread pv_syncServer;

    /**
     * The IP Transport
     */
    private clTransport pv_transport;

    /**
     * IP/Multicast address for audio packet
     */
    private String pv_contentStringInet;

    /**
     * Thread for main loop of streaming
     */
    private Thread pv_threadMainLoop;

    /**
     * Constructor
     */
    public clStreamer() {
        pv_syncServer = new clSyncServerThread();
        pv_transport = new clTransport();
        pv_syncServer.startServer();//TODO Move in start/stop when clSync manage stop
    }

    public void setContentIn(clContentIn pv_contentIn) {
        stop();
        this.pv_contentIn = pv_contentIn;
    }

    /**
     * IP/Multicast address where to send audio packet
     *
     * @param _Inet    String of the multicast address Ex "239.1.1.1"
     */
    public void setContentInet(String _Inet) {
        this.pv_contentStringInet = _Inet;
    }

    public String getContentInet() {
        return pv_contentStringInet;
    }

    public void start() {
        if (pv_transport.commLinkInit(pv_contentStringInet)==clTransport.COMM_LINK_OPEN)
            if (!pv_contentIn.isEndOfContent()) {
                pv_threadMainLoop = new Thread(new Runnable() {
                    public void run() {
                        //Number of Sample read
                        int sampleCount;
                        //End request
                        boolean endRequested = false;
                        //Prepare the buffer
                        byte[] buf = pv_contentIn.getAudioBlockBuffer();
                        int blockLength = pv_contentIn.getBlockLengthMs();
                        //int sampleCount = pv_contentIn.getPcmFormat().
                        //Prepare playout in xx sec
                        long timeStamp = pv_syncServer.getTime()+1000; //TODO Transform this value as a parameter

                        //First Send
                        sampleCount = pv_contentIn.readNextAudioBlock(buf);
                        pv_transport.sSendData(buf,timeStamp,true, sampleCount);
                        timeStamp+=blockLength;
                        //second Send to get advance
                        //pv_contentIn.readNextAudioBlock(buf);
                        //pv_transport.sSendData(buf, timeStamp, false);
                        //timeStamp += blockLength;

                        //Loop to read content while there is data
                        while( (!pv_contentIn.isEndOfContent())
                                && (!endRequested) ) {
                            sampleCount = pv_contentIn.readNextAudioBlock(buf);
                            pv_transport.sSendData(buf,timeStamp,false, sampleCount);
                            timeStamp+=blockLength;

                            //Log.i(TAG, "Send Samples " + sampleCount);

                            if (Thread.interrupted()) {
                                endRequested = true;
                            } else {
                                try {
                                    //add a rateshaper algorithm better than that.../2 : Replaced by StreamerService
                                    Thread.sleep(blockLength/2);
                                } catch (InterruptedException e) {
                                    endRequested = true;
                                }
                            }
                        }
                    }
                });
                pv_threadMainLoop.start();
            }
    }

    public void stop() {
        if (pv_threadMainLoop !=null) {
            pv_threadMainLoop.interrupt();
            try {
                pv_threadMainLoop.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        this.pv_transport.closeCommLink();
    }
}
