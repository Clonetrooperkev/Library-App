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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.uamp.R;
import com.example.android.uamp.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Placeholder activity for features that are not implemented in this sample, but
 * are in the navigation drawer.
 */
public class PlaceholderActivity extends ActionBarCastActivity {
    private static final String TAG = LogHelper.makeLogTag(PlaceholderActivity.class);
    List<String> contactsArray = new ArrayList<String>();
    List<String> urlArray = new ArrayList<String>();
    ListView listview;
    ArrayAdapter adapter;
    ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogHelper.w(TAG, "Testing Joe");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
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
            mProgressDialog = new ProgressDialog(PlaceholderActivity.this);
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
            // Create the array
            // YQL JSON URL
            String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Fwww.cmclibrary.org%2Fabout-the-library%2Fcontact-us%22%20and%20xpath%3D%22%2F%2Fdiv%5B%40class%3D'component-content%20rt-joomla'%5D%22&format=json&callback=";

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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            ListView lv = (ListView) findViewById(R.id.actresslist);
            lv.setAdapter(new ArrayAdapter<String>(com.example.android.uamp.ui.PlaceholderActivity.this, R.layout.list_item, R.id.actress_name, contactsArray));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String actressName = ((TextView) view.findViewById(R.id.actress_name)).getText().toString();

                    Toast.makeText(getApplicationContext(), actressName, Toast.LENGTH_SHORT).show();
                    position += -1;
                    if (position >= 0) {
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                PlaceholderActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Class activityClass = mDrawerMenuContents.getActivity(position);
                        startActivity(new Intent(PlaceholderActivity.this, activityClass), extras);
                        finish();
                    }

                }
            });
            mProgressDialog.dismiss();
        }
    }

}

