package com.leopal.synap;

//import android.content.res.Resources;

/**
 * <Class Usage>
 * <p/>
 * Creation information
 *
 * @author nicolas
 *         Date: 08/05/12
 *         Time: 22:47
 */
public class clRateLimitingTest extends android.test.AndroidTestCase {
    private int iBitrate = 500000;

    public void setUp() throws Exception {

    }

    public void testCalculateTableSlot() throws Exception {
        clRateLimiting classToTest = new clRateLimiting();

        classToTest.setMaximumBitrate(iBitrate);

        assertEquals(0, classToTest.testCalculateTableSlot(0));
        assertEquals(0, classToTest.testCalculateTableSlot(90));
        assertEquals(1, classToTest.testCalculateTableSlot(101));
        assertEquals(2, classToTest.testCalculateTableSlot(201));
        assertEquals(0, classToTest.testCalculateTableSlot(1001));
        assertEquals(0, classToTest.testCalculateTableSlot(3001));

        assertEquals(2, classToTest.testCalculateTableSlot(3001+201));
        assertEquals(2, classToTest.testCalculateTableSlot(3001+201+1001));
    }

    public void testSetDataSent() throws Exception {
        clRateLimiting classToTest = new clRateLimiting();

        classToTest.setMaximumBitrate(iBitrate);

        //Test initial add
        classToTest.setDataSent(1000,0);
        assertEquals(1000, classToTest.calculateDataSentTotalDuringPeriod());

        //Test adding to the same slot
        classToTest.setDataSent(1000,10);
        assertEquals(2000, classToTest.calculateDataSentTotalDuringPeriod());

        //Test cumulative slot
        classToTest.setDataSent(1000,150);
        assertEquals(3000, classToTest.calculateDataSentTotalDuringPeriod());

        //Test more than the period
        classToTest.setDataSent(1000,1151);
        assertEquals(1000, classToTest.calculateDataSentTotalDuringPeriod());
    }

    public void testIsReadyToSend() throws Exception {
        clRateLimiting classToTest = new clRateLimiting();

        classToTest.setMaximumBitrate(iBitrate);

        assertEquals(true, classToTest.isReadyToSend(iBitrate/10));
        assertEquals(true, classToTest.isReadyToSend(iBitrate+10));

        classToTest.setDataSent(iBitrate);
        assertEquals(iBitrate, classToTest.calculateDataSentTotalDuringPeriod());
        assertEquals(true, classToTest.isReadyToSend(iBitrate/10));
        assertEquals(0, classToTest.calculateDataSentTotalDuringPeriod());
    }
}
