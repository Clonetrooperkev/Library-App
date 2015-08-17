package com.example.android.uamp.ui;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class MainContactsActivity extends ActionBarCastActivity {
    private List<String> contactsArray = new ArrayList<>();
    private List<String> urlArray = new ArrayList<>();
    private ListView lv;
    private String detailsURL;
    private String detailsArray;
    private Parcelable state = null;
    private ProgressDialog mProgressDialog;
    private boolean firstContactSearch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //detailsURL controls the state of the activity
        //If detailsURL is not blank, perform a detail search
        detailsURL = "";

        firstContactSearch = false;
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if(extras.containsKey("DETAILS")){
                detailsURL = "http://www.cmclibrary.org/" + urlArray.get(extras.getInt("DETAILS"));
            }
        }
            if(detailsURL.equals("")){
                setContentView(R.layout.activity_contacts);
                initializeToolbar();
                setTitle("Contacts");
                state = null;
                contactsArray.clear();
                urlArray.clear();
                contactsArray.add("Library Branches");
                urlArray.add("Library Branches");
            }

        new DownloadJSON().execute();
    }
    private void showDetails() {
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.contact_details, null);

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
        TextView tView = (TextView) customView.findViewById(R.id.contact_details);
        tView.setMovementMethod(new ScrollingMovementMethod());
        tView.setText(detailsArray);
        builder.create().show();
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
            try {
                if(detailsURL.equals("")){
                    doContactSearch();
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
            ArrayAdapter ContactsArrayAdapter = new ArrayAdapter<>(MainContactsActivity.this, R.layout.contactlist_item, R.id.contact_name, contactsArray);
            lv = (ListView)findViewById(R.id.contactslist);
            lv.setAdapter(ContactsArrayAdapter);
            if(state != null){
                lv.onRestoreInstanceState(state);
            }
            if(!detailsURL.equals("")) {
                showDetails();
            }
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (position >= 0) {
                        state = lv.onSaveInstanceState();
                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                MainContactsActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Intent subIntent = new Intent(MainContactsActivity.this, MainContactsActivity.class);
                        subIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        subIntent.putExtra("DETAILS", position);
                        startActivity(subIntent, extras);
                    }
                }
            });
            if (contactsArray.size() == 0) {
                Toast.makeText(getApplicationContext(), "Contact Information Unavailable", Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();
        }
    }

    private void doContactSearch() throws Exception {
        String url = "http://www.cmclibrary.org/about-the-library/contact-us";
        URL obj1 = new URL(url);
        HttpURLConnection.setFollowRedirects(true);
        //Get session ID if necessary
        if(firstContactSearch) {
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
        NodeList testNode = (NodeList)xpath.evaluate("//span[@class='item-title']", document, XPathConstants.NODESET);
        for (int index = 0; index < testNode.getLength(); index++) {
            Node anode = testNode.item(index);
            Node subNode = (Node)xpath.evaluate("./a", anode, XPathConstants.NODE);
            String tempString = xpath.evaluate(".",subNode);
            tempString = tempString.trim();
            contactsArray.add(tempString);
            subNode = (Node)xpath.evaluate("./a/@href", anode, XPathConstants.NODE);
            tempString = xpath.evaluate(".",subNode);
            urlArray.add(tempString);

        }
    }
    private void doDetailSearch()throws Exception{
        detailsArray = "";
        if(detailsURL.equals("http://www.cmclibrary.org/Library Branches")){
            detailsArray = "Main Branch"+ "\r\n";
            detailsArray += "       609-463-6350"+ "\r\n";
            detailsArray +="Cape May City"+ "\r\n";
            detailsArray += "      609-884-9568"+ "\r\n";
            detailsArray +="Lower Township"+ "\r\n";
            detailsArray += "      609-886-8999"+ "\r\n";
            detailsArray +="Sea Isle City"+ "\r\n";
            detailsArray += "      609-263-7301"+ "\r\n";
            detailsArray +="Stone Harbor"+ "\r\n";
            detailsArray += "      609-36-86809"+ "\r\n";
            detailsArray +="Upper Township"+ "\r\n";
            detailsArray += "      609-628-2607"+ "\r\n";
            detailsArray +="Wildwood Crest"+ "\r\n";
            detailsArray += "      609-522-0564"+ "\r\n";
            detailsArray +="Woodbine"+ "\r\n";
            detailsArray += "      609-861-2501"+ "\r\n";

        }
        else{
            URL obj1 = new URL(detailsURL);
            HttpURLConnection.setFollowRedirects(true);
            //Get session ID if necessary
            if(firstContactSearch) {
                HttpURLConnection conx = (HttpURLConnection) obj1.openConnection();
                conx.setRequestProperty("CSP", "active");
                conx.setRequestProperty("Accept", "*/*");
                conx.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                conx.setUseCaches(false);
                conx.setDoInput(true);
                conx.setChunkedStreamingMode(0);
                conx.setRequestMethod("GET");
                int responseCodex = conx.getResponseCode();
                //Need to handle page not found etc...

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
            NodeList testNode = (NodeList)xpath.evaluate("//td[@class='item-title']", document, XPathConstants.NODESET);
            NodeList testNode2 = (NodeList)xpath.evaluate("//td[@class='item-phone']", document, XPathConstants.NODESET);
            for (int index = 0; index < testNode.getLength(); index++) {
                Node anode = testNode.item(index);
                Node subNode = (Node)xpath.evaluate("./a", anode, XPathConstants.NODE);
                String tempString = xpath.evaluate(".",subNode);
                tempString = tempString.trim();
                detailsArray += (tempString) + "\r\n";
                anode = testNode2.item(index);
                tempString = xpath.evaluate(".",anode);
                tempString = tempString.trim();
                detailsArray += "      "+ (tempString) + "\r\n";
            }
        }
    }


}

