/**
 * 
 */
package com.leopal.synap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import android.os.Handler;


public class clSyncClientThread {

	/**
	 * IP Adress of the server
	 */
	private InetAddress dstAddress;
	
	/**
	 * Client socket
	 */
	private Socket openedSocket;
	
	/**
	 * Difference between client date and server date :
	 * Server Date = Client Date + deltaDate;
	 */
	private long deltaDate;
	
	/**
	 * Difference between client date and server date :
	 * isValid ?
	 */
	private boolean deltaValid;
	
	/**
	 * Thread of the client
	 */
	private Thread ClientThread;
	
	/**
	 * Input Stream on socket
	 */
	private InputStream inStream;
	/**
	 * Output Stream on socket
	 */
	private OutputStream outStream;
	/**
	 * Public function to start a NTP client 
	 *
	 * @param dstAddress   IP Address of the NTP server
	 * 
	 * @return if the client thread has been started 
	 *
	 */
	public boolean setServer(InetAddress dstAddress) {
		this.dstAddress = dstAddress;
		setDeltaValid(false);
		ClientThread = new Thread(new Runnable() {
        	public void run() {
        		while(true) {
        			boolean ans;
					ans = connectServer();
					ans = sendRequest();
					do {
						ans = receiveAnswer(false);
					} while (ans == false);
					sendRequest();
					do {
						ans = receiveAnswer(true);
					} while (ans == false);
					closeServer();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        });
		ClientThread.start();
		return true;
				
	}

	/**
	 * Function to build an NTP request  
	 *
	 * @param none
	 * 
	 * @return if a request has been built and sent 
	 *
	 */
	private boolean sendRequest() {
		clSyncNTPMessage _mess = new clSyncNTPMessage();
		_mess.setOriginateDate(new Date().getTime());
		
		return sendToServer(_mess);
	}
	
	/**
	 * Function to send an NTP request to server
	 *
	 * @param syncNTPMessage    NTP message to be sent
	 * 
	 * @return if the message has been sent 
	 *
	 */
	private boolean sendToServer(clSyncNTPMessage syncNTPMessage) {
		ObjectOutputStream objOut;
		try {
			objOut = new ObjectOutputStream(outStream);
			objOut.writeObject(syncNTPMessage);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
		return true;
	}

	// TODO : remove after test
	public Handler mainHandler;
	
	/**
	 * Function to get server answer (if any), and compute time difference
	 * between server and client
	 *
	 * @param update   boolean indicating if the time shall be updated or not
	 * 
	 * @return if a answer has been received and managed 
	 *
	 */
	private boolean receiveAnswer(boolean update) {
		boolean ans = false;
		try {
			ObjectInputStream objIn = new ObjectInputStream(inStream);
			long _time = new Date().getTime();
			clSyncNTPMessage messIn = (clSyncNTPMessage)objIn.readObject();
			long d1 = _time - messIn.getTransmitDate();
			long d2 = messIn.getReceiveDate() - messIn.getOriginateDate();
			if (update) {
				setDeltaDate(d1 - d2);
				setDeltaValid(true);
				
				//mainHandler.sendEmptyMessage(0);

			}
			ans = true;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * Function to get create connection to server
	 *
	 * @param none
	 * 
	 * @return if the socket has been created 
	 *
	 */
	private boolean connectServer() {
		clNetwork networkInfo = new clNetwork();
		try {
			Socket _socket = new Socket(dstAddress, networkInfo.NTP_PORT);
			_socket.setSoTimeout(1000);
			_socket.setTcpNoDelay(true);
			openedSocket = _socket;
			inStream = openedSocket.getInputStream();
			outStream = openedSocket.getOutputStream();
			return true;
			
		} catch (IOException e) {
			return false;
		}		
	}
	
	/**
	 * Function to close connection to server
	 *
	 * @param none
	 * 
	 * @return if the socket has been closed 
	 *
	 */
	private boolean closeServer() {
		try {
			openedSocket.close();
			return true;
			
		} catch (IOException e) {
			return false;
		}		
	}
	
	/**
	 * Function to define delta between server and client time
	 *
	 * @param deltaDate   time difference
	 * 
	 * @return none 
	 *
	 */
	private void setDeltaDate(long deltaDate) {
		this.deltaDate = deltaDate;
	}


	/**
	 * Function to get delta between server and client time
	 *
	 * @param none
	 * 
	 * @return time difference 
	 *
	 */
	// TODO : set private after tests
	public long getDeltaDate() {
		/* TODO : delay 0 or delay max ? */
		long l = 0;
		if (isDeltaValid()){
			l = deltaDate;
		}
		return l;
	}


	/**
	 * Function to declare delta between server and client time as valid
	 *
	 * @param deltaDate   time difference
	 * 
	 * @return none 
	 *
	 */
	private void setDeltaValid(boolean deltaValid) {
		this.deltaValid = deltaValid;
	}


	/**
	 * Function to know if delta between server and client time is valid
	 *
	 * @param deltaDate   time difference
	 * 
	 * @return none 
	 *
	 */
	private boolean isDeltaValid() {
		return deltaValid;
	}

	/**
	 * Public function to get the synchronized time 
	 *
	 * @param none
	 * 
	 * @return time in milliseconds since Jan. 1, 1970 GMT.
	 *
	 */
	public long getTime() {
		Date _date = new Date();
		long _time = _date.getTime();
		_time = _time + getDeltaDate();
		return _time;
	}

    /**
     * Wait until specified timeStamp
     *
     * @param _timeStamp
     */
    public void waitUntilTimeStamp(long _timeStamp) throws InterruptedException {
        long delta = _timeStamp - getTime();
        if (delta>0) Thread.sleep(delta);
    }
}
