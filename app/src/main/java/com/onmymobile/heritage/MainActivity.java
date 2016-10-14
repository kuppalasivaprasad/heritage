package com.onmymobile.heritage;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.onmymobile.heritage.common.MyApplication;
import com.onmymobile.heritage.common.Preferences;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String DEFAULT = "A/N";
    private static String[] PERMISSIONS_CONTACT;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public List phList;
    EditText email, password, reg1, reg2;
    Button save;
    RadioButton real, batch;
    InputStream inputStream=null;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);



        real = (RadioButton) findViewById(R.id.real_button);
        batch = (RadioButton) findViewById(R.id.batch_button);

        String processing = Preferences.getString(Preferences.PrefType.flag, getApplicationContext());
        Log.e("Processing", "" + processing);

        if (processing == null || processing.equals("null") || processing.equals("Y")) {
            real.setChecked(true);
            Preferences.add(Preferences.PrefType.flag, "Y", getApplicationContext());
        } else if (processing.equals("N")) {
            batch.setChecked(true);
            Preferences.add(Preferences.PrefType.flag, "N", getApplicationContext());

        }
        String attachFlag = Preferences.getString(Preferences.PrefType.attachFlag, getApplicationContext());

        //ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);

        String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }


        ((MyApplication) getApplicationContext()).setFlag("N");
        //((MyApplication) getApplicationContext()).setAttachflag("N");


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        reg1 = (EditText) findViewById(R.id.receiver_1);
        reg2 = (EditText) findViewById(R.id.receiver_2);

        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String sEmail = email.getText().toString().trim();
                String sPassword = password.getText().toString().trim();
                String sReg1 = reg1.getText().toString().trim();
                String sReg2 = reg2.getText().toString().trim();

                if (real.isChecked()) {
                    Preferences.add(Preferences.PrefType.flag, "Y", getApplicationContext());
                    Log.e("Application1", "flag" + Preferences.getString(Preferences.PrefType.flag, getApplicationContext()));
                } else {
                    Preferences.add(Preferences.PrefType.flag, "N", getApplicationContext());

                    Log.e("Application", "flag" + Preferences.getString(Preferences.PrefType.flag, getApplicationContext()));
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String date = formatter.format(new Date());
                    Log.e("Date1", "" + date);

                    Preferences.add(Preferences.PrefType.msg_date, date, getApplicationContext());
                }

                if (sEmail.equals("") || sPassword.equals("") || ((sReg1.equals("")) && (sReg2.equals("")))) {
                    Toast.makeText(MainActivity.this, "Please enter sender details", Toast.LENGTH_SHORT).show();


                } else {

                    SharedPreferences sharedPreferences = getSharedPreferences("myData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("email", sEmail);
                    editor.putString("password", sPassword);
                    editor.putString("reg1", sReg1);
                    editor.putString("reg2", sReg2);


                    editor.commit();

                    Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

                }

            }
        });
        retrive();


        startService(new Intent(this, MyService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, MyService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
// Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                5 * 60 * 1000, pintent);


    }


    public void retrive() {

        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        String emailS = sharedPreferences.getString("email", DEFAULT);
        String passwordS = sharedPreferences.getString("password", DEFAULT);
        String reg1S = sharedPreferences.getString("reg1", DEFAULT);
        String reg2S = sharedPreferences.getString("reg2", DEFAULT);


        if (emailS.equals(DEFAULT) || passwordS.equals(DEFAULT) || reg1S.equals(DEFAULT) || reg2S.equals(DEFAULT)) {

            Log.e("MainActivity", "No Data Found");
            Toast.makeText(MainActivity.this, "Please enter send and receiver details", Toast.LENGTH_SHORT).show();


        } else {

            Log.e("MainActivity", "Data retrived successfully");
            Toast.makeText(MainActivity.this, "Data loaded successfully", Toast.LENGTH_SHORT).show();

            email.setText(emailS);
            password.setText(passwordS);
            reg1.setText(reg1S);
            reg2.setText(reg2S);


        }

        Log.e("MainActivity", "emailSp:-:" + emailS);
        Log.e("MainActivity", "passwordSp:-:" + passwordS);
        Log.e("MainActivity", "reg1Sp:-:" + reg1S);
        Log.e("MainActivity", "re2Sp:-:" + reg2S);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts.csv-related task you need to do.
                    Log.e("Granted", "permission");
                    startService(new Intent(this, SmsListener.class));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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
            case R.id.action_contacts:
               /* final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View alertlayout = inflater.inflate(R.layout.alert_add, null);
                alertDialog.setView(alertlayout);
                final EditText number=(EditText)alertlayout.findViewById(R.id.contact_add);


                alertDialog.setTitle("");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(number.length()>0)
                        {
                            if(number.length()==10) {

                                String phNum=number.getText().toString();
                                Log.e("Phone","number"+phNum);
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

                                        boolean var1=false;
                                        file.getParentFile().mkdirs();
                                        var1 = file.createNewFile();
                                        Log.e("var",""+var1);
                                        fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                                        BufferedWriter bw = new BufferedWriter(fileWriter);
                                        bw.write("Numbers");
                                        bw.newLine();
                                        bw.close();
                                    }
                                    else
                                    {
                                        fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                                        BufferedWriter bw = new BufferedWriter(fileWriter);
                                        bw.write(phNum);
                                        bw.newLine();
                                        bw.close();
                                        Toast.makeText(MainActivity.this,"Number Added Please reopen the app",Toast.LENGTH_SHORT);
                                    }
                                } catch (Exception e) {

                                }
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Please enter a valid mobile number",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Please enter a valid mobile number",Toast.LENGTH_SHORT);
                        }

                    }
                });


                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });


                alertDialog.show();*/
                Intent intent=new Intent(MainActivity.this,ContactsListActivity.class);
                startActivity(intent);


                return true;


        }


        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


}
