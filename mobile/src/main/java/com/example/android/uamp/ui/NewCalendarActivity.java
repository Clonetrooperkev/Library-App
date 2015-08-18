
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class NewCalendarActivity extends ActionBarCastActivity {
    private String detailsURL = "";
    private String detailsName;
    private String detailsDescription;
    private String detailsInfo;
    private ListView lv;
    private Parcelable state = null;
    private int startDay = 0;
    private int startMonth = 0;
    private int startYear = 0;
    private int endDay = 0;
    private int endMonth = 0;
    private int endYear = 0;
    private String searchresults = "a";
    private List<String> CalendarNameArray = new ArrayList<>();
    private List<String> CalendarDetailsURLArray = new ArrayList<>();
    private List<String> LocationArray = new ArrayList<>();
    private List<String> DateArray = new ArrayList<>();
    private List<String> PicturesArray = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private MenuItem search_byAll;
    private MenuItem menu_bookmobile;
    private MenuItem menu_cape_may_city_library;
    private MenuItem menu_cape_may_courthouse_library;
    private MenuItem menu_lower_cape_library;
    private MenuItem menu_sea_isle_city_library;
    private MenuItem menu_stone_harbor;
    private MenuItem menu_upper_cape_library;
    private MenuItem menu_wildwood_crest_library;
    private MenuItem menu_woodbine_library;
    private SearchView searchView;
    private View footerView;
    private int searchPage;
    private boolean firstCalendarSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //detailsURL controls the state of the activity
        //If detailsURL is not blank, perform a detail search
        detailsURL = "";

        firstCalendarSearch = true;
        final Calendar currentCal = Calendar.getInstance();
        endYear = (startYear = currentCal.get(Calendar.YEAR));
        endMonth = (startMonth = currentCal.get(Calendar.MONTH) + 1) + 1;
        endDay = (startDay = currentCal.get(Calendar.DAY_OF_MONTH));
        if (endMonth == 13) {
            endMonth = 1;
            endYear += 1;
        }

        super.onCreate(savedInstanceState);

        //Every time calendar is entered: set "searchbyAll" to "any"
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
        if (extras != null) {
            if (extras.containsKey("SEARCHVALUE")) {
                tempsearchresults = extras.getString("SEARCHVALUE");
            }
            if (extras.containsKey("MOREBUTTON")) {
                moreButton = extras.getString("MOREBUTTON");
            }
            if(extras.containsKey("DETAILS")){
                detailsURL = "http://events.cmclibrary.org/" + CalendarDetailsURLArray.get(extras.getInt("DETAILS"));
            }

        }
        assert moreButton != null;
        if (moreButton.equals("true")) {
            searchPage += 1;

        } else {
            if(detailsURL.equals("")){
                setContentView(R.layout.activity_calendar);
                initializeToolbar();
                setTitle("Calendar");
                CalendarNameArray.clear();
                LocationArray.clear();
                DateArray.clear();
                PicturesArray.clear();
                CalendarDetailsURLArray.clear();
                searchPage = 1;
                footerView = null;
                searchresults = tempsearchresults;
                state = null;
            }
        }
        new fetchCalendarData().execute();
    }
    private void showDetails() {
        // Inflate your custom layout containing 2 DatePickers
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.calendar_details, null);

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
        TextView tView = (TextView) customView.findViewById(R.id.calendar_details_name);
        tView.setText(detailsName);
        tView = (TextView) customView.findViewById(R.id.calendar_details_info);
        tView.setMovementMethod(new ScrollingMovementMethod());
        tView.setText(detailsInfo);
        tView = (TextView) customView.findViewById(R.id.calendar_details_description);
        tView.setMovementMethod(new ScrollingMovementMethod());
        tView.setText(detailsDescription);
        builder.create().show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((item.getItemId() == R.id.menu_range)) {
            showDatePicker();
        }
        if (item.isCheckable()) {
            item.setChecked(!item.isChecked());
            if(item.isChecked()){
                SharedPreferences settings = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Selected_Library", item.getItemId());
                editor.apply();
            }
            Bundle extras = ActivityOptions.makeCustomAnimation(
                    NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
            Intent searchIntent = new Intent(NewCalendarActivity.this, NewCalendarActivity.class);
            searchIntent.putExtra("SEARCHVALUE", "");
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(searchIntent, extras);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        // Inflate your custom layout containing 2 DatePickers
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.calendar_datepicker, null);

        // Define your date pickers
        final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);
        final DatePicker dpEndDate = (DatePicker) customView.findViewById(R.id.dpEndDate);
        dpStartDate.updateDate(startYear, startMonth - 1, startDay);
        dpEndDate.updateDate(endYear, endMonth - 1, endDay);
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customView); // Set the view of the dialog to your custom layout
        //builder.setTitle("Select start and end date");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startYear = dpStartDate.getYear();
                startMonth = dpStartDate.getMonth() + 1;
                startDay = dpStartDate.getDayOfMonth();
                endYear = dpEndDate.getYear();
                endMonth = dpEndDate.getMonth() + 1;
                endDay = dpEndDate.getDayOfMonth();
                dialog.dismiss();
                Bundle extras = ActivityOptions.makeCustomAnimation(
                        NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                Intent searchIntent = new Intent(NewCalendarActivity.this, NewCalendarActivity.class);
                searchIntent.putExtra("SEARCHVALUE", "");
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(searchIntent, extras);
            }
        });

        // Create and show the dialog
        builder.create().show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem searchByItem;
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu_calendar, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        MenuItem searchbyMenu = menu.findItem(R.id.menu_searchLib);
        SubMenu submenu = searchbyMenu.getSubMenu();
        search_byAll = submenu.findItem(R.id.menu_searchAllLib);
        menu_bookmobile = submenu.findItem(R.id.menu_bookmobile);
        menu_cape_may_city_library = submenu.findItem(R.id.menu_cape_may_city_library);
        menu_cape_may_courthouse_library = submenu.findItem(R.id.menu_cape_may_courthouse_library);
        menu_lower_cape_library = submenu.findItem(R.id.menu_lower_cape_library);
        menu_sea_isle_city_library = submenu.findItem(R.id.menu_sea_isle_city_library);
        menu_stone_harbor = submenu.findItem(R.id.menu_stone_harbor_library);
        menu_upper_cape_library = submenu.findItem(R.id.menu_upper_cape_library);
        menu_wildwood_crest_library = submenu.findItem(R.id.menu_wildwood_crest_library);
        menu_woodbine_library = submenu.findItem(R.id.menu_woodbine_library);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        int CheckedItem = settings.getInt("Selected_Library", 0);
        if (CheckedItem != 0) {
            searchByItem = menu.findItem(CheckedItem);
        } else {
            searchByItem = search_byAll;
        }
        searchByItem.setChecked(true);
        return true;

    }

    // DownloadJSON AsyncTask
    private class fetchCalendarData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(NewCalendarActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Calendar Data");
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
                    doCalSearch();
                }
                else{
                    doDetailSearch();
                }

            } catch (Exception e) {
                //LogHelper.e(TAG, e, "Things be wacky");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            CalendarListAdapter adapter = new CalendarListAdapter(NewCalendarActivity.this, CalendarNameArray, LocationArray, DateArray, PicturesArray);
            lv = (ListView) findViewById(R.id.calendarlist);
            lv.setAdapter(adapter);
            if (state != null) {
                lv.onRestoreInstanceState(state);
            }

            if(!detailsURL.equals("")) {
                showDetails();
            }
            if (CalendarNameArray.size() != 0) {
                if(footerView == null){
                    footerView = ((LayoutInflater) NewCalendarActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
                    lv.addFooterView(footerView);
                    Button forward = (Button) footerView.findViewById(R.id.loadMore);
                    forward.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            state = lv.onSaveInstanceState();
                            Bundle extras = ActivityOptions.makeCustomAnimation(
                                    NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                            Intent searchIntent = new Intent(NewCalendarActivity.this, NewCalendarActivity.class);
                            searchIntent.putExtra("MOREBUTTON", "true");
                            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(searchIntent, extras);
                        }
                    });
                }

            }
            else{
                Toast.makeText(getApplicationContext(), "Calendar Information Unavailable", Toast.LENGTH_LONG).show();
            }


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0) {
                        state = lv.onSaveInstanceState();
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                        Intent searchIntent = new Intent(NewCalendarActivity.this, NewCalendarActivity.class);
                        searchIntent.putExtra("DETAILS", position);
                        searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(searchIntent, extras);
                    }

                }
            });
            mProgressDialog.dismiss();
        }
    }

    private void doCalSearch() throws Exception {
        String url1 = "http://events.cmclibrary.org/eventcalendar.asp";
        Log.e("HTTP", "1");
        String USER_AGENT = "Chrome/43.0.2357.134";
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        URL obj1 = new URL(url1);
        HttpURLConnection.setFollowRedirects(true);
        Log.e("HTTP", "2");

        //Make a connection to get cookies
        if (firstCalendarSearch) {
            HttpURLConnection con = (HttpURLConnection) obj1.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            Map<String, List<String>> headerFields = con.getHeaderFields();
            con.setConnectTimeout(1000);
            firstCalendarSearch = false;

        }
        Log.e("HTTP", "3");
        //Open the real connection
        HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();
        Log.e("HTTP", "4");
        con1.setRequestProperty("CSP", "active");
        con1.setRequestProperty("Accept", "*/*");
        con1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        con1.setUseCaches(false);
        con1.setDoInput(true);
        con1.setDoOutput(true);
        con1.setChunkedStreamingMode(0);
        con1.setConnectTimeout(1000);
        String searchbyString = "";
        if( (search_byAll == null) || (search_byAll.isChecked()) ){
            searchbyString = "&AllLibs=" + URLEncoder.encode("ALL", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("0", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("1", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("2", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("3", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("4", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("4", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("5", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("6", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("7", "UTF-8")+
                    "&Lib=" + URLEncoder.encode("8", "UTF-8");
        }
        else{
            if(menu_cape_may_courthouse_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("0", "UTF-8");
            }
            if(menu_cape_may_city_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("1", "UTF-8");
            }
            if(menu_lower_cape_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("2", "UTF-8");
            }
            if(menu_sea_isle_city_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("3", "UTF-8");
            }
            if(menu_stone_harbor.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("4", "UTF-8");
            }
            if(menu_upper_cape_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("5", "UTF-8");
            }
            if(menu_wildwood_crest_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("6", "UTF-8");
            }
            if(menu_woodbine_library.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("7", "UTF-8");
            }
            if(menu_bookmobile.isChecked()){
                searchbyString = "&Lib=" + URLEncoder.encode("8", "UTF-8");
            }
        }
            String urlParameters1 =
                    "DispType=" + URLEncoder.encode("list", "UTF-8") +
                            //"perPageDispTracker=" +URLEncoder.encode("10", "UTF-8")+
                            "&date_type=" + URLEncoder.encode("range", "UTF-8") +
                            "&dr1Month=" + URLEncoder.encode(Integer.toString(startMonth), "UTF-8") +
                            "&dr1Day=" + URLEncoder.encode(Integer.toString(startDay), "UTF-8") +
                            "&dr1Year=" + URLEncoder.encode(Integer.toString(startYear), "UTF-8") +
                            "&dr2Month=" + URLEncoder.encode(Integer.toString(endMonth), "UTF-8") +
                            "&dr2Day=" + URLEncoder.encode(Integer.toString(endDay), "UTF-8") +
                            "&dr2Year=" + URLEncoder.encode(Integer.toString(endYear), "UTF-8") +
                            "&keyword=" + URLEncoder.encode(searchresults, "UTF-8")+ searchbyString;

            String urlParameters2 =
                    "DispType=" + URLEncoder.encode("list", "UTF-8") +
                            "&pageTracker=" + URLEncoder.encode(Integer.toString(searchPage), "UTF-8") +
                            "&SaveDispType=" + URLEncoder.encode("list", "UTF-8") +
                            "&perPageDispTracker=" + URLEncoder.encode("25", "UTF-8") +
                            "&dt=" + URLEncoder.encode("range", "UTF-8") +
                            "&ds=" + URLEncoder.encode(Integer.toString(startYear) + "-" + Integer.toString(startMonth) + "-" + Integer.toString(startDay), "UTF-8") +
                            "&de=" + URLEncoder.encode(Integer.toString(endYear) + "-" + Integer.toString(endMonth) + "-" + Integer.toString(endDay), "UTF-8") +
                            "&keyword=" + URLEncoder.encode(searchresults, "UTF-8")+ searchbyString;

        con1.setRequestMethod("POST");
        Log.e("HTTP", "5");

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    con1.getOutputStream());
            if (searchPage == 1) {
                wr.writeBytes(urlParameters1);
            } else {
                wr.writeBytes(urlParameters2);

            }
            wr.flush();
            wr.close();
        Log.e("HTTP", "6");

            BufferedReader in1 = new BufferedReader(
                    new InputStreamReader(con1.getInputStream()));
            String inputLine1;
            StringBuilder response1 = new StringBuilder();

            while ((inputLine1 = in1.readLine()) != null) {
                response1.append(inputLine1);
            }
            in1.close();
            String str = response1.toString();
        int i = 0;
        int a1;
        int a0;
        int a2;
        String string1 = str;
        do{
            a0 = string1.indexOf("td class=\"event_values\"");
            string1 = string1.substring(a0 + 1, string1.length());
            i = i+1;
            String test  = string1;

            a1 = test.indexOf("<img src");
            String tempstr = test.substring(a1+10,a1+60);
            if(tempstr != null){
                a2 = tempstr.indexOf("\"");
                if(a2 != -1){
                    PicturesArray.add("http://events.cmclibrary.org/" + tempstr.substring(0,a2));
                }
                else{
                    PicturesArray.add("/");
                }
            }
            Log.e("HTTP:", PicturesArray.get(PicturesArray.size()-1));


            a1 = test.indexOf("<a href");
            tempstr = test.substring(a1+9,a1+400);
            if(tempstr != null){
                a2 = tempstr.indexOf("\"");
                if(a2 != -1){
                    CalendarDetailsURLArray.add(tempstr.substring(0,a2));
                }
                else{
                    CalendarDetailsURLArray.add(" ");
                }
            }
            Log.e("HTTP:", CalendarDetailsURLArray.get(CalendarDetailsURLArray.size()-1));

            a1 = test.indexOf("event_title_list_special");
            //System.out.println("Event Index:" + Integer.toString(a1));
            tempstr = test.substring(a1 + 69, a1 + 200);
            if(tempstr != null){
                a2 = tempstr.indexOf("<");
                if(a2 != -1){
                    CalendarNameArray.add(tempstr.substring(0,a2));
                }
                else{
                    CalendarNameArray.add(" ");
                }
            }
            Log.e("HTTP:", CalendarNameArray.get(CalendarNameArray.size()-1));

            a1 = test.indexOf("Date:");
            //System.out.println("Date Index:" + Integer.toString(a1));
            tempstr = test.substring(a1 + 10, a1 + 200);

            if(tempstr != null){
                a2 = tempstr.indexOf("<");
                if(a2 != -1){
                    DateArray.add(tempstr.substring(0, a2));
                }
                else{
                    DateArray.add(" ");
                }
            }
            Log.e("HTTP:", DateArray.get(DateArray.size()-1));

            a1 = test.indexOf("Library:");
            //System.out.println("Library Index:" + Integer.toString(a1));
            tempstr = test.substring(a1 + 12, a1 + 200);

            if(tempstr != null){
                a2 = tempstr.indexOf("<");
                if(a2 != -1){
                    LocationArray.add(tempstr.substring(0,a2));
                }
                else{
                    LocationArray.add(" ");
                }
            }
            Log.e("HTTP:", LocationArray.get(LocationArray.size()-1));
            if(i==24){
                Log.e("HTTP", "Yo");
            }

        }while(i < 25);
/*
        //System.out.println("Image Index:" + Integer.toString(a1));
        String tempstr = str.substring(a1+9,a1+60);
        tempstr = tempstr.substring(0,tempstr.indexOf(" "));
        System.out.println("Image:" + tempstr);
        Log.e("HTTP", "7");

            HtmlCleaner cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setAllowHtmlInsideAttributes(true);
            props.setAllowMultiWordAttributes(true);
            props.setRecognizeUnicodeChars(true);
            props.setOmitComments(false);

            //HTML page root node
            TagNode root = cleaner.clean(str);
            cleaner.getInnerHtml(root);
        Log.e("HTTP", "8");

            String html = "<" + root.getName() + ">" + cleaner.getInnerHtml(root) + "</" + root.getName() + ">";
            InputSource source = new InputSource(new StringReader(html));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
        Log.e("HTTP", "8A");
            Node testNode1 = (Node) xpath.evaluate("//div[@id = 'divBody']", document, XPathConstants.NODE);
        Log.e("HTTP", "8B");
        NodeList testNode = (NodeList) xpath.evaluate(".//table[@class = 'event']/tbody/tr[2]", testNode1, XPathConstants.NODESET);
        Log.e("HTTP", "8C");
        for (int index = 0; index < testNode.getLength(); index++) {
            Log.e("HTTP", "8X");
            Node anode = testNode.item(index);
                //Node tNode = (Node)xpath.evaluate(".//td[@class = 'event_values']", anode, XPathConstants.NODE);
                Node subNode = (Node) xpath.evaluate(".//span[@class='event_title_list_special_class']", anode, XPathConstants.NODE);
                String testName = xpath.evaluate(".", subNode);
                if (!Objects.equals(testName, "")) {
                    CalendarNameArray.add(testName);
                    subNode = (Node) xpath.evaluate(".//td/text()[preceding-sibling::br[4]]", anode, XPathConstants.NODE);
                    String testAuth = xpath.evaluate(".", subNode);
                    LocationArray.add(testAuth);
                    subNode = (Node) xpath.evaluate(".//td/text()[preceding-sibling::br[2]]", anode, XPathConstants.NODE);
                    String testForm = xpath.evaluate(".", subNode);
                    DateArray.add(testForm);
                    subNode = (Node) xpath.evaluate(".//td/span/a/@href", anode, XPathConstants.NODE);
                    String testURL = xpath.evaluate(".", subNode);
                    CalendarDetailsURLArray.add(testURL);
                    String testPic = "";
                    if ((subNode = (Node) xpath.evaluate(".//img", anode, XPathConstants.NODE)) != null) {
                        testPic = "http://events.cmclibrary.org/" + xpath.evaluate("./@src", subNode);

                    }
                    if (Objects.equals(testPic, "")) {
                        testPic = "/";
                    }
                    PicturesArray.add(testPic);
                }
            }
        Log.e("HTTP", "9");
*/
        }



    private void doDetailSearch() throws Exception {
        URL obj1 = new URL(detailsURL);
        HttpURLConnection.setFollowRedirects(true);

        //Open the real connection
        HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();
        con1.setRequestProperty("CSP", "active");
        con1.setRequestProperty("Accept", "*/*");
        con1.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        con1.setUseCaches(false);
        con1.setDoInput(true);
        con1.setChunkedStreamingMode(0);

        con1.setRequestMethod("GET");
        int responseCode = con1.getResponseCode();
        //Need to handle page not found etc...
        BufferedReader in1 = new BufferedReader(
                new InputStreamReader(con1.getInputStream()));
        String inputLine1;
        StringBuilder response1 = new StringBuilder();

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
        Node testNode1 = (Node) xpath.evaluate("//html/body/div[3]/table/tbody/tr[2]/td", document, XPathConstants.NODE);
        detailsName = xpath.evaluate(".", testNode1).trim();
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/b[1]", document, XPathConstants.NODE);
        detailsInfo = xpath.evaluate(".", testNode1);
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/text()[preceding-sibling::b[1]]", document, XPathConstants.NODE);
        detailsInfo += " " + xpath.evaluate(".", testNode1) + "\r\n";
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/b[2]", document, XPathConstants.NODE);
        detailsInfo += xpath.evaluate(".", testNode1);
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/text()[preceding-sibling::b[2]]", document, XPathConstants.NODE);
        detailsInfo += " " + xpath.evaluate(".", testNode1) + "\r\n";
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/b[3]", document, XPathConstants.NODE);
        detailsInfo += xpath.evaluate(".", testNode1);
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/text()[preceding-sibling::b[3]]", document, XPathConstants.NODE);
        detailsInfo += " " + xpath.evaluate(".", testNode1) + "\r\n";
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/b[4]", document, XPathConstants.NODE);
        detailsInfo += xpath.evaluate(".", testNode1);
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/text()[preceding-sibling::b[4]]", document, XPathConstants.NODE);
        detailsInfo += " " + xpath.evaluate(".", testNode1) + "\r\n";
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/b[5]", document, XPathConstants.NODE);
        detailsInfo += xpath.evaluate(".", testNode1);
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/text()[preceding-sibling::b[5]]", document, XPathConstants.NODE);
        detailsInfo += " " + xpath.evaluate(".", testNode1) + "\r\n";
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/div[2]/text()[1]", document, XPathConstants.NODE);
        detailsDescription = xpath.evaluate(".", testNode1).trim() + " ";
        testNode1 = (Node) xpath.evaluate("/html/body/div[3]/table/tbody/tr[4]/td/div[2]/text()[2]", document, XPathConstants.NODE);
        detailsDescription += xpath.evaluate(".", testNode1).trim();
    }

    }


