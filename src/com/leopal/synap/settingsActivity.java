package com.leopal.synap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: laptop
 * Date: 31/12/12
 * Time: 08:34
 * To change this template use File | Settings | File Templates.
 */
public class settingsActivity extends  synapMainClass {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

    Button configOkButton = (Button)this.findViewById(R.id.saveIpAddress);
    configOkButton.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View viewParam)
        {
            EditText tmpEdtTxt = (EditText)findViewById(R.id.edit_ipAddress);
            String tmpTxt = tmpEdtTxt.getText().toString();

            //Test how to spread information within the global synap application
            //Custom content provider could be implemented
            Intent intent = new Intent();
            intent.putExtra("IP_ADDR", tmpTxt);
            setResult(RESULT_OK, intent);
            finish();
        }
    });
//        ((TextView)this.findViewById(R.id.title_streamer)).setBackgroundColor(Color.GRAY);
//        ((TextView)this.findViewById(R.id.title_streamer)).setTextColor(Color.WHITE);
    }
}