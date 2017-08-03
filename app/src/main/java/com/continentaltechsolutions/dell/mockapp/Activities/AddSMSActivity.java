package com.continentaltechsolutions.dell.mockapp.Activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.continentaltechsolutions.dell.mockapp.Business.dataDBHelper;
import com.continentaltechsolutions.dell.mockapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddSMSActivity extends AppCompatActivity implements View.OnClickListener {

    Button add;
    EditText nameEditText, numberEditText;
    private Pattern pattern;
    private Matcher matcher;
    private ArrayList<Map<String, String>> mPeopleList;
    private ProgressDialog pDialog;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sms);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        add = (Button) findViewById(R.id.button1);
        nameEditText = (EditText) findViewById(R.id.editText1);
        numberEditText = (EditText) findViewById(R.id.editText2);
        add.setOnClickListener(AddSMSActivity.this);
        mPeopleList = new ArrayList<Map<String, String>>();
        PopulatePeopleList();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.editText2);

        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.cuscont, new String[]{"Name", "Phone", "Type"}, new int[]{R.id.ccontName, R.id.ccontNo, R.id.ccontType});

        mTxtPhoneNo.setAdapter(mAdapter);
        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int arg2, long arg3) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(arg2);
                @SuppressWarnings("unused")
                String name = map.get("Name");
                String number = map.get("Phone");
                mTxtPhoneNo.setText(number);
            }
        });
    }

    public void PopulatePeopleList() {
        pDialog.setMessage("Loading...");
        showDialog();
        mPeopleList.clear();

        Cursor people = AddSMSActivity.this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (people.moveToNext()) {
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String hasPhone = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)) {

                // You know have the number so now query it like this
                Cursor phones = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                while (phones.moveToNext()) {

                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if (numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else if (numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if (numberType.equals("2"))
                        NamePhoneType.put("Type", "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");

                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();

        this.startManagingCursor(people);
        hideDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button1: {

                String name = nameEditText.getText().toString().trim();
                String number = numberEditText.getText().toString().trim();
                if (!isValidName(name)) {
                    nameEditText.setError("Invalid Name");
                }
                if (!isValidNumber(number)) {
                    numberEditText.setError("Invalid Number");
                } else if (isValidName(name) && isValidNumber(number)) {
                    nameEditText.setError(null);
                    numberEditText.setError(null);
                    saveDataToStudentDB(nameEditText.getText().toString(), numberEditText.getText().toString());
                }
            }
        }
    }

    private boolean isValidNumber(String pass) {
        if (pass != null && pass.length() > 9) {
            return true;
        }
        return false;
    }

    // validating name
    private boolean isValidName(String pass) {
        final String NAME_PATTERN = "^[\\p{L} .'-]+$";
        if (pass != null && pass.length() > 2) {
            pattern = Pattern.compile(NAME_PATTERN);
            matcher = pattern.matcher(pass);
            return matcher.matches();
        } else
            return false;
    }

    private void saveDataToStudentDB(String name, String number) {
        dataDBHelper dbOpenHelper = new dataDBHelper(getApplicationContext(), "Data", null, 1);
        //open database in reading/writing mode
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        try {
            if (database != null) {
                // Insert data to DB
                ContentValues data = new ContentValues();
                data.put("NAME", name);
                data.put("PHONE", number);
                database.insert("PEOPLE", null, data);
                // close the connection to database after data is inserted
                database.close();
                Toast.makeText(this, "Data saved succesfully", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
