package com.leopal.synap;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: nicolas
 * Date: 05/11/11
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class clSyncServerTest extends android.test.AndroidTestCase {
    public void testStartServer() throws Exception {
        clSyncServer classToTest = new clSyncServer();
        classToTest.startServer();

        clSyncClient classClient = new clSyncClient();
        Thread.sleep(1000);
        classClient.requestServer("127.0.0.1", 3000);

        Thread.sleep(1000);
        classToTest.stopServer();
    }
}
