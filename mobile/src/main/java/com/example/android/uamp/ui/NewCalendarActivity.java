
package com.example.android.uamp.ui;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.uamp.R;
import com.example.android.uamp.utils.LogHelper;
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
public class NewCalendarActivity extends ActionBarCastActivity {
    private static final int DATE_DIALOG_ID = 1;
    public int year;
    public int month;
    public int day;
    EditText txtDate;
    ListView lv;
    Parcelable state = null;
    public int startDay = 1;
    public int startMonth = 12;
    public int startYear = 2015;
    public int endDay = 2;
    public int endMonth = 12;
    public int endYear = 2015;
    //private Context context = null;
    private static final String TAG = LogHelper.makeLogTag(MainCatalogActivity.class);
    public String searchresults = "a";
    List<String> CalendarNameArray = new ArrayList<String>();
    List<String> LocationArray = new ArrayList<String>();
    List<String> DateArray = new ArrayList<String>();
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
        setContentView(R.layout.activity_calendar);
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
            CalendarNameArray.clear();
            LocationArray.clear();
            DateArray.clear();
            PicturesArray.clear();
            searchPage = 1;
            searchresults = tempsearchresults;
            state = null;
        }


        initializeToolbar();
        setTitle("Calendar");
        //setContentView(R.layout.activity_placeholder);
        new DownloadJSON().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( (item.getTitle().equals("Date Range"))) {
            showDatePicker();
        }
        /*
        if( (item.getTitle().equals("Start Date"))) {
            final Calendar c = Calendar.getInstance();
            final Calendar mc = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                            // TODO Auto-generated method stub
                            startYear = arg1;
                            startMonth = arg2+1;
                            startDay = arg3;
                            //showDate(arg1, arg2+1, arg3);
                        }
                    }, year, month, day);
            mc.set(2015,12,30);
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());

            dpd.getDatePicker().setMaxDate(mc.getTimeInMillis());
            dpd.show();

        }
        if( (item.getTitle().equals("End Date")) ) {
            final Calendar c = Calendar.getInstance();
            final Calendar mc = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                            // TODO Auto-generated method stub
                            endYear = arg1;
                            endMonth = arg2+1;
                            endDay = arg3;
                            //showDate(arg1, arg2+1, arg3);
                            Bundle extras = ActivityOptions.makeCustomAnimation(
                                    NewCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();
                            Intent searchIntent = new Intent(NewCalendarActivity.this, NewCalendarActivity.class);
                            searchIntent.putExtra("SEARCHVALUE", "");
                            searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(searchIntent, extras);
                        }
                    }, year, month, day);
            mc.set(2015,12,30);
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());

            dpd.getDatePicker().setMaxDate(mc.getTimeInMillis());
            dpd.show();

        }
*/
        return super.onOptionsItemSelected(item);
    }

    public void showDatePicker() {
        // Inflate your custom layout containing 2 DatePickers
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.calendar_datepicker, null);

        // Define your date pickers
        final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);
        final DatePicker dpEndDate = (DatePicker) customView.findViewById(R.id.dpEndDate);

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
            }});

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
/*
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
*/
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
                doCalSearch();
            }
            catch(Exception e){
                LogHelper.e(TAG,e,"Things be wacky");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            CalendarListAdapter adapter = new CalendarListAdapter(NewCalendarActivity.this, R.layout.calendarlist_item, CalendarNameArray, LocationArray, DateArray, PicturesArray);
            lv = (ListView)findViewById(R.id.calendarlist);
            lv.setAdapter(adapter);
            if(state != null){
                lv.onRestoreInstanceState(state);
            }



            if(CalendarNameArray.size() != 0) {
                View footerView = ((LayoutInflater) NewCalendarActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
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
        String url1 = "http://events.cmclibrary.org/eventcalendar.asp";

        String USER_AGENT = "Chrome/43.0.2357.134";
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        URL obj1 = new URL(url1);
        HttpURLConnection.setFollowRedirects(true);

        //Make a connection to get cookies
        if(searchPage == 1){
            HttpURLConnection con = (HttpURLConnection) obj1.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            Map<String, List<String>> headerFields = con.getHeaderFields();

        }

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
                        //"perPageDispTracker=" +URLEncoder.encode("25", "UTF-8")+
                        "&date_type=" + URLEncoder.encode("range", "UTF-8")+
                        "&dr1Month=" + URLEncoder.encode(Integer.toString(startMonth), "UTF-8")+
                        "&dr1Day=" + URLEncoder.encode(Integer.toString(startDay), "UTF-8")+
                        "&dr1Year=" + URLEncoder.encode(Integer.toString(startYear), "UTF-8")+
                        "&dr2Month=" + URLEncoder.encode(Integer.toString(endMonth), "UTF-8")+
                        "&dr2Day=" + URLEncoder.encode(Integer.toString(endDay), "UTF-8")+
                        "&dr2Year=" + URLEncoder.encode(Integer.toString(endYear), "UTF-8");

        String urlParameters2 =
                "DispType=" + URLEncoder.encode("list", "UTF-8") +
                "&pageTracker=" + URLEncoder.encode(Integer.toString(searchPage), "UTF-8") +
                "&SaveDispType=" +URLEncoder.encode("list", "UTF-8") +
                "&perPageDispTracker=" +URLEncoder.encode("25", "UTF-8")+
                "&dt=" +URLEncoder.encode("range", "UTF-8")+
                "&ds=" +URLEncoder.encode(Integer.toString(startYear)+"-"+Integer.toString(startMonth)+"-"+Integer.toString(startDay), "UTF-8")+
                "&de=" +URLEncoder.encode(Integer.toString(endYear)+"-"+Integer.toString(endMonth)+"-"+Integer.toString(endDay), "UTF-8");



        con1.setRequestMethod("POST");

        //Send request
        DataOutputStream wr = new DataOutputStream (
                con1.getOutputStream ());
        if(searchPage == 1) {
            wr.writeBytes(urlParameters1);
        }
        else{
            wr.writeBytes(urlParameters2);

        }
        wr.flush();
        wr.close();

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
        Node testNode1 = (Node)xpath.evaluate("//div[@id = 'divBody']", document, XPathConstants.NODE);
        NodeList testNode = (NodeList)xpath.evaluate(".//table[@class = 'event']/tbody/tr[2]", testNode1, XPathConstants.NODESET);
        for (int index = 0; index < testNode.getLength(); index++) {
            Node anode = testNode.item(index);
            //Node tNode = (Node)xpath.evaluate(".//td[@class = 'event_values']", anode, XPathConstants.NODE);
            Node subNode = (Node)xpath.evaluate(".//span[@class='event_title_list_special_class']", anode, XPathConstants.NODE);
            String testName = xpath.evaluate(".", subNode);
            if(testName != "") {
                CalendarNameArray.add(testName);
                subNode = (Node)xpath.evaluate(".//td/text()[preceding-sibling::br[4]]", anode, XPathConstants.NODE);
                String testAuth = xpath.evaluate(".",subNode);
                LocationArray.add(testAuth);
                subNode = (Node)xpath.evaluate(".//td/text()[preceding-sibling::br[2]]", anode, XPathConstants.NODE);
                String testForm = xpath.evaluate(".",subNode);
                DateArray.add(testForm);
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

