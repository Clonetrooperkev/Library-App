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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class MainContactsActivity extends ActionBarCastActivity {
    private static final String TAG = LogHelper.makeLogTag(MainContactsActivity.class);
    List<String> contactsArray = new ArrayList<String>();
    List<String> urlArray = new ArrayList<String>();
    ProgressDialog mProgressDialog;
    boolean contactsUnavailableError;
    boolean firstContactSearch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        contactsUnavailableError = false;
        firstContactSearch = false;
        LogHelper.w(TAG, "Testing Joe");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initializeToolbar();
        setTitle("Contacts");
        //setContentView(R.layout.activity_placeholder);

        new DownloadJSON().execute();

    }
    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainContactsActivity.this);
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
            contactsArray.add(" Library Branches");
            urlArray.add(" Library Branches");

            try {

                    doContactSearch();
            }
            catch(Exception e){
                LogHelper.e(TAG,e,"Things be wacky");
            }





            /*
            try {
                // Retrive JSON Objects from the given URL in JSONfunctions.class
                JSONObject json_data = JSONfunctions.getJSONfromURL(url);
                JSONObject json_query = json_data.getJSONObject("query");
                JSONObject json_results = json_query.getJSONObject("results");
                JSONObject json_div1 = json_results.getJSONObject("div");
                JSONObject json_div2 = json_div1.getJSONObject("div");
                JSONObject json_ul = json_div2.getJSONObject("ul");
                //JSONObject json_li = json_ul.getJSONObject("li");

               // JSONObject json_json_li = json_ul.getJSONObject("json");
                JSONArray json_result = json_ul.getJSONArray("li");
                for (int i = 0; i < json_result.length(); i++) {
                    JSONObject c = json_result.getJSONObject(i);
                    JSONObject vo = c.getJSONObject("span");
                    JSONObject va = vo.getJSONObject("a");
                    contactsArray.add(va.optString("content"));
                    urlArray.add(va.optString("href"));


                }

            } catch (JSONException e) {
                contactsUnavailableError = true;

                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            */

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            ListView lv = (ListView) findViewById(R.id.contactslist);
            lv.setAdapter(new ArrayAdapter<String>(MainContactsActivity.this, R.layout.contactlist_item, R.id.contact_name, contactsArray));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String contactName = ((TextView) view.findViewById(R.id.contact_name)).getText().toString();

                    Toast.makeText(getApplicationContext(), contactName, Toast.LENGTH_SHORT).show();
                    if (position >= 0) {
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                MainContactsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Class activityClass = SubContactsActivity.class;
                        Intent subIntent = new Intent(MainContactsActivity.this, activityClass);
                        subIntent.putExtra("SUBCONTACTKEY", urlArray.get(position));
                        startActivity (subIntent, extras);
                        finish();
                    }

                }
            });
            if (contactsUnavailableError) {
                Toast.makeText(getApplicationContext(), "Contact Information Unavailable", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }

    public void doContactSearch() throws Exception {
        String url = "http://www.cmclibrary.org/about-the-library/contact-us";
        String USER_AGENT = "Chrome/43.0.2357.134";
        URL obj1 = new URL(url);
        HttpURLConnection.setFollowRedirects(true);
        //Get session ID if necessary
        if(firstContactSearch == true) {
            HttpURLConnection conx = (HttpURLConnection) obj1.openConnection();
            conx.setRequestProperty("CSP", "active");
            conx.setRequestProperty("Accept", "*/*");
            conx.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            conx.setUseCaches(false);
            conx.setDoInput(true);
            conx.setChunkedStreamingMode(0);

            conx.setRequestMethod("GET");
            int responseCodex = conx.getResponseCode();
            BufferedReader inx = new BufferedReader(
                    new InputStreamReader(conx.getInputStream()));
            inx.close();
            firstContactSearch = false;
        }
        HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();
        con1.setRequestProperty("CSP", "active");
        con1.setRequestProperty("Accept", "*/*");
        con1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        con1.setUseCaches(false);
        con1.setDoInput(true);
        con1.setChunkedStreamingMode(0);

        con1.setRequestMethod("GET");
        int responseCode = con1.getResponseCode();
        BufferedReader in1 = new BufferedReader(
                new InputStreamReader(con1.getInputStream()));
        String inputLine1;
        StringBuffer response1 = new StringBuffer();

        while ((inputLine1 = in1.readLine()) != null) {
            response1.append(inputLine1);
        }
        in1.close();
        String str = response1.toString();

        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(false);

        //HTML page root node
        TagNode root = cleaner.clean(str);
        cleaner.getInnerHtml(root);

        String html = "<" + root.getName() + ">" + cleaner.getInnerHtml(root) + "</" + root.getName() + ">";
        InputSource source = new InputSource(new StringReader(html));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(source);

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        /*
        Node testNode1 = (Node) xpath.evaluate("//div[@class='nsm-long-item nsm-e35']", document, XPathConstants.NODE);
        detailsTitle = xpath.evaluate(".", testNode1);
        testNode1 = (Node) xpath.evaluate("//div[@class='nsm-long-item nsm-e9']", document, XPathConstants.NODE);
        detailsSummary = xpath.evaluate(".",testNode1);
        */
        //Node testNode1 = (Node) xpath.evaluate("/html/body/div[1]/b", document, XPathConstants.NODE);
        //detailsTitle = xpath.evaluate(".", testNode1);
        NodeList testNode = (NodeList)xpath.evaluate("//span[@class='item-title']", document, XPathConstants.NODESET);
        int i = testNode.getLength();
        for (int index = 0; index < testNode.getLength(); index++) {
            Node anode = testNode.item(index);
            Node subNode = (Node)xpath.evaluate("./a", anode, XPathConstants.NODE);
            String tempString = xpath.evaluate(".",subNode);
            contactsArray.add(tempString);
            subNode = (Node)xpath.evaluate("./a/@href", anode, XPathConstants.NODE);
            tempString = xpath.evaluate(".",subNode);
            urlArray.add(tempString);

        }
    }


}

