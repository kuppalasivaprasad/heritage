package com.onmymobile.heritage.common;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.onmymobile.heritage.CSVFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Barath on 5/7/16
 */
public class MyApplication extends Application {
    String flag;
    private Context context;
    private List phList;
    String attachflag;
    @Override
    public void onCreate() {

        super.onCreate();


context=getApplicationContext();
        Log.e("Application","Class");
        InputStream inputStream = null;
        try {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/Heritage");
            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();
            final String filename = folder.toString() + "/" + "contacts.csv";
            File file = new File(filename);
            FileWriter fileWriter=null;
            if(!file.exists())
            {
                boolean var1=false;
                file.getParentFile().mkdirs();
                var1 = file.createNewFile();

               // Runtime.getRuntime().exec("chmod 777 " + filename);
                Log.e("var",""+var1);
                fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write("Numbers");
                bw.newLine();
                bw.close();
            }

           /* for(int i=0;i<1000;++i)
            {
                fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write("999999999"+i);
                bw.newLine();
                bw.close();
            }*/

/*
            inputStream = new FileInputStream(file);

            Log.e("input",""+inputStream);
            CSVFile csvFile = new CSVFile(inputStream);
            phList = csvFile.read();
            Log.e("phone",""+phList);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);


    }
    public List getPhList()
    {
        return phList;
    }
    public void setPhList(List phList)
    {
        this.phList=phList;
    }
    public String getFlag()
    {
        return flag;
    }
    public void setFlag(String flag)
    {
        this.flag=flag;
    }

    public String getAttachflag()
    {
        return attachflag;
    }
    public void setAttachflag(String attachflag)
    {
        this.attachflag=attachflag;
    }


}
