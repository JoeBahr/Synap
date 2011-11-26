package com.leopal.synap;

import java.util.concurrent.LinkedBlockingQueue;


//TODO: Manage queue length and optimize its size

/**
 * This class allows the udp reception
 * It stores by itself thread elements into a queue that
 * needs to be read by the client
 *
 * Creation information
 * @author nicolas
 *         Date: 21/11/11
 *         Time: 15:34
 */
public class clTransportReceiver extends clTransport {
    private LinkedBlockingQueue<clTransportAudioPacket> rcvQueue = new LinkedBlockingQueue<clTransportAudioPacket>();
    private Thread mThreadListen;

    public clTransportReceiver(){
        super();
        mThreadListen = new Thread(new Runnable() {
            public void run() {
                try{
                    int res=0;
                    while (!Thread.interrupted() && (res!=4)) {
                        res = rReceiveData();
                        //A valid data has been received
                        if (res==1) {
                            rcvQueue.put(mAudioPacket);
                        }
                    }
                } catch (Exception e) {
                    closeCommLink();
                }
            }
        });
    }

    public clTransportAudioPacket getTransportAudioPacket() throws InterruptedException {
        return rcvQueue.take();
    }

    public void start(String _address) {
        if (!mThreadListen.isAlive()) {
            this.commLinkInit(_address);
            mThreadListen.start();
        }
    }

    public void stop() {
        if (!mThreadListen.isAlive()) {
            mThreadListen.interrupt();
        }
    }
}
