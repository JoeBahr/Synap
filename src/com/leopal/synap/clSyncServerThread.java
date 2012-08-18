/**
 * 
 */
package com.leopal.synap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class clSyncServerThread implements Runnable {

	/**
	 * Server socket
	 */
	private ServerSocket openedServerSocket;
	/**
	 * Client socket
	 */
	private Socket openedSocket;
	/**
	 * Input Stream on socket
	 */
	private InputStream inStream;
	/**
	 * Output Stream on socket
	 */
	private OutputStream outStream;
	/**
	 * Thread created to handle dialog with one client
	 */
	private Thread ServerThread;
	
	
	/**
	 * Define input socket for server thread and attach in and out streams
	 *
	 * @param _socket    socket created by the server socket when a connection
	 *                  is required by client
	 *
	 * @return 
	 *
	 */
	private void setOpenedSocket(Socket _socket){
		openedSocket = _socket;
		try {
			openedSocket.setTcpNoDelay(true);
			inStream = openedSocket.getInputStream();
			outStream = _socket.getOutputStream();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to get client request (if any), and answer with required times 
	 *
	 * @return if a request has been received and managed
	 *
	 */
	private boolean answerRequest() {
		boolean ans = false;
		try {
			/* ! creation of an ObjectInputStream is a blocking call ! */
			/* ! if none received within timeout delay, exception raised ! */
			ObjectInputStream objIn = new ObjectInputStream(inStream);
			long _time = new Date().getTime();
			clSyncNTPMessage messIn = (clSyncNTPMessage)objIn.readObject();
			clSyncNTPMessage messOut = new clSyncNTPMessage();
			messOut.setReceiveDate(_time);
			messOut.setOriginateDate(messIn.getOriginateDate());
			messOut.setTransmitDate(new Date().getTime());
			ans=sendToClient(messOut);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ans;
	}

	/**
	 * Function to send a NTP answer to client 
	 *
	 * @param syncNTPMessage    NTP message to be sent
	 * 
	 * @return if the message has been sent 
	 *
	 */
	private boolean sendToClient(clSyncNTPMessage syncNTPMessage) {
		ObjectOutputStream objOut;
		try {
			objOut = new ObjectOutputStream(outStream);
			objOut.writeObject(syncNTPMessage);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	/**
	 * Public function to start a NTP server 
	 *
	 * @return if the server thread has been started
	 *
	 */
	public boolean startServer() {
		clNetwork networkInfo = new clNetwork();
		try {
			ServerSocket _Socket = new ServerSocket(networkInfo.NTP_PORT);
			openedServerSocket = _Socket;
			ServerThread = new Thread(new Runnable() {
	        	public void run() {
	        		while(true) {
	        			try {
		    				Socket _socket = openedServerSocket.accept();
		    				clSyncServerThread serverThread = new clSyncServerThread();
		    				serverThread.setOpenedSocket(_socket);
		    				Thread t = new Thread(serverThread);
		    				t.start();

		    			} catch (IOException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
	        		}
	        	}
	        });
			ServerThread.start();
			return true;
			
		} catch (IOException e) {
			return false;
		}		
	}
	
	/**
	 * Runnable function to handle a connection with one client
	 * Answer to both client request and close communication 
	 *
	 * @return none
	 *
	 */
	@Override
	public void run() {
		boolean ans;
		do {
			ans = answerRequest();
		} while (ans == false);
		do {
			ans = answerRequest();
		} while (ans == false);
		try {
			openedSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Public function to get the synchronized time 
	 *
	 * @return time in milliseconds since Jan. 1, 1970 GMT.
	 *
	 */
	public long getTime() {
		Date _date = new Date();
		long _time = _date.getTime();
		return _time;
	}

}
