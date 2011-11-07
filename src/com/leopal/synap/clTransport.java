package com.leopal.synap;

/**
 * This class is the transport layer of the synap application based on the UDP protocol
 *  
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.util.Log;

public class clTransport {
	//Constant definition section
	private static final int CST_FRAME_SIZE = 4*5;		//Int is 32bits long
	
	private static final int TR_PORT = 3123;
	
	clTransportAudioPacket mAudioPacket = new 	clTransportAudioPacket();
	
	private InetAddress mGroup;
	private MulticastSocket mSocket;
	private boolean commLinkInit = false;
	private boolean mRcvCsErr = false;
	private boolean mRcvStartFrameIdErr = false;
		
	public clTransport(){
		mAudioPacket.mSeqNumber = 0;
		mAudioPacket.mCs = 0;
		mRcvCsErr = false;
		mRcvStartFrameIdErr = false;
		mAudioPacket.mTimeStamp = 0;
	}
	
	/**
	 * commLinkInit() is used to initialize a communication link.
	 * 
	 *  
	 * @param String multiCastAd Targeted multicast address    
	 *                            	 
	 * @return 	true => initialization OK
	 * 			false => initialization KO
	 *
	 */	
	boolean commLinkInit(String multiCastAd){
		try
		{
			mGroup = InetAddress.getByName(multiCastAd);
			mSocket = new MulticastSocket(TR_PORT);
			mSocket.joinGroup(mGroup);
			commLinkInit = true;
		}
		catch(Exception e)
		{
			Log.e("commLinkInit()", "Error", e);	
			commLinkInit = false;
		}
		 return commLinkInit;
	}

	/**
	 * closeCommLink() is used to close an opened communication link.
	 * 
	 *  
	 * @param String multiCastAd Targeted multicast address    
	 *                            	 
	 * @return 	true => initialization OK
	 * 			false => initialization KO
	 *
	 */	
	void closeCommLink(){
		if(commLinkInit){
			try
			{
				mSocket.leaveGroup(mGroup);
			}
			catch(Exception e)
			{
				Log.e("commLinkInit()", "Error", e);	
				commLinkInit = false;
			}
		}
	}
	
	/**
	 * sSendData() is used by the sender to send data to a group if a communication link is established.
	 * 
	 *  
	 * @param byte[] buf Audio packet to send     
	 * @param byte[] int TimeStamp related to the audio packet
	 * @param boolean firstAudioTrasnfer  	true => a new file transfer begins
	 * 										false => mSeqNumber is incremented
	 *                            	 
	 * @return 	true => initialization OK
	 * 			false => initialization KO
	 *
	 */		
	boolean sSendData(byte[] buf, int timeStamp, boolean firstAudioTransfer){
		boolean returnValue = false;
		if(commLinkInit)
		{
			preparePacketToSend(buf, timeStamp, firstAudioTransfer); 
			try
			{
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectOutputStream outStreamObject = new ObjectOutputStream(outStream);
				
				outStreamObject.writeObject(mAudioPacket);
				outStreamObject.close();
									
				DatagramPacket packetToSend = new DatagramPacket(outStream.toByteArray(), outStream.toByteArray().length, mGroup, TR_PORT);
				mSocket.send(packetToSend);
				returnValue = true;
			}
			catch(Exception e)
			{
				Log.e("sSendData()", "Error", e);
				returnValue = false;
			}
		}
		return returnValue;
	}
	
	/**
	 * rReceiveData() is used by the receiver to read data from a group if a communication link is established.
	 * 
	 *  
	 * @param TO BE DONE
	 *     	 
	 * @return  int value	0	-> commLinkInit missing 
	 * 						1	-> Rcv Ok
	 * 						2	-> Start Frame Id error
	 * 						3 	-> CS error
	 *						4   -> Exception detected			
	 */
	int rReceiveData(){
		int returnValue = 0;
		
		if(commLinkInit)
		{
			returnValue = 1;
			try
			{
				byte[] buf = new byte[65535];				
				ByteArrayInputStream inputStream  = new ByteArrayInputStream(buf);
				DatagramPacket rcvPacket = new DatagramPacket(buf, buf.length);

				mSocket.receive(rcvPacket);
				
				ObjectInputStream inputObject = new ObjectInputStream(inputStream);
				mAudioPacket = (clTransportAudioPacket)inputObject.readObject();
				
				//tests on rcv Data
				mRcvStartFrameIdErr = false;
				mRcvCsErr = false;
				if(mAudioPacket.mStartFrameId != mAudioPacket.getStartFrameId())
				{
					Log.d("Rcv", "Start Frame Id OK");
					mRcvStartFrameIdErr = true;
					returnValue = 2;
				}
				if(mAudioPacket.mCs != computeCs(mAudioPacket.mAudioData, mAudioPacket.mAudioData.length))
				{
					Log.d("Rcv", "CS KO");
					mRcvCsErr = true;
					returnValue = 3;
				}	
			}
			catch(Exception e)
			{
				Log.e("sSendData()", "Error", e);
				returnValue = 4;
			}
		}
		return returnValue;
	}
	
	/**
	 * preparePacketToSend() is a private function called before sending a UDP packet
	 * It fills the clTransportAudioPacket before being serialized 
	 * mSeqNumber is automatically incremented within the same audio file
	 * To initialize a new audio file transfer this variable is set to 0 through the firstAudioTrasnfer parameter
	 * 
	 * Packet structure:
	 * 
	 * [START_ID][FRAME_LENGTH][SEQ_NUMBER][TIME_STAMP][AUDIO_DATA][CHECKSUM]
	 * 
	 * [START_ID] int (4 Bytes)  mStartFrameId class clTransportAudioPacket
	 * [FRAME_LENGTH] int (4 Bytes)  mDataLength class clTransportAudioPacket
	 * [SEQ_NUMBER] int (4 Bytes)  mSeqNumber class clTransportAudioPacket
	 * [TIME_STAMP] int (4 Bytes)  mTimeStamp class clTransportAudioPacket
	 * [AUDIO_DATA] buf (n Bytes)  mAudioData class clTransportAudioPacket
	 * [CHECKSUM]	int (4 Bytes)  mCs		  class clTransportAudioPacket	(compute on mAudioData only in this version)
	 *   
	 * @param byte[] buf Audio packet to send     
	 * @param byte[] int TimeStamp related to the audio packet
	 * @param boolean firstAudioTrasnfer  	true => a new file transfer begins
	 * 										false => mSeqNumber is incremented
	 * 
	 * @return byte[]	the packet ready to send
	 *
	 */
	void preparePacketToSend(byte[] buf, int timeStamp, boolean firstAudioTransfer) {
		
		if(firstAudioTransfer)
		{
			mAudioPacket.mSeqNumber = 0;
		}
		else
		{
			mAudioPacket.mSeqNumber++;
		}
		
		//[START_ID] set by default
		mAudioPacket.mDataLength = buf.length + CST_FRAME_SIZE;		//[FRAME_LENGTH]
		//[SEQ_NUMBER] set above
		mAudioPacket.mTimeStamp = timeStamp;//[TIME_STAMP]
		mAudioPacket.mAudioData = buf;		//[AUDIO_DATA] 
		mAudioPacket.mCs = computeCs(buf, buf.length);
	}
	
	/**
	 * computeCs() is a private function called to compute a CS an an array
	 *   
	 * @param byte[] buf The array on which the CS is computed
	 * @param byte[] length  number of bytes from buf used for computation (from buf[0]to buf[length])     
	 * 
	 * @return int the check sumvalue	the packet ready to send
	 *
	 */	
	int computeCs(byte[] buf, int length){
		int cs = 0;
		
		if(buf.length <= length){
		
			for(int i = 0; i < length; i++){
				cs += (int)buf[i];
			}
		}
		//To be adapted in case of buf.length > length
		//Code error to return
		return cs;
	}
}



