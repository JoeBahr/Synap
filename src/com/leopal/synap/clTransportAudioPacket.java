package com.leopal.synap;

public class clTransportAudioPacket implements java.io.Serializable {
	
	/* A serializable class needs a version number used during deserialization to verify
	 that the sender and receiver of a serialized object have loaded classes for that object 
	that are compatible with respect to serialization */
	private static final long serialVersionUID = 6L;	
	private static final int START_FRAME_ID = 123456789;
	
	int getStartFrameId()
	{
		return START_FRAME_ID;
	}
	
	int mStartFrameId = START_FRAME_ID;
	int mDataLength = 0;
	int mSeqNumber = 0;
	int mTimeStamp = 0;
	byte mAudioData[];
	int mCs = 0;
}
