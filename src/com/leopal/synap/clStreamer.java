package com.leopal.synap;

/**
 * This class manage the streaming of a contentIn
 *
 * @author nicolas
 * Date: 17/11/11
 * Time: 17:39
 */
public class clStreamer {
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
     * IP/Multicast address where to send audio packet
     */
    private String pv_destinationInet;

    /**
     * Thread for main loop of streaming
     */
    private Thread pv_threadStreamingMainLoop;

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
     * @param pv_destinationInet    String of the multicast address Ex "239.1.1.1"
     */
    public void setDestinationInet(String pv_destinationInet) {
        this.pv_destinationInet = pv_destinationInet;
    }

    public String getDestinationInet() {
        return pv_destinationInet;
    }

    public void start() {
        if (pv_transport.commLinkInit(pv_destinationInet)==clTransport.COMM_LINK_OPEN)
            if (!pv_contentIn.isEndOfContent()) {
                pv_threadStreamingMainLoop = new Thread(new Runnable() {
                    public void run() {
                        //Prepare the buffer
                        byte[] buf = pv_contentIn.getAudioBlockBuffer();
                        int blockLength = pv_contentIn.getBlockLengthMs();
                        //Prepare playout in xx sec
                        long timeStamp = pv_syncServer.getTime()+3000; //TODO Transform this value as a parameter

                        //First Send
                        pv_contentIn.readNextAudioBlock(buf);
                        pv_transport.sSendData(buf,timeStamp,true);
                        timeStamp+=blockLength;
                        //second Send to get advance
                        //pv_contentIn.readNextAudioBlock(buf);
                        //pv_transport.sSendData(buf, timeStamp, false);
                        //timeStamp += blockLength;

                        //Loop to read content while there is data
                        while( (!pv_contentIn.isEndOfContent())
                                && (!pv_threadStreamingMainLoop.isInterrupted()) ) {
                            pv_contentIn.readNextAudioBlock(buf);
                            pv_transport.sSendData(buf,timeStamp,false);
                            timeStamp+=blockLength;

                            try {
                                Thread.sleep(blockLength);
                            } catch (InterruptedException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    }
                });
            }
    }

    public void stop() {
        if (pv_threadStreamingMainLoop!=null) {
            pv_threadStreamingMainLoop.interrupt();
            try {
                pv_threadStreamingMainLoop.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        this.pv_transport.closeCommLink();
    }
}
