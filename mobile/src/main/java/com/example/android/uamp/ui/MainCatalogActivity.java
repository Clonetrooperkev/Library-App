
package com.example.android.uamp.ui;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.uamp.R;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Placeholder activity for features that are not implemented in this sample, but
 * are in the navigation drawer.
 */
public class MainCatalogActivity extends ActionBarCastActivity{
    private String detailsURL = "";
    private String detailsTitle;
    String detailsSummary;
    private String detailsAvailability= "";
    private ListView lv;
    private Parcelable state = null;
    private boolean firstDetailSearch;
    //private Context context = null;
    private String searchresults = "a";
    private List<String> CatalogDetailsURLArray = new ArrayList<String>();
    private List<String> CatalogArray = new ArrayList<String>();
    private List<String> AuthorArray = new ArrayList<String>();
    private List<String> FormatArray = new ArrayList<String>();
    private List<String> PicturesArray = new ArrayList<String>();
    List<String> urlArray = new ArrayList<String>();
    ListView listview;
    ArrayAdapter adapter;
    private ProgressDialog mProgressDialog;
    private boolean catalogUnavailableError;
    private MenuItem searchby_any_MenuItem;
    private MenuItem searchby_author_MenuItem;
    private MenuItem searchby_ISBN_MenuItem;
    private MenuItem searchby_title_MenuItem;
    private MenuItem format_any_MenuItem;
    private MenuItem format_book_MenuItem;
    private MenuItem format_large_print_MenuItem;
    private MenuItem format_audiobook_MenuItem;
    private MenuItem format_audio_ebook_MenuItem;
    private MenuItem format_ebook_MenuItem;
    private MenuItem format_dvd_MenuItem;
    private MenuItem format_Bray_disc_MenuItem;
    private MenuItem format_videotape_MenuItem;
    private MenuItem format_music_cd_MenuItem;
    private MenuItem format_sound_recording_MenuItem;
    private MenuItem format_serial_MenuItem;
    private SearchView searchView;
    private View footerView;
    private int searchPage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        firstDetailSearch = true;
        detailsURL = "";
        catalogUnavailableError = false;
        super.onCreate(savedInstanceState);

