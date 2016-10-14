package com.onmymobile.heritage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onmymobile.heritage.common.MyApplication;
import com.onmymobile.heritage.common.Preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.realm.Realm;

/**
 * Created by Bharath on 5/7/16
 */
public class SendMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String email1;
    private String subject;
    private String message;
    private int nextId;
    private String time;
    private String date;
    public static final String DEFAULT = "A/N";



    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;




    //Class Constructor
    public SendMail(Context context, String email, String email1, String subject, String message, int id, String msgTime, String msgDate){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.email1=email1;
        this.subject = subject;
        this.message = message;
        this.nextId=id;
        this.time=msgTime;
        this.date=msgDate;
        Log.e("Email",""+email);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.e("Execute",""+message);
        //Showing progress dialog while sending email
       // progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
       // progressDialog.dismiss();
        //Showing a success message






    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();
        SharedPreferences sharedPreferences = context.getSharedPreferences("myData", Context.MODE_PRIVATE);

        final String emailID = sharedPreferences.getString("email", DEFAULT);
        final String password = sharedPreferences.getString("password", DEFAULT);
        Log.e("Emial",""+emailID);
/*
        MandrillMessage allMessage = new MandrillMessage("gcduf0NvtLurLqZwoc6peA",nextId,context);
        // create your message
        EmailMessage message1 = new EmailMessage();
        message1.setFromEmail(emailID);
        message1.setFromName("Heritage");
        message1.setHtml("Date:"+date+" "+"at"+time+"\n"+"<P>"+message+"</p>");
        message1.setText("Date:"+date+" "+"at"+time+"\n"+"\n"+message);
        //message1.setId(nextId);

        Log.e("message",""+message1);
        message1.setSubject(subject);

        // add recipients
        Recipient recipient = new Recipient();
        Recipient recipient1=new Recipient();
        List<Recipient> recipients = new ArrayList<Recipient>();
        Log.e("Email",""+email);
        if(email.equals("")) {
        }else {
            recipient.setEmail(email);
            recipients.add(recipient);
        }
        Log.e("Email","1"+email1);
        if(email1.equals("")) {
        }else{
            recipient1.setEmail(email1);
            recipients.add(recipient1);
        }
        //recipient.setName("soroush");

        message1.setTo(recipients);
        Log.e("Recipients",""+recipients.size());


        allMessage.setMessage(message1);
        allMessage.send();*/


        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailID,password);
                    }
                });
        int result=0;

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(session.getProperty("mail.from"),"Heritage"));
            Log.e("Email","Address"+emailID);
            Log.e("Session","mail"+session.getProperty("mail.from"));

            //Adding re ceiver
            if(email.equals(""))
            {
            }else {
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }

            if(email1.equals("")) {
            }else{
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email1));
            }

            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText("Date:"+date+" "+"at"+time+"\n"+"\n"+message);

            //Sending email
            Transport.send(mm);

            result=0;


        }catch(SendFailedException ee)
        {
            result=1;

        }
        catch (MessagingException e) {
            e.printStackTrace();
            result=1;
            Log.e("Exception",""+e);
            Log.e("Mail not sent","");



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(result==0 )
        {
            Log.e("Result",""+result);
            //Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Sms mysms = realm.where(Sms.class).equalTo("id", nextId).findFirst();
            mysms.setFlag("N");
            String message1=mysms.getMessage();
            final String date = mysms.getDate();
            final String origin = mysms.getName();
            String monthName=null;
            String year=null;

            Log.e("message",""+message1);
            try {
                Date d = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date);
                Log.e("date",""+d);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
                year=new SimpleDateFormat("yyyy").format(cal.getTime());
                Log.e("month",""+monthName);
                Log.e("year",""+year);



            }catch (Exception e)
            {

                Log.e("exception",""+e);
            }

            if(message1.contains(context.getResources().getString(R.string.powerIssue)))
            {
                final DatabaseReference heritage = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(context.getResources().getString(R.string.firebase_url));
                Map<String, Object> day=new HashMap<String, Object>();
                day.put("From",origin);
                String desc=message1.replace(context.getResources().getString(R.string.powerIssue),"");
                day.put("Desc",desc);
                day.put("Time",date+ " "+time);
                heritage.child("day").child(date).child(context.getResources().getString(R.string.powerIssue)).push().updateChildren(day);
                heritage.child("month").child(monthName).child(context.getResources().getString(R.string.powerIssue)).push().updateChildren(day);
                heritage.child("year").child(year).child(context.getResources().getString(R.string.powerIssue)).push().updateChildren(day);


            }
            else if(message1.contains(context.getResources().getString(R.string.vehicleIssue)))
            {
                final DatabaseReference heritage = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(context.getResources().getString(R.string.firebase_url));
                Map<String, Object> day=new HashMap<String, Object>();
                day.put("From",origin);
                String desc=message1.replace(context.getResources().getString(R.string.vehicleIssue),"");
                day.put("Desc",desc);
                day.put("Time",date+ " "+time);
                heritage.child("day").child(date).child(context.getResources().getString(R.string.vehicleIssue)).push().updateChildren(day);
                heritage.child("month").child(monthName).child(context.getResources().getString(R.string.vehicleIssue)).push().updateChildren(day);
                heritage.child("year").child(year).child(context.getResources().getString(R.string.vehicleIssue)).push().updateChildren(day);


            }
            else if(message1.contains(context.getResources().getString(R.string.resourcesIssue)))
            {
                final DatabaseReference heritage = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(context.getResources().getString(R.string.firebase_url));
                Map<String, Object> day=new HashMap<String, Object>();
                day.put("From",origin);
                String desc=message1.replace(context.getResources().getString(R.string.resourcesIssue),"");
                day.put("Desc",desc);
                day.put("Time",date+ " "+time);
                heritage.child("day").child(date).child(context.getResources().getString(R.string.resourcesIssue)).push().updateChildren(day);
                heritage.child("month").child(monthName).child(context.getResources().getString(R.string.resourcesIssue)).push().updateChildren(day);
                heritage.child("year").child(year).child(context.getResources().getString(R.string.resourcesIssue)).push().updateChildren(day);


            }
            else if(message1.contains(context.getResources().getString(R.string.tankersIssue)))
            {
                final DatabaseReference heritage = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(context.getResources().getString(R.string.firebase_url));
                Map<String, Object> day=new HashMap<String, Object>();
                day.put("From",origin);
                String desc=message1.replace(context.getResources().getString(R.string.tankersIssue),"");
                day.put("Desc",desc);
                day.put("Time",date+ " "+time);
                heritage.child("day").child(date).child(context.getResources().getString(R.string.tankersIssue)).push().updateChildren(day);
                heritage.child("month").child(monthName).child(context.getResources().getString(R.string.tankersIssue)).push().updateChildren(day);
                heritage.child("year").child(year).child(context.getResources().getString(R.string.tankersIssue)).push().updateChildren(day);


            }
            else  if(message1.contains(context.getResources().getString(R.string.otherIssues)))
            {
                final DatabaseReference heritage = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl(context.getResources().getString(R.string.firebase_url));
                Map<String, Object> day=new HashMap<String, Object>();
                day.put("From",origin);
                String desc=message1.replace(context.getResources().getString(R.string.otherIssues),"");
                day.put("Desc",desc);
                day.put("Time",date+ " "+time);
                heritage.child("day").child(date).child(context.getResources().getString(R.string.otherIssues)).push().updateChildren(day);
                heritage.child("month").child(monthName).child(context.getResources().getString(R.string.otherIssues)).push().updateChildren(day);
                heritage.child("year").child(year).child(context.getResources().getString(R.string.otherIssues)).push().updateChildren(day);


            }
            realm.commitTransaction();
            ((MyApplication) context.getApplicationContext()).setFlag("N");

            Log.e("flag","application"+((MyApplication)context.getApplicationContext()).getFlag());


            final String message = mysms.getMessage();

            Log.e("mandril", "date" + date);
            //Preferences.add(Preferences.PrefType.msg_date,date,context);
            final String time = mysms.getTime();
            Log.e("mandrill", "time" + time);

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
                    boolean sharedPreferences1 = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",true).commit();

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



            Log.e("Message1",""+message);

        }
        else
        {
            Log.e("Message","not send");
            ((MyApplication) context.getApplicationContext()).setFlag("N");

            Log.e("flag","application"+((MyApplication)context.getApplicationContext()).getFlag());
        }
        return null;
    }
}