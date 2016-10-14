package com.onmymobile.heritage;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by onmymobile on 8/7/16.
 */
public class CSVFile {
    InputStream inputStream;

public CSVFile(InputStream inputStream){
        this.inputStream = inputStream;
        }

public List<String> read(){
        List<String> resultList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Log.e("Reader",""+reader);
        try {
        String csvLine;
        while ((csvLine = reader.readLine()) != null) {

        String[] row = csvLine.split(",");
                if(csvLine.equals("Numbers"))
                {
                   //Do nothing
                       // resultList.add("+91" + csvLine);
                }
                else {
                        resultList.add("+91" + csvLine);

                }

        }
        }
        catch (Exception e) {
        throw new RuntimeException("Error in reading CSV file: "+e);
        }
        finally {
        try {
        inputStream.close();
        }
        catch (IOException e) {
        throw new RuntimeException("Error while closing input stream: "+e);
        }
        }
        return resultList;
        }
        }