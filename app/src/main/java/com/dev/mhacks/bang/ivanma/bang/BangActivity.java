package com.dev.mhacks.bang.ivanma.bang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
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
    ArrayList<Integer> contactPositions; //Stores all cursor positions of contacts in contact table
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

        //do startup animation
        startUpAnimate();

        puDB = new MySQLiteHelper(this);
        contactPositions = new ArrayList<Integer>();
        String [] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};

        cr = getContentResolver();
        cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

            while (phones.moveToNext()) {
                //not used at the moment
                //String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        //only store positions of phone numbers that have > 10 digits
                        if(number.length() >= 10)
                            contactPositions.add(cursor.getPosition());
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
    protected void onStart() {
        super.onStart();
        startUpAnimate(); //animations should occur even if user hits home button and re-enters app
    }

    //Bang button callback
    public void bang(View view) {
        Random r = new Random();
        Integer idx = r.nextInt(contactPositions.size());

        if (!cursor.moveToPosition(contactPositions.get(idx))) {
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

    //helper function that sends an a given message to a number via SMS
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

    //helper function for start-up animations
    public void startUpAnimate(){
        bangButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_top));
        settingsButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_left));
        infoButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_bottom));
        shareButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_right));
    }

    //gets a random message from our SQLite DB
    private String findMessage(){
        String puLine;
        Random r = new Random();
        long idx = r.nextInt((int) puDB.getSize());
        puLine = puDB.getLine(idx);
        return puLine;
    }

    //Info button callback that displays AlertDialog
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

    //Share button callback that brings up intent list
    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "You should go Bang! your friends - http://bit.ly/bangapp2014");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    //animation for fade in text animation when bang button pressed
    private void printMessage(String message, String contactId){
        outputTxt.setClickable(true);
        outputTxt.setText( contactId + "\n\n" + message);
        Animation fadein = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadein.setDuration(2000);
        outputTxt.startAnimation(fadein);
    }

    //if user clicks text-> text fades
    public void fadeText(View v){
        Animation fadeout = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeout.setFillAfter(true);
        fadeout.setDuration(1000);
        outputTxt.startAnimation(fadeout);
        outputTxt.setClickable(false);
    }
}
