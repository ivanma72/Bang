package com.dev.mhacks.bang.ivanma.bang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;


public class BangActivity extends Activity {

    private Button bangButton;
    private TextView bangTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang);


        bangTextView = (TextView)findViewById(R.id.contact_textview);

        bangButton = (Button)findViewById(R.id.bang_button);
        bangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //find  random phone number from contacts
                /*String pnumber = "9896986724";
                String message = "Hello World";
                try {
                    PendingIntent sentPI = PendingIntent.getBroadcast(BangActivity.this, 0, new Intent("SMS_SENT"), 0);
                    SmsManager.getDefault().sendTextMessage(pnumber, null, message, sentPI, null);
                }
                catch(Exception e){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BangActivity.this);
                    AlertDialog dialog = alertDialogBuilder.create();
                    dialog.setMessage(e.getMessage());
                    dialog.show();

                }*/

                fetchContacts();
            }
        });
    }

    public void fetchContacts(){
        String phoneNumber;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        //set up random contact row
        int list_len = cursor.getCount();
        long rand_seed = System.currentTimeMillis();
        Random rnum = new Random(rand_seed);
        int pos = rnum.nextInt(list_len);

        //Get a random person + phone number
        if(cursor.getCount() == 0){
            return;
        }

        cursor.moveToPosition(pos);
        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

        while(hasPhoneNumber == 0){
            pos = rnum.nextInt(list_len);
            hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
        }

        output.append("\n First Name:" + name);
        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
                new String[]{contact_id}, null);

        if(phoneCursor.moveToNext()){
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
            output.append("\n Phone number: " + phoneNumber);
        }
        phoneCursor.close();

        bangTextView.setText(output);
        cursor.close();
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
}
