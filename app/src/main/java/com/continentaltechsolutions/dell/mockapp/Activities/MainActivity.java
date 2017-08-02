package com.continentaltechsolutions.dell.mockapp.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.continentaltechsolutions.dell.mockapp.Business.Helper;
import com.continentaltechsolutions.dell.mockapp.Business.TrackGPS;
import com.continentaltechsolutions.dell.mockapp.Business.dataDBHelper;
import com.continentaltechsolutions.dell.mockapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    EditText edTxtCall, edTxtSMS;
    Button btnCall, btnSMS;
    String phoneNo;
    String message;
    Helper helper;
    TrackGPS gps;
    double Chk_in_longitude;
    double Chk_in_latitude;
    String Chk_in_address;
    boolean sos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new Helper(this, this);
        gps = new TrackGPS(MainActivity.this);
        //Getting the edittext and button instance
        edTxtCall = (EditText) findViewById(R.id.edTxtCall);
        edTxtSMS = (EditText) findViewById(R.id.edTxtSMS);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnSMS = (Button) findViewById(R.id.btnSMS);

        //DEVICEID = helper.loadSavedPreferences("Sp", "DEVICEID");

        //Performing action on button click
        btnCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String number = edTxtCall.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }

        });

        //Performing action on button click
        btnSMS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //sendSMSMessage();
                populatesosData();
                if (sos) {
                    Intent int1 = new Intent(getApplicationContext(), SMSActivity.class);
                    startActivity(int1);
                } else {
                    Intent int1 = new Intent(getApplicationContext(), AddSMSActivity.class);
                    startActivity(int1);
                }

            }

        });
    }

    private boolean populatesosData() {
        dataDBHelper dbOpenHelper = new dataDBHelper(getApplicationContext(),
                "Data", null, 1);

        // open database in reading/writing mode
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

        if (database != null) {
            Cursor cursor;
            cursor = database.query("PEOPLE", null, null, null, null, null,
                    null, null);
            if (cursor != null && cursor.moveToLast()) {
                sos = true;
            }
            // close the connection to database after data is inserted
            database.close();
        } else
            sos = false;
        return sos;
    }

    // Hardware Back Button
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(
                this);
        builder1.setCancelable(false)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alert1 = builder1.create();
        alert1.show();
    }

    //region NOTUSED
    protected void sendSMSMessage() {
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Calendar c = Calendar.getInstance();
        final String formattedDate = df.format(c.getTime());
        setGPSCoordinates();
        phoneNo = edTxtCall.getText().toString();
        String newMsg = edTxtSMS.getText().toString();

        String defaultMsg = "Help me now as I might be in trouble. " + formattedDate + " " + Chk_in_address + " " + "http://maps.google.com/?q=" + Chk_in_latitude + "," + Chk_in_longitude;
        if (newMsg != null)
            message = defaultMsg + " " + newMsg;
        else
            message = defaultMsg;


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
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
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
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void setGPSCoordinates() {
        gps = new TrackGPS(MainActivity.this);
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

    //endregion
}
