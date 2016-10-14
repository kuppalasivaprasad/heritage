package com.onmymobile.heritage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.onmymobile.heritage.common.MyApplication;
import com.onmymobile.heritage.common.Preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import io.realm.Realm;

/**
 * Created by onmymobile on 26/7/16.
 */
public class SendDailyReport extends AsyncTask<Void, Void, Void> {

    //Information to send email
    public static final String DEFAULT = "A/N";
    //Declaring Variables
    private Context context;
    private Session session;
    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;
    String emailID;
    String password;
    String email;
    String email1;
    String fileName;
    String attachDate;


    //Class Constructor
    public SendDailyReport(Context context,String email, String password, String reg1, String re2, String file, String attachDate) {
        this.emailID=email;
        this.password=password;
        this.email=reg1;
        this.email1=re2;
        this.fileName=file;
        this.attachDate=attachDate;
        this.context=context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

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
       // SharedPreferences sharedPreferences = context.getSharedPreferences("myData", Context.MODE_PRIVATE);
        Log.e("Emial", "" + emailID);

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
                        return new PasswordAuthentication(emailID, password);
                    }
                });
        int result = 0;

        try {
            //Creating MimeMessage object
            Message mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(session.getProperty("mail.from"), "Heritage"));
            Log.e("Email", "Address" + emailID);
            Log.e("Session", "mail" + session.getProperty("mail.from"));

            Log.e("file", "name" + fileName);

            //Adding re ceiver
            if (email.equals("")) {
            } else {
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }

            if (email1.equals("")) {
            } else {
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email1));
            }

            //Adding subject
            Multipart _multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(fileName);

            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("daily_report ("+attachDate+" )");

            _multipart.addBodyPart(messageBodyPart);
            mm.setContent(_multipart);
            mm.setSubject("Daily Report ("+attachDate+" )");
            //Adding message
            //mm.setText("Date:" + date + " " + "at" + time + "\n" + "\n" + message);

            //Sending email
            Transport.send(mm);
            boolean sharedPreferences1 = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();

            Log.e("Shared","Preferences"+sharedPreferences1);
            Preferences.add(Preferences.PrefType.attachFlag,"N",context.getApplicationContext());
            result = 0;


        } catch (SendFailedException ee) {
            result = 1;
            boolean error = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",true).commit();

        } catch (MessagingException e) {
            e.printStackTrace();
            result = 1;
            Log.e("Exception", "" + e);
            Log.e("Mail not sent", "");
            boolean error = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",true).commit();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            boolean error = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",true).commit();
        }

       /* if (result == 0) {


        } else {
            Log.e("Message", "not send");



        }*/
        return null;
    }
}
