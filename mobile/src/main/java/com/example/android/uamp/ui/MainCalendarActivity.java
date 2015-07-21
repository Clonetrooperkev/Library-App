package com.example.android.uamp.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.uamp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//Set toolbar if we can.
public class MainCalendarActivity extends ActionBarCastActivity {
    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    List<String> calendarArray = new ArrayList<String>();
    ListView listview;
    ArrayAdapter adapter;
    public Calendar mService;

    GoogleCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    ProgressDialog mProgressDialog;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "clonetrooperkev@gmail.com";
    private static final String ASCOPES[] = {CalendarScopes.CALENDAR_READONLY};
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/drive.file",
            "email",
            CalendarScopes.CALENDAR,
            "profile");

/*
    private static String serviceAccountId = "101010101010@developer.gserviceaccount.com";
   // private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String P12_FILE_NAME = "1010101010101010101010101010101010101010-privatekey.p12";
*/

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initializeToolbar();
        setTitle("Calendar");

        mProgressDialog = new ProgressDialog(MainCalendarActivity.this);
        // Set progressdialog title
        mProgressDialog.setTitle("Retrieving Calendar Data");
        // Set progressdialog message
        mProgressDialog.setMessage("Fetching...");
        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.show();
        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        try {
            //Download Private Key from Google Service Account and place it in the the raw folder in the project.
            PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), getResources().openRawResource(R.raw.kevintesting267d28d845bb), "notasecret", "privatekey", "notasecret");
            //Get email address for the service account
            String emailAddress = "585418159223-0ss5e3fqknchu9h5m23tdd5s2vek4m3m@developer.gserviceaccount.com";
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(emailAddress)
                    .setServiceAccountPrivateKey(serviceAccountPrivateKey)
                    .setServiceAccountScopes(SCOPES)
                    .build();
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (GeneralSecurityException e){
            e.printStackTrace();
        }

    mService=new Calendar.Builder(
        transport,jsonFactory,credential)
        .setApplicationName("Google Calendar API Android Quickstart")
        .build();

        }


    @Override
    public void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mProgressDialog.setMessage("Google Play Services required: " +
                    "after installing, close and relaunch this app.");

        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */

    private void refreshResults() {
            if (isDeviceOnline()) {
                new ApiAsyncTask(this).execute();
            } else {
                mProgressDialog.setMessage("No network connection available.");

            }
        }


    /**
     * Clear any existing Google Calendar API data from the TextView and update
     * the header message; called from background threads and async tasks
     * that need to update the UI (in the UI thread).
     */
    public void clearResultsText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setMessage("Retrieving data...");

            }
        });
    }

    /**
     * Fill the data TextView with the given List of Strings; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     * @param dataStrings a List of Strings to populate the main TextView with.
     */
    public void updateResultsText(final List<String> dataStrings) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (dataStrings == null) {
                    mProgressDialog.setMessage("Error retrieving data!");


                } else if (dataStrings.size() == 0) {
                    mProgressDialog.setMessage("No data found.");


                } else {
                    mProgressDialog.setMessage("Data retrieved using" +
                            " the Google Calendar API:");
                    ListView lv = (ListView) findViewById(R.id.calendarlist);
                    lv.setAdapter(new ArrayAdapter<String>(MainCalendarActivity.this, R.layout.calendarlist_item, R.id.calendar_name, dataStrings));
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String calendarName = ((TextView) view.findViewById(R.id.calendar_name)).getText().toString();

                            Toast.makeText(getApplicationContext(), calendarName, Toast.LENGTH_SHORT).show();
                            if (position >= 0) {
                                Bundle extras = ActivityOptions.makeCustomAnimation(
                                        MainCalendarActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                                Class activityClass = mDrawerMenuContents.getActivity(position);
                                startActivity(new Intent(MainCalendarActivity.this, activityClass), extras);
                                finish();
                            }

                        }
                    });
                }
                mProgressDialog.dismiss();
            }
        });
    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setMessage(message);


            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    /*
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                //credential.setSelectedAccountName("clonetrooperkev@gmail.com"), REQUEST_AUTHORIZATION);
    }
*/
    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    public boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainCalendarActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

}