package com.leopal.synap;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: laptop
 * Date: 30/12/12
 * Time: 00:52
 * To change this template use File | Settings | File Templates.
 */
public class aboutActivity extends  synapMainClass {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("!!! ACTIVITY TO BE IMPLEMENTED !!!");
        setContentView(tv);
    }
}
