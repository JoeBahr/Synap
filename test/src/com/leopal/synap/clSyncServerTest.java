package com.leopal.synap;

import junit.framework.TestCase;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: nicolas
 * Date: 05/11/11
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class clSyncServerTest extends android.test.AndroidTestCase {
    public void testStartServer() throws Exception {
        clSyncServerThread classToTest = new clSyncServerThread();
        classToTest.startServer();

        clSyncClientThread classClient = new clSyncClientThread();
        classClient.setServer(InetAddress.getByName("127.0.0.1"));
        Thread.sleep(1000);

        long timeTmp = classClient.getTime();
        assertEquals((int)(timeTmp/100), (int)(classToTest.getTime()/100)); //TODO: See how to ameliorate precision around 10 ms
        //Thread.sleep(1000);
    }
}
