package com.onmymobile.heritage;

import io.realm.RealmObject;

/**
 * Created by Bharath on 5/7/16
 */
public class Sms extends RealmObject {
    private String date;
    private String name;
    private String message;
    private String flag;
    private int id;
    private String time;

    public Sms()
    {

    }
    public int getId()
    {
        return id;
    }
    public  void setId(int id)
    {
        this.id=id;
    }
    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date=date;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message=message;
    }
    public String getFlag()
    {
        return flag;
    }
    public void setFlag(String flag)
    {
        this.flag=flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
