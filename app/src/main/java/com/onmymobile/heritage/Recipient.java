package com.onmymobile.heritage;

import java.util.ArrayList;

/**
 * Created by SoroushJavdan on 1/28/15.
 */
public class Recipient {

    private String email;
    private String email1;
    private String name;
    private String type="to";
    private ArrayList<String> values = new ArrayList<>();

    public String getEmail1()
    {
        return email1;
    }
    public void setEmail1(String email1)
    {
        this.email1=email1;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name ){
        this.name =  name ;
    }


    public void setEmail(String email) {
            this.email = email;
        }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public ArrayList<String> getValues()
    {
        return values;
    }
    public boolean addValues(String emails)
    {
        values.add(emails);
        return true;

    }

}
