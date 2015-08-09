
package com.example.android.uamp.ui;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.uamp.R;
import com.example.android.uamp.utils.LogHelper;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
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
public class NewCalendarActivity extends ActionBarCastActivity {
    ListView lv;
    Parcelable state = null;
    //private Context context = null;
    private static final String TAG = LogHelper.makeLogTag(MainCatalogActivity.class);
    public String searchresults = "a";
    List<String> CatalogArray = new ArrayList<String>();
    List<String> AuthorArray = new ArrayList<String>();
    List<String> FormatArray = new ArrayList<String>();
    List<String> PicturesArray = new ArrayList<String>();
    List<String> urlArray = new ArrayList<String>();
    ListView listview;
    ArrayAdapter adapter;
    ProgressDialog mProgressDialog;
    boolean catalogUnavailableError;
    MenuItem searchby_any_MenuItem;
    MenuItem searchby_author_MenuItem;
    MenuItem searchby_ISBN_MenuItem;
    MenuItem searchby_title_MenuItem;
    MenuItem format_any_MenuItem;
    MenuItem format_book_MenuItem;
    MenuItem format_large_print_MenuItem;
    MenuItem format_audiobook_MenuItem;
    MenuItem format_audio_ebook_MenuItem;
    MenuItem format_ebook_MenuItem;
    MenuItem format_dvd_MenuItem;
    MenuItem format_Bray_disc_MenuItem;
    MenuItem format_videotape_MenuItem;
    MenuItem format_music_cd_MenuItem;
    MenuItem format_sound_recording_MenuItem;
    MenuItem format_serial_MenuItem;
    SearchView searchView;
    int searchPage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        catalogUnavailableError = false;
        LogHelper.w(TAG, "Testing Joe");
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        handleIntent(getIntent());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        searchView.setFocusable(false);

        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        String tempsearchresults = "";
        String moreButton = "";
        setContentView(R.layout.activity_catalog);
        Bundle extras = intent.getExtras();
        if(extras != null) {
            if(extras.containsKey("SEARCHVALUE")) {
                tempsearchresults= extras.getString("SEARCHVALUE");
            }
            if(extras.containsKey("MOREBUTTON")){
                moreButton= extras.getString("MOREBUTTON");
            }

        }
        if(moreButton.equals("true")){
            searchPage += 1;

        }
        else{
            CatalogArray.clear();
            AuthorArray.clear();
            FormatArray.clear();
            PicturesArray.clear();
            searchPage = 1;
            searchresults = tempsearchresults;
            state = null;
        }


