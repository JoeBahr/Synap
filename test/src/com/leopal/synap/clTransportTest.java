package com.leopal.synap;

import android.util.Log;


public class clTransportTest extends android.test.AndroidTestCase {

	/**
	 * Simple test case to check init and close functions
	 *
	 */	 
	public void testInitCloseFunctions() throws Exception {
	        
		clTransport classToTest = new clTransport();
		Log.d("test", "debug");
		short loopbackAddr  = classToTest.commLinkInit("127.0.0.1");
		short loopbackClose = classToTest.closeCommLink();
		short multicastAddr = classToTest.commLinkInit("239.1.1.1");
		short multicastClose = classToTest.closeCommLink();
		
		assertEquals(clTransport.COMM_LINK_ADDR_ERROR, loopbackAddr);
		assertEquals(clTransport.COMM_LINK_CLOSE, loopbackClose);
		assertEquals(clTransport.COMM_LINK_OPEN, multicastAddr);
		assertEquals(clTransport.COMM_LINK_CLOSE, multicastClose);
		Log.d("test", "end");
	}
	

	/**
	 * TO BE DEFINED
	 *
	 */	 
	private static final int SLEEP_TIME = 200;	//ms
	private static final String MC_ADD = "224.0.0.1";
	public void testTranportProcedure() throws Exception {	  
			
					 clTransport mySenderTransport = new clTransport();
		 clTransport myReceiverTransport = new clTransport();
		 byte audioData1[] = "Test".getBytes();
		 int timeStamp = 1234;
         int sampleCount = 1235;
	     
		 if((mySenderTransport.commLinkInit(MC_ADD) == clTransport.COMM_LINK_OPEN) && 
				 (myReceiverTransport.commLinkInit(MC_ADD) == clTransport.COMM_LINK_OPEN))
		 {
            Log.d("Sender & Receiver", "commLinkInit OK");

            mySenderTransport.sSendData(audioData1, timeStamp, false, sampleCount);
            Log.d("Sender", "Send cmd");
            Thread.sleep(SLEEP_TIME);
            myReceiverTransport.rReceiveData();
            Log.d("Receiver", "Data received");

            mySenderTransport.closeCommLink();
            Log.d("Sender", "Closed");
            myReceiverTransport.closeCommLink();
            Log.d("Receiver", "Closed");

            assertEquals(mySenderTransport.mAudioPacket.mStartFrameId, myReceiverTransport.mAudioPacket.mStartFrameId);
            assertEquals(mySenderTransport.mAudioPacket.mSeqNumber, myReceiverTransport.mAudioPacket.mSeqNumber);
            assertEquals(mySenderTransport.mAudioPacket.mDataLength, myReceiverTransport.mAudioPacket.mDataLength);
            assertEquals(mySenderTransport.mAudioPacket.mTimeStamp, myReceiverTransport.mAudioPacket.mTimeStamp);
            assertEquals(mySenderTransport.mAudioPacket.mCs, myReceiverTransport.mAudioPacket.mCs);
            assertEquals(mySenderTransport.mAudioPacket.mAudioDataSampleCount, myReceiverTransport.mAudioPacket.mAudioDataSampleCount);
		 }
		 else
		 {
			 Log.d("Sender & Receiver", "commLinkInit KO");
			 assertEquals(1, 0);
		 }
		 
		 
	}
}
