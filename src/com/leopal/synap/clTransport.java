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
	
	public static final int COMM_LINK_CLOSE = 0;
	public static final int COMM_LINK_OPEN = 1;
	public static final int COMM_LINK_ADDR_ERROR = 2;
	public static final int COMM_LINK_EXCEPTION_ERR = 3;
	
	private static final int TR_PORT = 3123;
	
	clTransportAudioPacket mAudioPacket = new 	clTransportAudioPacket();
	private InetAddress mGroup;
	private MulticastSocket mSocket;
	private short mcommLinkInit;
	private boolean mRcvCsErr;
	private boolean mRcvStartFrameIdErr;

	/**
	 * clTransport() class constructor.
	 * 
	 *  
	 * @param None    
	 *                            	 
	 * @return 	None
	 *
	 */	
	public clTransport(){
		mAudioPacket.mSeqNumber = 0;
		mAudioPacket.mCs = 0;
		mRcvCsErr = false;
		mRcvStartFrameIdErr = false;
		mAudioPacket.mTimeStamp = 0;
		mcommLinkInit = COMM_LINK_CLOSE;
		mRcvCsErr = false;
		mRcvStartFrameIdErr = false;
	}
	
	/**
	 * commLinkInit() is used to initialize a communication link.
	 * 
	 *  
	 * @param String multiCastAd: Targeted multicast address    
	 *                            	 
	 * @return 	COMM_LINK_CLOSE => initialization not performed
	 * 			COMM_LINK_OPEN => initialization performed and OK
	 * 			COMM_LINK_ADDR_ERROR => the address is not a multicast address
	 * 			COMM_LINK_EXCEPTION_ERR => An exception is returned by a socket function
	 *
	 */	
	public short commLinkInit(String multiCastAd){
		mcommLinkInit = COMM_LINK_CLOSE;
		try
		{
			mGroup = InetAddress.getByName(multiCastAd);
			if(mGroup.isMulticastAddress()) 						//multicast address only
			{
				mSocket = new MulticastSocket(TR_PORT);
				mSocket.joinGroup(mGroup);
				mcommLinkInit = COMM_LINK_OPEN;
			}
			else
			{
				mcommLinkInit = COMM_LINK_ADDR_ERROR;
			}
		}
		catch(Exception e)
		{
			Log.e("mcommLinkInit()", "Error", e);	
			mcommLinkInit = COMM_LINK_EXCEPTION_ERR;
		}
		 return mcommLinkInit;
	}

	/**
	 * closeCommLink() is used to close an opened communication link.
	 * 
	 *  
	 * @param None   
	 *                            	 
	 * @return 	COMM_LINK_CLOSE => the operations ends successfully
	 * 			COMM_LINK_EXCEPTION_ERR => An exception is returned by the socket function
	 *
	 */	
	public short closeCommLink(){
		if(mcommLinkInit == COMM_LINK_OPEN)
		{
			try
			{
				mSocket.leaveGroup(mGroup);
				mcommLinkInit = COMM_LINK_CLOSE;
			}
			catch(Exception e)
			{
				Log.e("mcommLinkInit()", "Error", e);	
				mcommLinkInit = COMM_LINK_EXCEPTION_ERR;
			}
		}
		else
		{
			mcommLinkInit = COMM_LINK_CLOSE;
		}
		return mcommLinkInit;
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
	public boolean sSendData(byte[] buf, int timeStamp, boolean firstAudioTransfer){
		boolean returnValue = false;
		if(mcommLinkInit == COMM_LINK_OPEN)
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
	 * @param None
	 *     	 
	 * @return  int value	0	-> mcommLinkInit missing 
	 * 						1	-> Rcv Ok
	 * 						2	-> Start Frame Id error
	 * 						3 	-> CS error
	 *						4   -> Exception detected			
	 */
	public int rReceiveData(){
		int returnValue = 0;
		
		if(mcommLinkInit == COMM_LINK_OPEN)
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
	private void preparePacketToSend(byte[] buf, int timeStamp, boolean firstAudioTransfer) {
		
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
	private int computeCs(byte[] buf, int length){
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



