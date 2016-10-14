package com.onmymobile.heritage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.onmymobile.heritage.common.MyApplication;
import com.onmymobile.heritage.common.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


/**
 * Created by SoroushJavdan on 1/28/15.
 */
public class MandrillMessage {

    private static Context context;
        public final static String ENDPOINT = "https://mandrillapp.com/api/1.0/messages/send.json" ;
    public static final String DEFAULT = "A/N";



    private Integer id;
        public MandrillMessage(String key, int nextId,Context context1){
            if(key == null) {
                throw new NullPointerException(
                        "'key' is null , please provide Mandrill API key");
            }
            this.key = key ;

            this.context=context1;
            Log.e("Context",""+context);

            this.id=nextId;
        }


        private String key;
        private EmailMessage message;





    public String getKey() {
            return key;
        }
        public EmailMessage getMessage() {
            return message;
        }

        public void setMessage(EmailMessage message) {
            this.message = message;
        }

        public void setKey(String key){
            this.key = key ;
        }





        public String getJson(){
            if(message == null) {
                throw new NullPointerException(
                        "'message' is null , please make sure that you set message");
            }
            Gson gson = new Gson();
            return gson.toJson(this);
        }


        public void send(){
            if(message == null) {
                throw new NullPointerException(
                        "'message' is null , please make sure that you set message");
            }

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    StringBuffer result = new StringBuffer("");
                    try {
                        URL url = new URL(ENDPOINT);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");

                        connection.connect();


                        OutputStream os = connection.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                        osw.write(getJson());
                        osw.flush();
                        osw.close();

                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            result.append(line + "\n");
                        }
                        br.close();

                    } catch (IOException e) {
                        // writing exception to log
                        e.printStackTrace();
                    }
                    Log.d("MandrillMessage", result.toString());
                    String status = null;
                    try {
                        for (int i = 0; i < result.length(); ++i) {

                            JSONArray jsonObject = new JSONArray(result.toString());
                            //Log.e("Json","object"+jsonObject.toString());
                            try {
                                for (int j = 0; i < jsonObject.length(); ++j) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject(j);
                                    status = jsonObject1.getString("status");
                                    Log.e("Status", "" + status);
                                }
                            } catch (JSONException e) {

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (status != null) {
                        if (status == "sent" || status.equals("sent")) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            Log.e("id", "" + id);
                            Sms mysms = realm.where(Sms.class).equalTo("id", id).findFirst();
                            mysms.setFlag("N");
                            realm.commitTransaction();
                            final String message = mysms.getMessage();
                            final String date = mysms.getDate();
                            Log.e("mandril", "date" + date);
                            //Preferences.add(Preferences.PrefType.msg_date,date,context);
                            final String time = mysms.getTime();
                            Log.e("mandrill", "time" + time);
                            final String origin = mysms.getName();
                            Log.e("Send", "Application" + ((MyApplication) context.getApplicationContext()).getFlag());
                            File folder = new File(Environment.getExternalStorageDirectory()
                                    + "/Heritage");
                            boolean var = false;
                            if (!folder.exists())
                                var = folder.mkdir();

                            System.out.println("" + var);
                            //String msgDate=Preferences.getString(Preferences.PrefType.msg_date,context);
                            final String filename = folder.toString() + "/" + date + ".csv";


                            Log.e("filename", "" + filename);

                            File file = new File(filename);


                            String entry = origin + "," + date + "," + time + "," + (char) 34 + message + (char) 34;
                            Log.e("entry", "date" + entry);
                            FileWriter fileWriter = null;


                            try {


                                if (!file.exists()) {

                                    boolean var1 = true;
                                    file.getParentFile().mkdirs();
                                    var1 = file.createNewFile();
                                    Log.e("var", "" + var1);
                                    fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                                    BufferedWriter bw = new BufferedWriter(fileWriter);
                                    bw.write("Origin" + "," + "date" + "," + "time" + "," + "message");
                                    bw.newLine();
                                    bw.write(entry);
                                    bw.newLine();
                                    Log.e("buffered", "reader" + bw);
                                    bw.close();
                                    Log.e("Entered", "" + file.exists());

                                    Preferences.add(Preferences.PrefType.attach_time,time, context);
                                    Preferences.add(Preferences.PrefType.attach_date,date,context);
                                    Preferences.add(Preferences.PrefType.file_name,filename,context);
                                    boolean sharedPreferences = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",true).commit();

                                   // Log.e("Mandril","run"+sharedPreferences);
                                    String prefTime = Preferences.getString(Preferences.PrefType.msg_time, context);
                                   /* SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("myData", Context.MODE_PRIVATE);

                                    String email = sharedPreferences.getString("email", DEFAULT);
                                    String password = sharedPreferences.getString("password", DEFAULT);
                                    String reg1 = sharedPreferences.getString("reg1", DEFAULT);
                                    String re2 = sharedPreferences.getString("reg2", DEFAULT);*/




                                } else {

                                    Log.e("entered", "else");
                                    fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                                    BufferedWriter bw = new BufferedWriter(fileWriter);
                                    bw.write(entry);
                                    Log.e("buffered", "reader" + bw);
                                    bw.newLine();
                                    bw.close();

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("Exception", "" + e);
                            }
                            // show waiting screen


                        }
                    }
                        ((MyApplication) context.getApplicationContext()).setFlag("N");

                        return null;
                    }

            }.execute();


        }
    public void sendAttachment(){
        if(message == null) {
            throw new NullPointerException(
                    "'message' is null , please make sure that you set message");
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                StringBuffer result = new StringBuffer("");
                try{
                    URL url = new URL(ENDPOINT);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.connect();


                    OutputStream os = connection.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                    osw.write(getJson());
                    osw.flush();
                    osw.close();

                    boolean sharedPreferences = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();
                    BufferedReader br = new BufferedReader(new InputStreamReader( connection.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    br.close();


                } catch (IOException e) {
                    // writing exception to log
                    e.printStackTrace();
                    boolean error = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",true).commit();
                    Log.e("error","mandrill"+error);

                }
                Log.d("MandrillMessage", result.toString());
                return null;
            }
        }.execute();


    }


}

