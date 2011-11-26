package com.leopal.synap;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * This class the reception of a streaming ContentIn and send it to ContentOut
 * Usage:
 *  The ContentOut is given
 *  The MCastIP Content is given
 *  Start the process that loop RX/ContentOut
 *
 * Creation information
 * @author nicolas
 *         Date: 21/11/11
 *         Time: 16:57
 */
public class clReceiver {
    /** Logging TAG*/
    private static final String TAG = "clReceiverLog";

    /**
     * The destination where content will be send
     */
    private clContentOut pv_contentOut;

    /**
     * The NTP Server
     */
    private clSyncClientThread pv_sync;

    /**
     * The IP Transport
     */
    private clTransportReceiver pv_transport;

    /**
     * Thread for main loop of streaming
     */
    private Thread pv_threadMainLoop;

    /**
     * IP/Multicast address for audio packet
     */
    private String pv_contentStringInet;

    /**
     * IP for server direct connection
     */
    private String pv_serverStringInet;

    public clReceiver() {
        //Start Sync
        pv_sync = new clSyncClientThread();
        //Start Transport
        pv_transport = new clTransportReceiver();
    }

    public void start() {
        try {
            pv_sync.setServer(InetAddress.getByName(pv_serverStringInet)); //TODO Move in start/stop when clSync manage stop
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //Adapt receive buffer size according to output parameters
        pv_transport.setRcvBuffer(pv_contentOut.getBlockLengthMs()*pv_contentOut.getPcmFormat().getOneMsByteSize());
        pv_transport.start(pv_contentStringInet);

        pv_threadMainLoop = new Thread(new Runnable() {
            public void run() {
                Log.i(TAG,"main thread started");
                clTransportAudioPacket packet;
                try {
                    Log.i(TAG,"wait for packet");
                    packet = pv_transport.getTransportAudioPacket();
                    pv_contentOut.queueAudioBlock(packet.mAudioData,packet.mAudioDataSampleCount);
                    pv_sync.waitUntilTimeStamp(packet.mTimeStamp);
                    pv_contentOut.start();
                    while(!Thread.interrupted()) {
                        packet = pv_transport.getTransportAudioPacket();
                        pv_contentOut.queueAudioBlock(packet.mAudioData,packet.mAudioDataSampleCount);
                    }
                } catch (InterruptedException e) {
                    //Packet reception problem
                    e.printStackTrace();
                }
                pv_transport.stop();
                pv_contentOut.stop();
                Log.i(TAG,"main thread stopped");
            }
        });
        pv_threadMainLoop.start();
    }

    public void stop() {
        if (pv_threadMainLoop !=null) {
            if (pv_threadMainLoop.isAlive()) {
                pv_threadMainLoop.interrupt();
                try {
                    pv_threadMainLoop.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        this.pv_transport.closeCommLink();
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

    /**
     * IP address to connect to server
     *
     * @param _Inet    String of the addresse
     */
    public void setServerInet(String _Inet) {
        this.pv_serverStringInet = _Inet;
    }

    public String getServerInet() {
        return pv_serverStringInet;
    }

    public void setContentOut(clContentOut _contentOut){
        stop();
        this.pv_contentOut = _contentOut;
    }
}
