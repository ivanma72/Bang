package com.dev.mhacks.bang.ivanma.bang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class BangActivity extends Activity {

    private Button bangButton;
    private ImageButton settingsButton;
    private ImageButton infoButton;
    private ImageButton shareButton;

    private TextView outputTxt;

    ContentResolver cr;
    ArrayList<Integer> contacts;
    Cursor cursor;
    private static MySQLiteHelper puDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang);

        //instantiate views
        bangButton = (Button)findViewById(R.id.bang_button);
        settingsButton = (ImageButton)findViewById(R.id.settings_button);
        infoButton = (ImageButton)findViewById(R.id.info_button);
        shareButton = (ImageButton)findViewById(R.id.share_button);
        outputTxt = (TextView)findViewById(R.id.outputTxt);

        //do animation
        fadeIn();

        puDB = new MySQLiteHelper(this);

        contacts = new ArrayList<Integer>();

        cr = getContentResolver();
        cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

            while (phones.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        if(number.length() >= 10)
                            contacts.add(cursor.getPosition());
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // do something with the Work number here...
                        break;
                }
            }
            phones.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bang, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void bang(View view) {
        Random r = new Random();
        Integer idx = r.nextInt(contacts.size());

        if (!cursor.moveToPosition(contacts.get(idx))) {
            return;
        }

        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        boolean flag = false;
        while (phones.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number = number.replaceAll("[^\\d.]", "");
            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    // do something with the Home number here...
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    // do something with the Mobile number here...
                    String message = findMessage();
                    sendMessage(message, "3152562973");//input number for random Number
                    printMessage(message, name);
                    Log.i("BangActivity", "sendMessage called on number " + number);
                    flag = true;
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    // do something with the Work number here...
                    break;
            }

            if(flag) break;
        }
        phones.close();
    }

    private void sendMessage(String message, String number) {
        try {
            PendingIntent pi = PendingIntent.getBroadcast(BangActivity.this, 0, new Intent("SMS_SENT"), 0);
            SmsManager.getDefault().sendTextMessage(number,null, message, pi, null);
            Log.i("BangActivity", "sendMessage end of try block");
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BangActivity.this);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
        }
    }

    public void fadeIn(){

        Animation faderight = AnimationUtils.loadAnimation(this, R.anim.fade_right);
        Animation fadeleft = AnimationUtils.loadAnimation(this, R.anim.fade_left);
        Animation fadetop = AnimationUtils.loadAnimation(this, R.anim.fade_top);
        Animation fadebottom = AnimationUtils.loadAnimation(this, R.anim.fade_bottom);
        bangButton.startAnimation(fadetop);
        settingsButton.startAnimation(fadeleft);
        infoButton.startAnimation(fadebottom);
        shareButton.startAnimation(faderight);

    }

    private String findMessage(){
        String puLine;
        Random r = new Random();
        long idx = r.nextInt((int) puDB.getSize());
        puLine = puDB.getLine(idx);
        return puLine;
    }

    public void info(View view){
        ContextThemeWrapper themedContext;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            themedContext = new ContextThemeWrapper( BangActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar );
        }
        else {
            themedContext = new ContextThemeWrapper( BangActivity.this, android.R.style.Theme_Light_NoTitleBar );
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);
        builder.setView(getLayoutInflater().inflate(R.layout.info_alert, null));
        builder.setPositiveButton(R.string.close, null);
        builder.create();

        builder.show();
    }

    //activated when share button is clicked
    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "You should go Bang! your friends!");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    //animation for fade in text animation when bang button pressed
    private void printMessage(String message, String contactId){
        outputTxt.setClickable(true);
        outputTxt.setText( contactId + "\n\n" + message);
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        anim.setDuration(2000);
        outputTxt.startAnimation(anim);
        return;
    }

    //if user clicks text, it goes away
    public void fadeText(View v){
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        anim.setFillAfter(true);
        anim.setDuration(1000);
        outputTxt.startAnimation(anim);
        outputTxt.setClickable(false);

    }
}
