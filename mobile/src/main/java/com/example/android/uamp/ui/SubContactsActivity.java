/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.uamp.ui;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.uamp.R;
import com.example.android.uamp.utils.LogHelper;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Placeholder activity for features that are not implemented in this sample, but
 * are in the navigation drawer.
 */

public class SubContactsActivity extends ActionBarCastActivity {
    private static final String TAG = LogHelper.makeLogTag(SubContactsActivity.class);
    List<String> subcontactsArray = new ArrayList<String>();
    List<String> subphonesArray = new ArrayList<String>();
    List<String> suburlArray = new ArrayList<String>();
    ListView listview;
    ArrayAdapter adapter;
    ProgressDialog mProgressDialog;
    boolean contactsUnavailableError;
    String copiedtext;
    String subContactCategory;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        contactsUnavailableError = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity_contacts);
        initializeToolbar();
        setTitle("Contact");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            if(extras.containsKey("SUBCONTACTKEY")) {
                subContactCategory= extras.getString("SUBCONTACTKEY");
            }
        }
        new DownloadJSON().execute();

    }
    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(SubContactsActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Contact Data");
            // Set progressdialog message
            mProgressDialog.setMessage("Fetching...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(subContactCategory.equals(" Library Branches")){
                subcontactsArray.add("Main Branch");
                subphonesArray.add("609-463-6350");
                subcontactsArray.add("Cape May City");
                subphonesArray.add("609-884-9568");
                subcontactsArray.add("Lower Township");
                subphonesArray.add("609-886-8999");
                subcontactsArray.add("Sea Isle City");
                subphonesArray.add("609-263-7301");
                subcontactsArray.add("Stone Harbor");
                subphonesArray.add("609-36-86809");
                subcontactsArray.add("Upper Township");
                subphonesArray.add("609-628-2607");
                subcontactsArray.add("Wildwood Crest");
                subphonesArray.add("609-522-0564");
                subcontactsArray.add("Woodbine");
                subphonesArray.add("609-861-2501");

            }
            else {
                String urlfront = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%20%3D%20%22http%3A%2F%2Fcmclibrary.org";
                String urlmid = "/about-the-library/contact-us/118-adult-programing";
                String urlback = "%22%20and%20xpath%20%3D%20%22%2F%2Ftable%5B%40class%20%3D%20'category'%5D%22&format=json&diagnostics=true&callback=";
                String url = urlfront + subContactCategory + urlback;
                try {
                    // Retrive JSON Objects from the given URL in JSONfunctions.class
                    JSONObject json_data = JSONfunctions.getJSONfromURL(url);
                    JSONObject json_query = json_data.getJSONObject("query");
                    JSONObject json_results = json_query.getJSONObject("results");
                    JSONObject json_table = json_results.getJSONObject("table");
                    JSONObject json_tbody = json_table.getJSONObject("tbody");


                    JSONArray json_result = json_tbody.optJSONArray("tr");
                    if (json_result == null) {
                        JSONObject json_result1 = json_tbody.getJSONObject("tr");
                        JSONArray json_tdA = json_result1.getJSONArray("td");
                        JSONObject D = json_tdA.getJSONObject(0);
                        JSONObject con1 = D.getJSONObject("a");
                        subcontactsArray.add(con1.optString("content"));
                        JSONObject Q = json_tdA.getJSONObject(2);
                        subphonesArray.add(Q.optString("content"));
                    } else {
                        json_result = json_tbody.getJSONArray("tr");
                        for (int i = 0; i < json_result.length(); i++) {
                            JSONObject o = json_result.getJSONObject(i);
                            JSONArray json_td1 = o.getJSONArray("td");
                            JSONObject c = json_td1.getJSONObject(0);
                            JSONObject con = c.getJSONObject("a");
                            subcontactsArray.add(con.optString("content"));
                            JSONObject p = json_td1.getJSONObject(2);
                            subphonesArray.add(p.optString("content"));


                        }
                    }

                } catch (JSONException e) {
                    contactsUnavailableError = true;

                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

            }
            return null;
        }



        @Override
        protected void onPostExecute(Void args) {
            //ContactDetailAdapter adapter = new ContactDetailAdapter(SubContactsActivity.this,subcontactsArray,subphonesArray);
            //ListView lv = (ListView) findViewById(R.id.subcontactslist);
            //lv.setAdapter(new ArrayAdapter<String>(SubContactsActivity.this, R.layout.sub_contactlist_item, R.id.sub_contact_name, subcontactsArray));
            //lv1.setAdapter(new ArrayAdapter<String>(SubContactsActivity.this, R.layout.sub_contactlist_item, R.id.sub_contact_phone_name, subphonesArray));
            //lv.setAdapter(adapter);

            ContactDetailAdapter adapter = new ContactDetailAdapter(SubContactsActivity.this, R.layout.sub_contactlist_item, subcontactsArray, subphonesArray);


            ListView lv = (ListView)findViewById(R.id.subcontactslist);
            lv.setAdapter(adapter);



            if (contactsUnavailableError) {
                Toast.makeText(getApplicationContext(), "Contact Information Unavailable", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }



}

