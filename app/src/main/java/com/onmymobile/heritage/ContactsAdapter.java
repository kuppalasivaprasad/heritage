package com.onmymobile.heritage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by onmymobile on 18/8/16.
 */

public class ContactsAdapter extends ArrayAdapter<String> {

    private int Resource;
    private Context context;
    private LayoutInflater vi;
    private List<String> contacts;
    private List<String> cont;
    private ListView listView;
    private ProgressDialog progressDialog;
    TextView nocontacts;

    public ContactsAdapter(Context context, int resource, List<String> objects, List<String> list, TextView noContacts) {
       super(context,resource,objects);
        this.context = context;
        this.Resource = resource;
        this.contacts = objects;
        this.cont=list;
        this.nocontacts=noContacts;

        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        notifyDataSetChanged();


    }
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = vi.inflate(R.layout.contact_item, parent, false);


            holder = new ViewHolder();


            holder.contact=(TextView)convertView.findViewById(R.id.contact);
            holder.delete=(Button)convertView.findViewById(R.id.delete);
            listView=(ListView)parent.findViewById(R.id.contact_list);
           // nocontacts=(TextView)parent.findViewById(R.id.nocontacts);

           /* if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }





       /* for(int i=0;i<contacts.size();++i)
        {
            cont.add(contacts.get(i).toString().substring(3));

        }*/

        if(cont.size()>0) {
            holder.contact.setText(cont.get(position).toString());
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                        new delContacts(position).execute();
                       /* if(progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }*/




            }
        });

        return convertView;
    }

    public class delContacts extends AsyncTask<Void,Void,Void>
    {

        int position;
        public delContacts(int pos) {
            this.position=pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

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
            try {

            } catch (Exception e) {

            }
            cont.remove(position);
            contacts.remove(position);
            file.delete();
            FileWriter fileWriter = null;
            try {
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write("Numbers");
                bw.newLine();
                bw.close();
            } catch (Exception e) {

            }
            for (int i = 0; i < cont.size(); ++i) {
                try {
                    fileWriter = new FileWriter(file.getAbsoluteFile(), true);
                    BufferedWriter bw = new BufferedWriter(fileWriter);
                    bw.write(cont.get(i).toString());
                    bw.newLine();
                    bw.close();
                } catch (Exception e) {

                }
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            if(cont.size()<=0) {
                nocontacts.setVisibility(View.VISIBLE);
            }
            ContactsAdapter contactsAdapter = new ContactsAdapter(getContext(), R.layout.contact_item, contacts, cont, nocontacts);
            listView.setAdapter(contactsAdapter);
            contactsAdapter.notifyDataSetChanged();



        }
    }
    static class ViewHolder {
        public TextView contact;
        public Button delete;
    }
}
