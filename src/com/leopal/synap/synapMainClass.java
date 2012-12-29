package com.leopal.synap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: laptop
 * Date: 10/11/12
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */


//!!!! http://android-developers.blogspot.fr/2012/01/say-goodbye-to-menu-button.html

public class synapMainClass extends Activity {
    //Test how to spread information within the global synap application
    String testIpAddr = "";

    /**
     * Manage option Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent myIntent = new Intent(this, settingsActivity.class);
                startActivityForResult(myIntent, 1);
                return true;
            case R.id.help:
                //startActivity(new Intent(this, help.class));
                showMsg("ACTIVITY TO BE IMPLEMENTED");
                return true;
            case R.id.about:
                startActivity(new Intent(this, aboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        testIpAddr = data.getStringExtra("IP_ADDR");
    }

    private void showMsg(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
        toast.show();
    }

}
