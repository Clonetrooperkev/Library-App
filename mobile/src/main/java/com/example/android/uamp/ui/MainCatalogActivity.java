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
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Placeholder activity for features that are not implemented in this sample, but
 * are in the navigation drawer.
 */
public class MainCatalogActivity extends ActionBarCastActivity {
    private static final String TAG = LogHelper.makeLogTag(MainCatalogActivity.class);
    public String searchresults = "a";
    List<String> CatalogArray = new ArrayList<String>();
    List<String> urlArray = new ArrayList<String>();
    ListView listview;
    ArrayAdapter adapter;
    ProgressDialog mProgressDialog;
    boolean catalogUnavailableError;
    MenuItem searchby_any_MenuItem;
    MenuItem searchby_author_MenuItem;
    MenuItem searchby_ISBN_MenuItem;
    MenuItem searchby_title_MenuItem;
    SearchView searchView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        catalogUnavailableError = false;
        LogHelper.w(TAG, "Testing Joe");
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        searchView.setFocusable(false);

        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        CatalogArray.clear();
        setContentView(R.layout.activity_catalog);
            Bundle extras = intent.getExtras();
            if(extras == null) {
                searchresults= null;
            } else {
                searchresults= extras.getString("SEARCHVALUE");
            }
        initializeToolbar();
        setTitle("Catalog");
        //setContentView(R.layout.activity_placeholder);
        new DownloadJSON().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchby_any:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.searchby_author:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.searchby_title:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.searchby_ISBN:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        MenuItem searchbyMenu = menu.findItem(R.id.menu_searchby);
        SubMenu submenu = searchbyMenu.getSubMenu();
        searchby_any_MenuItem = submenu.findItem(R.id.searchby_any);
        searchby_author_MenuItem = submenu.findItem(R.id.searchby_author);
        searchby_ISBN_MenuItem = submenu.findItem(R.id.searchby_ISBN);
        searchby_title_MenuItem = submenu.findItem(R.id.searchby_title);



            return true;
    }
    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            /*
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData cp = myClipboard.getPrimaryClip();
            ClipData.Item item = cp.getItemAt(0);

            catalogSearchText = item.getText().toString();
            */
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainCatalogActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Catalog Data");
            // Set progressdialog message
            mProgressDialog.setMessage("Fetching...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                doBookSearch();
            }
            catch(Exception e){
                LogHelper.e(TAG,e,"Things be wacky");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            ListView lv = (ListView) findViewById(R.id.cataloglist);
            lv.setAdapter(new ArrayAdapter<String>(MainCatalogActivity.this, R.layout.cataloglist_item, R.id.catalog_name, CatalogArray));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String catalogName = ((TextView) view.findViewById(R.id.catalog_name)).getText().toString();

                    Toast.makeText(getApplicationContext(), catalogName, Toast.LENGTH_SHORT).show();
                    if (position >= 0) {
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                MainCatalogActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Class activityClass = mDrawerMenuContents.getActivity(position);
                        startActivity(new Intent(MainCatalogActivity.this, activityClass), extras);
                        finish();
                    }

                }
            });
            if (catalogUnavailableError) {
                Toast.makeText(getApplicationContext(), "Catalog Information Unavailable", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }

    public void doBookSearch() throws Exception {
        String Qurl = "http://cat.cmclibrary.org/polaris/search/searchresults.aspx?ctx=1.1033.0.0.3&type=Keyword&term=";
        String bookSort = "&limit=TOM=bks";
        String authSort = "&by=AU";
        String titleSort = "&by=TI";
        String ISBNSort = "&by=ISBN";
        if (searchresults == ""){
            searchresults = "b";
        }
        String url1 = "http://cat.cmclibrary.org/polaris/search/components/ajaxResults.aspx?page=1";
        String url = Qurl + URLEncoder.encode(searchresults, "UTF-8");
        //url += bookSort;
        if (searchby_any_MenuItem.isChecked() ){
        }
        if (searchby_title_MenuItem.isChecked() ){
            url+=titleSort;
        }
        if (searchby_author_MenuItem.isChecked() ){
            url+=authSort;
        }
        if (searchby_ISBN_MenuItem.isChecked() ){
            url+=ISBNSort;
        }
        String USER_AGENT = "Chrome/43.0.2357.134";
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        URL obj = new URL(url);
        URL obj1 = new URL(url1);
        HttpURLConnection.setFollowRedirects(true);

        //Make a connection to get cookies
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        Map<String, List<String>> headerFields = con.getHeaderFields();

        //Open the real connection
        HttpURLConnection con1 = (HttpURLConnection) obj.openConnection();
        con1.setRequestProperty("CSP", "active");
        con1.setRequestProperty("Accept", "*/*");
        con1.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        con1.setRequestMethod("GET");
        int responseCode = con1.getResponseCode();

        //Now get the search results
        con1 = (HttpURLConnection) obj1.openConnection();
        responseCode = con1.getResponseCode();
        BufferedReader in1 = new BufferedReader(
                new InputStreamReader(con1.getInputStream()));
        String inputLine1;
        StringBuffer response1 = new StringBuffer();

        while ((inputLine1 = in1.readLine()) != null) {
            response1.append(inputLine1);
        }
        in1.close();
        String str = response1.toString();

        //String xml = "<resp><status>good</status><msg>hi</msg></resp>";



        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(false);

        //URL object

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
        // NodeList nodeList = (NodeList)xpath.compile("//span[@class='nsm-short-item nsm-e134']").evaluate(document, XPathConstants.NODESET);
        NodeList nodeList = (NodeList)xpath.evaluate("//span[@class='nsm-short-item nsm-e135']", document, XPathConstants.NODESET);

        String[] results = new String[nodeList.getLength()];
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            String name = xpath.evaluate(".",node);
            results[index] = name;
            CatalogArray.add(name);

        }
        //String msg

        System.out.println("msg= + ;");


    }

}