        //Every time catalog is entered: set "search by" and "format" to "any"
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        handleIntent(getIntent());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        searchView.setFocusable(false);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String tempsearchresults = "";
        String moreButton = "";
        Bundle extras = intent.getExtras();
        if(extras != null) {
            if(extras.containsKey("SEARCHVALUE")) {
                tempsearchresults= extras.getString("SEARCHVALUE");
            }
            if(extras.containsKey("MOREBUTTON")){
                moreButton= extras.getString("MOREBUTTON");
            }
            if(extras.containsKey("DETAILS")){
                detailsURL = CatalogDetailsURLArray.get(extras.getInt("DETAILS"));
                detailsAvailability = "";
            }

        }
        if(moreButton.equals("true")){
            searchPage += 1;

        }
        else{
            if(detailsURL.equals("")){
                setContentView(R.layout.activity_catalog);
                initializeToolbar();
                setTitle("Catalog");
                CatalogArray.clear();
                AuthorArray.clear();
                FormatArray.clear();
                PicturesArray.clear();
                CatalogDetailsURLArray.clear();
                searchPage = 1;
                searchresults = tempsearchresults;
                state = null;
                footerView = null;
                detailsAvailability = "";
            }

        }


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
                editor.apply();
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
        inflater.inflate(R.menu.search_menu_catalog, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //Restores search result in search field
        if (!TextUtils.isEmpty(searchresults)) {
            searchView.setQuery(searchresults, false);
        }
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
                if(detailsURL.equals("")){
                    doBookSearch();
                }
                else{
                    doDetailSearch();
                }
            }
            catch(Exception e){
                //LogHelper.e(TAG,e,"Things be wacky");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            CatalogListAdapter adapter = new CatalogListAdapter(MainCatalogActivity.this, CatalogArray, AuthorArray, FormatArray, PicturesArray);
            lv = (ListView)findViewById(R.id.cataloglist);
            lv.setAdapter(adapter);
            if(state != null){
                lv.onRestoreInstanceState(state);
            }


            if(!detailsURL.equals("")) {
                showDetails();
            }
            if(CatalogArray.size() != 0) {
                if(footerView == null){
                    footerView = ((LayoutInflater) MainCatalogActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);

                    lv.addFooterView(footerView);
                    Button forward = (Button) footerView.findViewById(R.id.loadMore);
                    forward.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            state = lv.onSaveInstanceState();
                            Bundle extras = ActivityOptions.makeCustomAnimation(
                                    MainCatalogActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                            Intent searchIntent = new Intent(MainCatalogActivity.this, MainCatalogActivity.class);
                            searchIntent.putExtra("MOREBUTTON", "true");
                            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(searchIntent, extras);
                        }
                    });
                }

            }




            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0) {
                        state = lv.onSaveInstanceState();
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                MainCatalogActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                        Intent searchIntent = new Intent(MainCatalogActivity.this, MainCatalogActivity.class);
                        searchIntent.putExtra("DETAILS", position);
                        searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(searchIntent, extras);
                    }

                }
            });
            if (catalogUnavailableError) {
                Toast.makeText(getApplicationContext(), "Catalog Information Unavailable", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }
    private void showDetails() {
        // Inflate your custom layout containing 2 DatePickers
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.catalog_details, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customView); // Set the view of the dialog to your custom layout
        //builder.setTitle("Select start and end date");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                detailsURL = "";

            }
        });

        // Create and show the dialog
        TextView tView = (TextView) customView.findViewById(R.id.catalog_details_title);
        tView.setText(detailsTitle);
        tView = (TextView) customView.findViewById(R.id.catalog_details_availability);
        tView.setMovementMethod(new ScrollingMovementMethod());
        tView.setText(detailsAvailability);
        builder.create().show();
    }
    private void doBookSearch() throws Exception {
        String Qurl = "http://cat.cmclibrary.org/polaris/search/searchresults.aspx?ctx=1.1033.0.0.3&type=Keyword&term=";
        //String bookSort = "&limit=TOM=bks";
        if (searchresults == ""){
            searchresults = "";
        }
        String url1 = "http://cat.cmclibrary.org/polaris/search/components/ajaxResults.aspx?page=";
        url1 += String.valueOf(searchPage);
        String url = Qurl + URLEncoder.encode(searchresults, "UTF-8");
        //url += bookSort;
        if ( (searchby_any_MenuItem == null) || (searchby_any_MenuItem.isChecked() ) ){
        }
        else {
            if (searchby_title_MenuItem.isChecked()) {
                url += "&by=TI";
            }
            if (searchby_author_MenuItem.isChecked()) {
                url += "&by=AU";
            }
            if (searchby_ISBN_MenuItem.isChecked()) {
                url += "&by=ISBN";
            }
        }
        if( (format_any_MenuItem == null) || format_any_MenuItem.isChecked() ){

        }
        else {
            if (format_book_MenuItem.isChecked()) {
                url += "&limit=TOM=bks";
            }
            if (format_large_print_MenuItem.isChecked()) {
                url += "&limit=TOM=lpt";
            }
            if (format_audiobook_MenuItem.isChecked()) {
                url += "&limit=TOM=abk";
            }
            if (format_audio_ebook_MenuItem.isChecked()) {
                url += "&limit=TOM=aeb";
            }
            if (format_ebook_MenuItem.isChecked()) {
                url += "&limit=TOM=ebk";
            }
            if (format_dvd_MenuItem.isChecked()) {
                url += "&limit=TOM=dvd";
            }
            if (format_Bray_disc_MenuItem.isChecked()) {
                url += "&limit=TOM=brd";
            }
            if (format_videotape_MenuItem.isChecked()) {
                url += "&limit=TOM=vcr";
            }
            if (format_music_cd_MenuItem.isChecked()) {
                url += "&limit=TOM=mcd";
            }
            if (format_sound_recording_MenuItem.isChecked()) {
                url += "&limit=TOM=rec";
            }
            if (format_serial_MenuItem.isChecked()) {
                url += "&limit=TOM=ser";
            }
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
        Node testNode1 = (Node)xpath.evaluate("//table[@width = '100%']/tbody", document, XPathConstants.NODE);
        NodeList testNode = (NodeList)xpath.evaluate("./tr", testNode1, XPathConstants.NODESET);
        for (int index = 0; index < testNode.getLength(); index++) {
            Node anode = testNode.item(index);
            Node subNode = (Node)xpath.evaluate(".//span[@class='nsm-short-item nsm-e135']", anode, XPathConstants.NODE);
            String testName = xpath.evaluate(".", subNode);
            if(testName != "") {
                CatalogArray.add(testName);
                subNode = (Node)xpath.evaluate(".//span[@class='nsm-short-item nsm-e118']", anode, XPathConstants.NODE);
                String testAuth = xpath.evaluate(".",subNode);
                AuthorArray.add(testAuth);
                subNode = (Node)xpath.evaluate(".//span[@class='nsm-short-item nsm-e249']", anode, XPathConstants.NODE);
                String testForm = xpath.evaluate(".",subNode);
                FormatArray.add(testForm);
                //subNode = (Node) xpath.evaluate(".//a[@class='nsm-brief-action-link']/@href", anode, XPathConstants.NODE);
                subNode = (Node) xpath.evaluate(".//a[@class='results-title-link-avail']/@href", anode, XPathConstants.NODE);
                String testURL = xpath.evaluate(".", subNode);
                CatalogDetailsURLArray.add(testURL);
                subNode = (Node)xpath.evaluate(".//img[@class='thumbnail']/@src", anode, XPathConstants.NODE);
                String testPic = xpath.evaluate(".",subNode);
                if(testPic == ""){
                    testPic= "/";
                }
                PicturesArray.add(testPic);
            }
        }

    }
    private void doDetailSearch() throws Exception {
        String USER_AGENT = "Chrome/43.0.2357.134";
        URL obj1 = new URL(detailsURL);
        HttpURLConnection.setFollowRedirects(true);
        //Get session ID if necessary
        if(firstDetailSearch) {
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
            firstDetailSearch = false;
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
        Node testNode1 = (Node) xpath.evaluate("/html/body/div[1]/b", document, XPathConstants.NODE);
        detailsTitle = xpath.evaluate(".", testNode1);
        NodeList testNode = (NodeList)xpath.evaluate("//td[@class='location']", testNode1, XPathConstants.NODESET);
        int i = testNode.getLength();
        for (int index = 0; index < testNode.getLength(); index++) {
            Node anode = testNode.item(index);
            detailsAvailability += xpath.evaluate(".",anode) + "\r\n";

        }
    }

}

