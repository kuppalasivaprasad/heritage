package com.onmymobile.heritage;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.onmymobile.heritage.common.MyApplication;
import com.onmymobile.heritage.common.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by BHarath on 5/7/16
 */
public class SmsListener extends BroadcastReceiver {
    String msgBody = "";
    String msgFrom = "";
    long msg_timestamp;
    String msgDate;
    String msgTime;
    int nextID;
    public static final String DEFAULT = "A/N";
    final SmsManager sms = SmsManager.getDefault();
    MyApplication myApplication=null;
    List phList;

    InputStream inputStream=null;

   @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("Entered","receive");
       String processing=Preferences.getString(Preferences.PrefType.flag,context);
       if(processing.equals("Y")) {
           if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
               Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
               SmsMessage[] msgs = null;

               if (bundle != null) {
                   //---retrieve the SMS message received---
                  //phList=((MyApplication)context.getApplicationContext()).getPhList();
                   try {
                       File folder = new File(Environment.getExternalStorageDirectory()
                               + "/Heritage");
                       boolean var = false;
                       if (!folder.exists())
                           var = folder.mkdir();
                       final String filename = folder.toString() + "/" + "contacts.csv";
                       File file = new File(filename);
                       inputStream = new FileInputStream(file);

                       Log.e("input", "" + inputStream);
                       CSVFile csvFile = new CSVFile(inputStream);
                       phList = csvFile.read();
                       Log.e("phone", "" + phList);
                   }catch (Exception e)
                   {

                   }
                   try {
                       Object[] pdus = (Object[]) bundle.get("pdus");
                       msgs = new SmsMessage[pdus.length];
                       for (int i = 0; i < msgs.length; i++) {
                           msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                           msgFrom = msgs[i].getOriginatingAddress();
                           msgBody += msgs[i].getMessageBody();
                           msg_timestamp = msgs[i].getTimestampMillis();
                       }

                       Log.e("SmsListener", "msgFrom:-:" + msgFrom);
                       Log.e("phone", "list" + phList);
                    if(phList.contains(msgFrom)) {

                       msgDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(msg_timestamp));
                       Log.e("SmsListener", "dateString:-:" + msgDate);

                       Date date = new Date(msg_timestamp);
                       DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                       msgTime = formatter.format(date);

                       Log.e("SmsLois", "dateFormatted:-:" + msgTime);

                   /* Preferences.add(Preferences.PrefType.msg_body, msgBody, context);
                    Preferences.add(Preferences.PrefType.msg_numb, msgFrom, context);
                    Preferences.add(Preferences.PrefType.msg_date, msgDate, context);
                    Preferences.add(Preferences.PrefType.msg_time, msgTime, context);*/


                       Realm realm = Realm.getDefaultInstance();

                       realm.beginTransaction();
                       Sms sms = realm.createObject(Sms.class);
                       nextID = (int) (realm.where(Sms.class).maximumInt("id") + 1);
                       sms.setId(nextID);
                       sms.setName(msgFrom);
                       sms.setFlag("Y");
                       sms.setMessage(msgBody);
                       sms.setDate(msgDate);
                       sms.setTime(msgTime);
                       realm.commitTransaction();


                       RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
                       Realm.setDefaultConfiguration(realmConfig);
                       final RealmResults<Sms> message = realm.where(Sms.class).findAll();
                   }


                   } catch (Exception e) {
                       Log.d("Exception caught", e.getMessage());
                   }


                   SharedPreferences sharedPreferences = context.getSharedPreferences("myData", Context.MODE_PRIVATE);

                   String email = sharedPreferences.getString("email", DEFAULT);
                   String password = sharedPreferences.getString("password", DEFAULT);
                   String reg1 = sharedPreferences.getString("reg1", DEFAULT);
                   String re2 = sharedPreferences.getString("reg2", DEFAULT);


                   if (email.equals(DEFAULT) || password.equals(DEFAULT) || reg1.equals(DEFAULT) || re2.equals(DEFAULT)) {

                       Log.e("SmsListener", "No Data Found");


                   } else {

                       //Log.e("application","flaf"+application.getFlag());



                       if (msgTime != null) {
                           ((MyApplication) context.getApplicationContext()).setFlag("Y");
                           new SendMail(context, reg1, re2, msgFrom, msgBody, nextID, msgTime, msgDate).execute();
                       }


                   }


               }
           }
       }


    }


}
