package com.leopal.synap;

import android.util.Log;
import java.util.Date;

/**
 * This class is made to limit the Bitrate of a stream.
 * The analysis is made over a harcoded period of time.
 * This period will be split in x(static) counter="a subperiod of the main period"
 * Each counter is then filled with the quantity of data sent
 * This table is used with a sliding window to determine the instantaneous bitrate sent.
 *
 * Creation information
 *
 * @author nicolas
 *         Date: 06/05/12
 *         Time: 18:52
 */
public class clRateLimiting {
    private static final String TAG = "clRateLimiting";

    /**
     * The analysis will be done on x ms
     * This period is split in x subperiod
     */
    private static final int pv_MainPeriodInMs = 1000;
    private static final int pv_NumberOfSubperiod = 10;

    /**
     * Wait time between two loop of rate shaping test when dataslot not available
     */
    private static final int pv_SleepLoopMs = pv_MainPeriodInMs/pv_NumberOfSubperiod/10;

    /**
     * This table holds the quantity of data sent during each subPeriod
     * The slot that was modified
     */
    private long[] pv_TableDataSent;
    private int   pv_TableLastSlotNumber;
    private long  pv_TableLastSlotTime;
    private static final int pv_TableSlotDuration=pv_MainPeriodInMs/pv_NumberOfSubperiod;

    private int pv_MaximumBitrate = 0;

    public clRateLimiting() {
        pv_TableDataSent = new long[pv_NumberOfSubperiod];
    }

    public void setMaximumBitrate(int _bitrate) {
        pv_MaximumBitrate = _bitrate;
    }

    /**
     * Blocks until it is possible to send this data quantity without breaking Maximum Bitrate rule
     * simulateBitrate with new data
     *   If possible -> unlock
     *   If databitsize>defined max datarate -> unlock
     *   If not wait xx ms and loop
     * @param _dataBitSize  Size of the datachunk to send
     * @return true   Can send data
     *         false  Can not send data
     */
    public boolean isReadyToSend(long _dataBitSize) {
        long currentBitrate;
        boolean notFirstLoop=false;
        try {
            do{
                if (notFirstLoop) Thread.sleep(pv_SleepLoopMs);
                calculateTableSlot();
                currentBitrate = calculateDataSentTotalDuringPeriod();
                Log.d(TAG, "Slot= " + String.valueOf(pv_TableLastSlotNumber)+" -Time= "+String.valueOf(pv_TableLastSlotTime));
                Log.d(TAG, "Bitrate= " + String.valueOf(currentBitrate));

                if (!notFirstLoop) notFirstLoop = true;
            } while (((currentBitrate + _dataBitSize)>=pv_MaximumBitrate) && (_dataBitSize<pv_MaximumBitrate) );
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * This insert into the table that some data was sent
     * @param _dataBitSize  Size of the datachunk that was sent
     * @param _newTime      It was sent at...
     */
    public void setDataSent(long _dataBitSize, long _newTime) {
        if (_newTime==-1) {
            calculateTableSlot();
        } else {
            calculateTableSlot(_newTime);
        }
        pv_TableDataSent[pv_TableLastSlotNumber]+= _dataBitSize;
    }

    /**
     * This insert into the table that some data was sent
     * @param _dataBitSize  Size of the datachunk that was sent
     */
    public void setDataSent(long _dataBitSize) {
        setDataSent(_dataBitSize, -1);
    }

    /**
     * Determine the Table Slot according to current time
     */
    private void calculateTableSlot() {
        Date _date = new Date();
        calculateTableSlot(_date.getTime());
    }

    /**
     * Calculate the Table Slot Number according to new given time
     * @param _newTime  The current time of evaluation in comparison with previous one (Millisecond)
     *                  This is only used for absolute calculation, no need to be the real time (HMS MS).
     */
    private void calculateTableSlot(long _newTime) {
        long nbSlot = (_newTime - pv_TableLastSlotTime)/pv_TableSlotDuration;
        //If we have more jump, it means we have to reset the whole buffer only once.
        if (nbSlot>pv_NumberOfSubperiod) nbSlot= pv_NumberOfSubperiod;
        if (nbSlot>0) {
            //Set time
            pv_TableLastSlotTime= _newTime;
            for (long i=0; i<nbSlot; i++) {
                pv_TableLastSlotNumber++;
                //Have we looped within the Table
                if (pv_TableLastSlotNumber==(pv_NumberOfSubperiod)) pv_TableLastSlotNumber=0;
                pv_TableDataSent[pv_TableLastSlotNumber] = 0;
            }
        }
    }

    /**
     * Calculate the total of data sent during the period
     * @return bit number sent
     */
    public long calculateDataSentTotalDuringPeriod() {
        long tot=0;
        for (int i=0; i<(pv_NumberOfSubperiod); i++) {
            tot += pv_TableDataSent[i];
        }
        return tot;
    }

    public long testCalculateTableSlot(long _newTime) {
        calculateTableSlot(_newTime);
        return pv_TableLastSlotNumber;
    }
}
