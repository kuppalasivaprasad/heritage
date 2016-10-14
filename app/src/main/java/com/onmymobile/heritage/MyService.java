package com.onmymobile.heritage;

/**
 * Created by onmymobile on 5/7/16
 */

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.onmymobile.heritage.common.MyApplication;
import com.onmymobile.heritage.common.Preferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyService extends Service {

    public static final String DEFAULT = "A/N";
    int nextID;
    int id;
    List phList;
    private String processingFlag;
    private String prevDate;
    InputStream inputStream=null;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myData", Context.MODE_PRIVATE);

        String email = sharedPreferences.getString("email", DEFAULT);
        String password = sharedPreferences.getString("password", DEFAULT);
        String reg1 = sharedPreferences.getString("reg1", DEFAULT);
        String re2 = sharedPreferences.getString("reg2", DEFAULT);
        processingFlag = Preferences.getString(Preferences.PrefType.flag, getApplicationContext());
        if (processingFlag == null || processingFlag.equals("Y")) {
            Preferences.add(Preferences.PrefType.flag, "Y", getApplicationContext());
        }
        Log.e("Service", "Started");
        Log.e("Processing", "flag" + Preferences.getString(Preferences.PrefType.flag, getApplicationContext()));


        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Heritage");
        if (folder.exists()) {
            String attachTime = Preferences.getString(Preferences.PrefType.attach_time, getApplicationContext());
            String attachDate = Preferences.getString(Preferences.PrefType.attach_date, getApplicationContext());
            Log.e("Attach", "Date" + attachDate);
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("isFirstRun", true);
            Log.e("Service:is", "run" + isFirstRun);
            String attachFlag = Preferences.getString(Preferences.PrefType.attachFlag, getApplicationContext());
            if (attachFlag == null || attachFlag.equals("null")) {
                Preferences.add(Preferences.PrefType.attachFlag, "N", getApplicationContext());

            }
            Log.e("Attach", "flag" + attachFlag);
            attachFlag = Preferences.getString(Preferences.PrefType.attachFlag, getApplicationContext());

            if (attachFlag == "N" || attachFlag.equals("N")) {
                if (isFirstRun) {
                    Log.e("hour", "ofday" + hour);
                    if (hour >= 17) {

                        try {
                            String filename1 = folder.toString() + "/" + attachDate + ".csv";
                            Log.e("file", "name" + filename1);
                            if (filename1 != null) {
                                Log.e("file", "name" + filename1);
                                File file = new File(filename1);
                                if (file.exists()) {
                                    Preferences.add(Preferences.PrefType.attachFlag, "Y", getApplicationContext());
                                    new SendDailyReport(getApplicationContext(), email, password, reg1, re2, filename1, attachDate).execute();
                                /*MandrillMessage allMessage = new MandrillMessage("gcduf0NvtLurLqZwoc6peA", id, getApplicationContext());
                                EmailMessage message1 = new EmailMessage();
                                message1.setFromEmail(email);
                                message1.setFromName("Heritage");
                                message1.setHtml("<p>Daily Report</p>");
                                message1.setText("<p>Daily Report</p>");
                                message1.setSubject("Daily Report ("+attachDate+")");
                                // add recipients
                                Recipient recipient = new Recipient();
                                Recipient recipient1 = new Recipient();
                                List<Recipient> recipients = new ArrayList<Recipient>();
                                Log.e("Email", "" + reg1);
                                if (reg1.equals("")) {
                                } else {
                                    recipient.setEmail(reg1);
                                    recipients.add(recipient);
                                }
                                Log.e("Email", "1" + re2);
                                if (re2.equals("")) {
                                } else {
                                    recipient1.setEmail(re2);
                                    recipients.add(recipient1);
                                }
                                Attachment attachment = new Attachment();
                                List<Attachment> attachments = new ArrayList<Attachment>();

                                attachment.setType(".csv");
                                attachment.setName("daily_report (" + attachDate + ").csv");
                                Uri u1 = null;

                                // String str = FileUtils.readFileToString(file, "UTF-8");
                                String encodedString = "0";
                                InputStream inputStream = null;//You can get an inputStream using any IO API
                                inputStream = new FileInputStream(filename1);

                                byte[] bytes;
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                ByteArrayOutputStream output = new ByteArrayOutputStream();
                                try {
                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        output.write(buffer, 0, bytesRead);

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                bytes = output.toByteArray();
                                encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                                attachment.setContent(encodedString);
                                attachments.add(attachment);
                                message1.setTo(recipients);
                                message1.setAttachments(attachments);
                                allMessage.setMessage(message1);
                                allMessage.sendAttachment();*/


                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        processingFlag = Preferences.getString(Preferences.PrefType.flag, getApplicationContext());
        if (processingFlag.equals("N")) {
            Log.e("Entered", "N");
            prevDate = Preferences.getString(Preferences.PrefType.msg_date, getApplicationContext());
            Log.e("previous", "Date" + prevDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String currDate = formatter.format(new Date());

            String dateEnd = formatter.format(new Date());
            Date currentDate = null;
            Date previousDate = null;
            //phList = ((MyApplication) getApplicationContext()).getPhList();
            try {
                File folder1 = new File(Environment.getExternalStorageDirectory()
                        + "/Heritage");
                boolean var = false;
                if (!folder.exists())
                    var = folder.mkdir();
                final String filename = folder1.toString() + "/" + "contacts.csv";
                File file = new File(filename);
                inputStream = new FileInputStream(file);

                Log.e("input", "" + inputStream);
                CSVFile csvFile = new CSVFile(inputStream);
                phList = csvFile.read();
                Log.e("phone", "" + phList);
            }catch (Exception e)
            {

            }
            Log.e("phone", "1list" + phList);
            try {
                currentDate = formatter.parse(dateEnd);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (prevDate != null) {
                try {
                    previousDate = formatter.parse(prevDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String filter = "date>=" + previousDate.getTime() + " and date<=" + currentDate.getTime();
                final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
                Cursor cursor = getContentResolver().query(SMS_INBOX, null, filter, null, null);
                Log.e("Cursor", "Data" + cursor.getCount());

                while (cursor.moveToNext()) {

                    Log.e("Entered", "cursor");
                    // Convert date to a readable format.
                    Calendar calendar = Calendar.getInstance();
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    Long timestamp = Long.parseLong(date);
                    calendar.setTimeInMillis(timestamp);
                    Date finaldate = calendar.getTime();
                    Log.e("Date", "final" + finaldate);
                    String msgDate = new SimpleDateFormat("dd-MM-yyyy").format(finaldate);
                    String msgTime = new SimpleDateFormat("HH:mm:ss").format(finaldate);
                    String smsBody = cursor.getString(cursor.getColumnIndex("body"));

                    String phoneNumber = cursor.getString(cursor.getColumnIndex("address"));
                    Log.e("Origin", "message" + phoneNumber);
                    if (phList.contains(phoneNumber)) {
                        Realm realm = Realm.getDefaultInstance();

                        realm.beginTransaction();
                        Sms sms = realm.createObject(Sms.class);
                        nextID = (int) (realm.where(Sms.class).maximumInt("id") + 1);
                        sms.setId(nextID);
                        sms.setName(phoneNumber);
                        sms.setFlag("Y");
                        sms.setMessage(smsBody);
                        sms.setDate(msgDate);
                        sms.setTime(msgTime);
                        realm.commitTransaction();
                    }


                    Preferences.add(Preferences.PrefType.msg_date, currDate, getApplicationContext());

                }
                //cursor.moveToNext();


                cursor.close();
            }
        }
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Sms> message = realm.where(Sms.class).equalTo("flag", "Y").findAll();
        Log.e("message", "size" + message.size());

        final RealmResults<Sms> flag = realm.where(Sms.class).equalTo("flag", "N").findAll();

        if (flag.size() > 0) {
            realm.beginTransaction();
            flag.clear();
            realm.commitTransaction();
            // Log.e("Flag", "" + flag);
        }
        try {
            String flag1;
            flag1 = ((MyApplication) getApplicationContext()).getFlag();
            Log.e("flag1", "" + flag1);
            if (flag1 == null || flag1.equals(null)) {
                ((MyApplication) getApplicationContext()).setFlag("N");
            }
            flag1 = ((MyApplication) getApplicationContext()).getFlag();
            if (message.size() > 0) {
                if (flag1.equals("N") || flag1.equals("Z")) {
                    Log.e("falg", "" + flag1);
                    for (int i = 0; i < message.size(); ++i) {
                        String msgBody = message.get(i).getMessage();
                        int nextID = message.get(i).getId();
                        String origin = message.get(i).getName();
                        String msgTime = message.get(i).getTime();
                        String msgDate = message.get(i).getDate();
                        ((MyApplication) getApplicationContext()).setFlag("Y");
                        Log.e("Flag", "Application" + ((MyApplication) getApplicationContext()).getFlag());

                        new SendMail(getApplicationContext(), reg1, re2, origin, msgBody, nextID, msgTime, msgDate).execute();
                    }
                }
            }
        } catch (Exception e) {

        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}