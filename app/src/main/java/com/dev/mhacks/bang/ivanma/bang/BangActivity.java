package com.dev.mhacks.bang.ivanma.bang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.Random;


public class BangActivity extends Activity {

    ContentResolver cr;
    ArrayList<Integer> contacts;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang);

        MySQLiteHelper puDB = new MySQLiteHelper(this);
        puLine pu = new puLine();
        pu.setKey(23);
        pu.setLine("adasda");
        puDB.addLine(pu);

        Log.i("Bang", puDB.getLine(23));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BangActivity.this);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setMessage(puDB.getLine(23));
        dialog.show();

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
                    sendMessage(name + " " + number, "2032463012");
                    Log.i("BangActivity", "sendMessage called on number " + number);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    // do something with the Work number here...
                    break;
            }
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
}
