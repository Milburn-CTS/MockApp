package com.continentaltechsolutions.dell.mockapp.Business;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by DELL on 02-Aug-17.
 */

public class Helper {
    Context mContext;
    Activity mActivity;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public Helper(Context mContext, Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    public void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,Manifest.permission.ACCESS_FINE_LOCATION)){
            //ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
            Toast.makeText(mContext,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    public final double fourdecimalplaces(double value) {
        DecimalFormat newFormat = new DecimalFormat("#.#######");
        double twoDecimal = Double.valueOf(newFormat.format(value));
        return twoDecimal;
    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("loction address", "" + strReturnedAddress.toString());
            } else {
                Log.e("loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("loction address", "Canont get Address!");
        }
        return strAdd;
    }

    //Code to check the state of Internet connection
    public final boolean isInternetOn() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            Toast.makeText(mContext, " Error: " + e, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public int loadSavedPreferences(String spName,String key) {
        SharedPreferences sharedPreferences =  mContext.getSharedPreferences(spName, 0);
        Integer value = sharedPreferences.getInt(key,0);
        return value;
    }

    public  void savePreferences(String key, Integer value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Sp",0); //mContext.getSharedPreferences("Sp", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