        initializeToolbar();
        setTitle("Catalog");
        //setContentView(R.layout.activity_placeholder);
        new DownloadJSON().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String SelectedMenu;
        if (item.isCheckable()) {
            item.setChecked(!item.isChecked());
            if(item.getOrder() >= 20){
                SelectedMenu = "SearchBy";
            }
            else{
                SelectedMenu = "Format";
            }
            if(item.isChecked()){
                SharedPreferences settings = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(SelectedMenu, item.getItemId());
                editor.commit();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem searchByItem;
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
        MenuItem formatSearch = menu.findItem(R.id.menu_format);
        SubMenu subformatmenu = formatSearch.getSubMenu();
        format_any_MenuItem = subformatmenu.findItem(R.id.format_any);
        format_book_MenuItem = subformatmenu.findItem(R.id.format_book);
        format_large_print_MenuItem = subformatmenu.findItem(R.id.format_large_print);
        format_audiobook_MenuItem = subformatmenu.findItem(R.id.format_audiobook);
        format_audio_ebook_MenuItem = subformatmenu.findItem(R.id.format_audio_ebook);
        format_ebook_MenuItem = subformatmenu.findItem(R.id.format_ebook);
        format_dvd_MenuItem = subformatmenu.findItem(R.id.format_dvd);
        format_Bray_disc_MenuItem = subformatmenu.findItem(R.id.format_Bray_disc);
        format_videotape_MenuItem = subformatmenu.findItem(R.id.format_videotape);
        format_music_cd_MenuItem = subformatmenu.findItem(R.id.format_music_cd);
        format_sound_recording_MenuItem = subformatmenu.findItem(R.id.format_sound_recording);
        format_serial_MenuItem = subformatmenu.findItem(R.id.format_serial);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        int CheckedItem = settings.getInt("SearchBy", 0);
        if(CheckedItem != 0){
            searchByItem = menu.findItem(CheckedItem);
        }
        else{
            searchByItem = searchby_any_MenuItem;
        }
        searchByItem.setChecked(true);
        CheckedItem = settings.getInt("Format", 0);
        if(CheckedItem != 0){
            searchByItem = menu.findItem(CheckedItem);
        }
        else{
            searchByItem = format_any_MenuItem;
        }
        searchByItem.setChecked(true);

        return true;
    }
    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(NewCalendarActivity.this);
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
                doCalSearch();
            }
            catch(Exception e){
                LogHelper.e(TAG,e,"Things be wacky");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            CatalogListAdapter adapter = new CatalogListAdapter(NewCalendarActivity.this, R.layout.cataloglist_item, CatalogArray, AuthorArray, FormatArray, PicturesArray);
            lv = (ListView)findViewById(R.id.cataloglist);
            lv.setAdapter(adapter);
            if(state != null){
                lv.onRestoreInstanceState(state);
            }



            if(CatalogArray.size() != 0) {
                View footerView = ((LayoutInflater) NewCalendarActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
                lv.addFooterView(footerView);
                Button forward = (Button) footerView.findViewById(R.id.loadMore);
                forward.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        state = lv.onSaveInstanceState();
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                        Intent searchIntent = new Intent(NewCalendarActivity.this, MainCatalogActivity.class);
                        searchIntent.putExtra("MOREBUTTON", "true");
                        searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(searchIntent, extras);
                    }
                });
            }




            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String catalogName = ((TextView) view.findViewById(R.id.catalog_name)).getText().toString();

                    Toast.makeText(getApplicationContext(), catalogName, Toast.LENGTH_SHORT).show();
                    if (position >= 0) {
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Class activityClass = mDrawerMenuContents.getActivity(position);
                        startActivity(new Intent(NewCalendarActivity.this, activityClass), extras);
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

    public void doCalSearch() throws Exception {
        String Qurl = "http://cat.cmclibrary.org/polaris/search/searchresults.aspx?ctx=1.1033.0.0.3&type=Keyword&term=";
        //String bookSort = "&limit=TOM=bks";
        if (searchresults == ""){
            searchresults = "b";
        }
        String url1 = "http://events.cmclibrary.org/eventcalendar.asp";
        String url = Qurl + URLEncoder.encode(searchresults, "UTF-8");
        //url += bookSort;

        if (searchby_any_MenuItem.isChecked() ){
        }
        if (searchby_title_MenuItem.isChecked() ){
            url+="&by=TI";
        }
        if (searchby_author_MenuItem.isChecked() ){
            url+="&by=AU";
        }
        if (searchby_ISBN_MenuItem.isChecked() ){
            url+="&by=ISBN";
        }
        if(format_any_MenuItem.isChecked()){

        }
        if(format_book_MenuItem.isChecked()){
            url += "&limit=TOM=bks";
        }
        if(format_large_print_MenuItem.isChecked()){
            url += "&limit=TOM=lpt";
        }
        if(format_audiobook_MenuItem.isChecked()){
            url += "&limit=TOM=abk";
        }
        if(format_audio_ebook_MenuItem.isChecked()){
            url += "&limit=TOM=aeb";
        }
        if(format_ebook_MenuItem.isChecked()){
            url += "&limit=TOM=ebk";
        }
        if(format_dvd_MenuItem.isChecked()){
            url += "&limit=TOM=dvd";
        }
        if(format_Bray_disc_MenuItem.isChecked()){
            url += "&limit=TOM=brd";
        }
        if(format_videotape_MenuItem.isChecked()){
            url += "&limit=TOM=vcr";
        }
        if(format_music_cd_MenuItem.isChecked()){
            url += "&limit=TOM=mcd";
        }
        if(format_sound_recording_MenuItem.isChecked()){
            url += "&limit=TOM=rec";
        }
        if(format_serial_MenuItem.isChecked()){
            url += "&limit=TOM=ser";
        }
        String USER_AGENT = "Chrome/43.0.2357.134";
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        URL obj = new URL(url);
        URL obj1 = new URL(url1);
        HttpURLConnection.setFollowRedirects(true);

        //Make a connection to get cookies
        HttpURLConnection con = (HttpURLConnection) obj1.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        Map<String, List<String>> headerFields = con.getHeaderFields();

        //Open the real connection
        HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();
        con1.setRequestProperty("CSP", "active");
        con1.setRequestProperty("Accept", "*/*");
        con1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        con1.setUseCaches(false);
        con1.setDoInput(true);
        con1.setDoOutput(true);
        con1.setChunkedStreamingMode(0);
        String urlParameters1 =
                "DispType=" + URLEncoder.encode("list", "UTF-8") +
                        "&dt=" + URLEncoder.encode("range", "UTF-8")+
                        "&ds=" + URLEncoder.encode("2015-12-1", "UTF-8")+
                        "&de=" + URLEncoder.encode("2015-12-2", "UTF-8");
        //String newUrlParams = "plink_public_url=&plink_public_dyn_url=&plink_staff_url=&rsslink_public_dyn_url=&rsslink_staff_url=&saved_year=2015&weekstart=12%2F1%2F2015&view_date=&viewIndividualDate=&LangType=0&pageTracker=&perPageDispTracker=&WindowMode=&noheader=&lad=&transfer=&SearchState=&OngoingState=&PageTitleStr=&keyword=&DispType=list&CURDATE_month=December&CURDATE_YEAR=2015&dt=range&ds=2015-12-1&de=2015-12-2&sd=&Lib=0&AllTypes=ALL&EventType=Arts+and+Crafts+Program&EventType=Author+Visit&EventType=Book+Club&EventType=Book+Sale&EventType=Bookmobile&EventType=Computer+Class&EventType=Education+Program&EventType=Food%2FCookery+Program&EventType=Gaming+Events&EventType=Garden+Program&EventType=Health%2FExercise+Program&EventType=Holiday&EventType=Maker+Program&EventType=Movie+Nights&EventType=Music+Program&EventType=Natural+History+Program&EventType=Organization+Meeting&EventType=Other&EventType=Storytime&EventType=Summer+Reading&EventType=Workshops&AllAgeGroups=ALL&AgeGroup=Family&AgeGroup=Teen&AgeGroup=Children&AgeGroup=Adult&SaveDispType=week&TotalEvents=21&TotalAgeGroups=4&TotalLibs=9&SavePointer=&returnToSearch=&CalNum=0&ad=&direction=&nopw=&qurl=";

        String urlParameters2 =
                "DispType=" + URLEncoder.encode("list", "UTF-8") +
                        "&date_type=" + URLEncoder.encode("range", "UTF-8")+
                        "&dr1Month=" + URLEncoder.encode("12", "UTF-8")+
                        "&dr1Day=" + URLEncoder.encode("1", "UTF-8")+
                        "&dr1Year=" + URLEncoder.encode("2015", "UTF-8")+
                        "&dr2Month=" + URLEncoder.encode("12", "UTF-8")+
                        "&dr2Day=" + URLEncoder.encode("31", "UTF-8")+
                        "&dr2Year=" + URLEncoder.encode("2015", "UTF-8");

        con1.setRequestMethod("POST");

        //Send request
        DataOutputStream wr = new DataOutputStream (
                con1.getOutputStream ());
        wr.writeBytes(urlParameters2);
        wr.flush();
        wr.close();


        //int responseCode = con1.getResponseCode();

        //Now get the search results
        //con1 = (HttpURLConnection) obj1.openConnection();
        //responseCode = con1.getResponseCode();
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
        Node testNode1 = (Node)xpath.evaluate("//div[@id = 'divBody']", document, XPathConstants.NODE);
        NodeList testNode = (NodeList)xpath.evaluate(".//table[@class = 'event']/tbody/tr[2]", testNode1, XPathConstants.NODESET);
        for (int index = 0; index < testNode.getLength(); index++) {
            Node anode = testNode.item(index);
            //Node tNode = (Node)xpath.evaluate(".//td[@class = 'event_values']", anode, XPathConstants.NODE);
            Node subNode = (Node)xpath.evaluate(".//span[@class='event_title_list_special_class']", anode, XPathConstants.NODE);
            String testName = xpath.evaluate(".", subNode);
            if(testName != "") {
                CatalogArray.add(testName);
                subNode = (Node)xpath.evaluate(".//span[@class='event_title_list_special_class']", anode, XPathConstants.NODE);
                String testAuth = xpath.evaluate(".",subNode);
                AuthorArray.add(testAuth);
                subNode = (Node)xpath.evaluate(".//text()", anode, XPathConstants.NODE);
                String testForm = xpath.evaluate(".",subNode);
                FormatArray.add(testForm);
                String testPic = "";
                if((subNode = (Node)xpath.evaluate(".//img", anode, XPathConstants.NODE)) != null){
                    testPic = "http://events.cmclibrary.org/"+ xpath.evaluate("./@src",subNode);


                }
                if(testPic == ""){
                    testPic= "/";
                }
                PicturesArray.add(testPic);
            }
        }

    }

}

