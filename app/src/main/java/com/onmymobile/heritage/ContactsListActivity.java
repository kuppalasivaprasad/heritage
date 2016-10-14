package com.onmymobile.heritage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by onmymobile on 18/8/16.
 */

public class ContactsListActivity extends AppCompatActivity {
    ListView contacts;
    List<String> contactsList;
    InputStream inputStream = null;
    List<String> cont;
    TextView noContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        noContacts=(TextView)findViewById(R.id.nocontacts);

        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contactsList = new ArrayList<>();
        cont = new ArrayList<>();
        contacts = (ListView) findViewById(R.id.contact_list);
        try {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/Heritage");
            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();
            final String filename = folder.toString() + "/" + "contacts.csv";
            File file = new File(filename);
            FileWriter fileWriter = null;
            if (!file.exists()) {
                boolean var1 = false;
                file.getParentFile().mkdirs();
                var1 = file.createNewFile();

                //Runtime.getRuntime().exec("chmod 777 " + filename);
                Log.e("var", "" + var1);
                fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write("Numbers");
                bw.newLine();
                bw.close();
            }

            inputStream = new FileInputStream(file);

            Log.e("input", "" + inputStream);
            CSVFile csvFile = new CSVFile(inputStream);
            contactsList = csvFile.read();
            Log.e("phone", "" + contactsList);
            Collections.reverse(contactsList);
            for (int i=0; i<contactsList.size(); ++i) {
                cont.add(contactsList.get(i).toString().substring(3));
            }
            if(cont.size()>0) {
                ContactsAdapter contactsAdapter = new ContactsAdapter(ContactsListActivity.this, R.layout.contact_item, contactsList, cont,noContacts);
                contacts.setAdapter(contactsAdapter);
                contactsAdapter.notifyDataSetChanged();
            }
            else
            {
                noContacts.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add:
                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ContactsListActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View alertlayout = inflater.inflate(R.layout.alert_add, null);
                alertDialog.setView(alertlayout);
                final EditText number = (EditText) alertlayout.findViewById(R.id.contact_add);

                AlertDialog dialog=alertDialog.create();

                alertDialog.setTitle("");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (number.length() > 0) {
                            if (number.length() == 10) {

                                String phNum = number.getText().toString();
                                Log.e("Phone", "number" + phNum);
                                File folder = new File(Environment.getExternalStorageDirectory()
                                        + "/Heritage");
                                boolean var = false;
                                if (!folder.exists())
                                    var = folder.mkdir();

                                System.out.println("" + var);
                                //String msgDate=Preferences.getString(Preferences.PrefType.msg_date,context);
                                final String filename = folder.toString() + "/" + "contacts.csv";


                                Log.e("filename", "" + filename);

                                File file = new File(filename);


                                FileWriter fileWriter = null;
                                try {


                                    if (!file.exists()) {

                                        boolean var1 = false;
                                        file.getParentFile().mkdirs();
                                        var1 = file.createNewFile();
                                        Log.e("var", "" + var1);
                                        fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                                        BufferedWriter bw = new BufferedWriter(fileWriter);
                                        bw.write("Numbers");
                                        bw.newLine();
                                        bw.close();
                                    } else {
                                        fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                                        BufferedWriter bw = new BufferedWriter(fileWriter);
                                        bw.write(phNum);
                                        bw.newLine();
                                        bw.close();
                                        Toast.makeText(ContactsListActivity.this, "Contact Added !", Toast.LENGTH_SHORT).show();
                                        Intent myintent = new Intent(ContactsListActivity.this, ContactsListActivity.class);
                                        startActivity(myintent);
                                        finish();
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                Toast.makeText(ContactsListActivity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ContactsListActivity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });


                //alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alertDialog.show();


                return true;


        }


        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

}
