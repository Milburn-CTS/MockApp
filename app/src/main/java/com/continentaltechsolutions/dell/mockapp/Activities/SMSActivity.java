package com.continentaltechsolutions.dell.mockapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.continentaltechsolutions.dell.mockapp.Business.Helper;
import com.continentaltechsolutions.dell.mockapp.Business.TrackGPS;
import com.continentaltechsolutions.dell.mockapp.Business.dataDBHelper;
import com.continentaltechsolutions.dell.mockapp.Business.sosmsg;
import com.continentaltechsolutions.dell.mockapp.Business.sosmsgAdapter;
import com.continentaltechsolutions.dell.mockapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SMSActivity extends AppCompatActivity {
    private static final String TAG = "SMSActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    List<sosmsg> sosmsgList = new ArrayList<sosmsg>();
    List<String> pn = new ArrayList<String>();
    List<String> nm = new ArrayList<String>();
    ListView listView1;
    sosmsgAdapter adapter;
    String phoneNo;
    String message;
    Helper helper;
    TrackGPS gps;
    double Chk_in_longitude;
    double Chk_in_latitude;
    String Chk_in_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        helper = new Helper(this, this);
        gps = new TrackGPS(SMSActivity.this);

        Refresh();

        registerForContextMenu(listView1);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView
                Toast.makeText(SMSActivity.this, pn.get(position).toString(), Toast.LENGTH_SHORT).show();
                sendSMSMessage(pn.get(position).toString());
            }
        };

        // Setting the item click listener for the listview
        listView1.setOnItemClickListener(itemClickListener);

        Button sendtoall = (Button) findViewById(R.id.buttonl);
        sendtoall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < pn.size(); i++) {
                    sendSMSMessage(pn.get(i));
                }
            }
        });

    }//End of onCreate

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void Refresh() {
        try {
            pn.clear();
            sosmsgList.clear();

            dataDBHelper dbOpenHelper = new dataDBHelper(this.getApplicationContext(),
                    "Data", null, 1);
            //open database in reading/writing mode
            SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
            //listView1.clearChoices();
            if (database != null) {

                Cursor cursor = database.query("PEOPLE", null, null, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    sosmsgList.add(new sosmsg(cursor.getString(cursor.getColumnIndex("NAME")), cursor.getString(cursor.getColumnIndex("PHONE"))));
                    pn.add(new String(cursor.getString(cursor.getColumnIndex("PHONE"))));
                    nm.add(new String(cursor.getString(cursor.getColumnIndex("NAME"))));
                    while (cursor.moveToNext()) {
                        sosmsgList.add(new sosmsg(cursor.getString(cursor.getColumnIndex("NAME")), cursor.getString(cursor.getColumnIndex("PHONE"))));
                        pn.add(new String(cursor.getString(cursor.getColumnIndex("PHONE"))));
                        nm.add(new String(cursor.getString(cursor.getColumnIndex("NAME"))));
                    }
                }

                database.close();
            }

            adapter = new sosmsgAdapter(getApplicationContext(), R.layout.sosview_row, sosmsgList);
            listView1 = (ListView) findViewById(R.id.list);
            listView1.clearChoices();
            listView1.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error. Please Try Again...", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean populatesosData() {
        dataDBHelper dbOpenHelper = new dataDBHelper(getApplicationContext(),
                "Data", null, 1);

        //open database in reading/writing mode
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

        if (database != null) {
            Cursor cursor;
            cursor = database.query("PEOPLE", null, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToLast()) {
                return true;
            }
            // close the connection to database after data is inserted
            database.close();
        }
        ;
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSMS:
                Toast.makeText(this, "Add Phone Number to SMS", Toast.LENGTH_SHORT).show();
                Intent int1 = new Intent(getApplicationContext(), AddSMSActivity.class);
                startActivity(int1);
                break;
        }
        return true;
    }

    protected void sendSMSMessage(String txtphoneNo) {
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Calendar c = Calendar.getInstance();
        final String formattedDate = df.format(c.getTime());
        setGPSCoordinates();
        phoneNo = txtphoneNo;//edTxtCall.getText().toString();
        //String newMsg = edTxtSMS.getText().toString();
        //String defaultMsg =
        message = "Help me now as I might be in trouble. " + formattedDate + " " + Chk_in_address + " " + "http://maps.google.com/?q=" + Chk_in_latitude + "," + Chk_in_longitude;
       /* if (newMsg != null)
            message = defaultMsg + " " + newMsg;
        else
            message = defaultMsg;*/

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            try
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(SMSActivity.this, "SMS sent for " + phoneNo, Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(SMSActivity.this,"SMS failed, please try again for " + phoneNo,Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent for " + phoneNo,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again for " + phoneNo, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void setGPSCoordinates() {
        gps = new TrackGPS(SMSActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }

        if (gps.canGetLocation()) {
            Chk_in_longitude = helper.fourdecimalplaces(gps.getLongitude());
            Log.e("long", String.valueOf(Chk_in_longitude));
            Chk_in_latitude = helper.fourdecimalplaces(gps.getLatitude());
            Log.e("lat", String.valueOf(Chk_in_latitude));
            Chk_in_address = helper.getCompleteAddressString(Chk_in_latitude, Chk_in_longitude);
            //Toast.makeText(HomeActivity.this, "Lat: " + Chk_in_latitude + "Lng: " + Chk_in_longitude + "Addr: " + Chk_in_address, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        @SuppressWarnings("unused")
        String[] names = getResources().getStringArray(R.array.system);
        switch (item.getItemId()) {

            case R.id.delete:
                Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.delete) +
                                " context menu option for " + nm.get((int) info.id),
                        Toast.LENGTH_SHORT).show();

                boolean po = m(nm.get((int) info.id));
                if (po == false) {
                    //getActivity().finish();
                    Log.d(TAG, "po == false");
                }
                Refresh();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    boolean m(String a) {
        dataDBHelper dbOpenHelper = new dataDBHelper(this.getApplicationContext(), "Data", null, 1);
        int i = 0;
        //open database in reading/writing mode
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        database.delete("PEOPLE", "NAME=" + "'" + a + "'", null);
        if (!populatesosData()) {
            Log.d(TAG, "populatesosData != null");
            Intent pm = new Intent(this, MainActivity.class);
            startActivity(pm);
            finish();
            return true;
            //getActivity().finish();
        }
        sosmsgList.clear();
        pn.clear();
        nm.clear();
        if (database != null) {
            Log.d(TAG, "" + i);
            Cursor cursor = database.query("PEOPLE", null, null, null, null, null, null);
            Log.d(TAG, "" + i);
            if (cursor == null) {
                Log.d(TAG, "" + i);
                //	getActivity().finish();
                return false;
            }
            if (cursor != null) {
                cursor.moveToFirst();
                i++;
                sosmsgList.add(new sosmsg(cursor.getString(cursor.getColumnIndex("NAME")), cursor.getString(cursor.getColumnIndex("PHONE"))));
                pn.add(new String(cursor.getString(cursor.getColumnIndex("PHONE"))));
                nm.add(new String(cursor.getString(cursor.getColumnIndex("NAME"))));

                while (cursor.moveToNext()) {
                    i++;
                    sosmsgList.add(new sosmsg(cursor.getString(cursor.getColumnIndex("NAME")), cursor.getString(cursor.getColumnIndex("PHONE"))));
                    pn.add(new String(cursor.getString(cursor.getColumnIndex("PHONE"))));
                    nm.add(new String(cursor.getString(cursor.getColumnIndex("NAME"))));
                }

                if (i == 0) {
                    Log.d(TAG, "" + i);
                }
            }
            database.close();
        }
        adapter.notifyDataSetChanged();
        return true;
    }
}
