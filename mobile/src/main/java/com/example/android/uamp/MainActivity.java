package com.example.android.uamp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.uamp.ApiAsyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    Calendar mService;

    GoogleCredential credential;
    private TextView mStatusText;
    private TextView mResultsText;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mStatusText = new TextView(this);
        mStatusText.setLayoutParams(tlp);
        mStatusText.setTypeface(null, Typeface.BOLD);
        mStatusText.setText("Retrieving data...");
        activityLayout.addView(mStatusText);

        mResultsText = new TextView(this);
        mResultsText.setLayoutParams(tlp);
        mResultsText.setPadding(16, 16, 16, 16);
        mResultsText.setVerticalScrollBarEnabled(true);
        mResultsText.setMovementMethod(new ScrollingMovementMethod());
        activityLayout.addView(mResultsText);

        setContentView(activityLayout);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        /*credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                //.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
                .setSelectedAccountName("clonetrooperkev@gmail.com");
*/
/*
    public static GoogleCredential createCredentialForServiceAccount(
            HttpTransport transport,
            JsonFactory jsonFactory,
            Iterable<String> serviceAccountScopes,
            File p12File) throws GeneralSecurityException, IOException {
        return new GoogleCredential.Builder().setTransport(transport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId("585418159223-6rckrmro68eksm0485gm59apnl9p5unn@developer.gserviceaccount.com")
                .setServiceAccountScopes(SCOPES)
                .setServiceAccountPrivateKeyFromP12File(new File("C:\\Users\\Kevin\\Desktop\\Android TestA\\kevinpermissions.json"))
                .build();
    }
}
*/
        File currentDir = new File("storage/sdcard/kevintesting-267d28d845bb.p12");
        //File currentDir = new File("mnt/shell/emulated/0/kevintesting-267d28d845bb.p12");


        /*File[] files = currentDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    return filename.toLowerCase().endsWith(".*");
                }
            });
*/
        try {
            //InputStream KevStream = MainActivity.class.getResourceAsStream("storage/sdcard/kevinpermissions.json");
            //PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), MainActivity.class.getResourceAsStream("/kevinpermissions.json"), "notasecret", "privatekey", "notasecret");
            String emailAddress = "585418159223-0ss5e3fqknchu9h5m23tdd5s2vek4m3m@developer.gserviceaccount.com";
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(emailAddress)
                    .setServiceAccountPrivateKeyFromP12File(currentDir)
                    //.setServiceAccountPrivateKey(serviceAccountPrivateKey)
                    .setServiceAccountScopes(SCOPES)
                    .build();
        }
            catch (GeneralSecurityException e) {
                e.printStackTrace();
        }
            catch (IOException e) {
                e.printStackTrace();
        }



    mService=new Calendar.Builder(
        transport,jsonFactory,credential)
        .setApplicationName("Google Calendar API Android Quickstart")
        .build();

        }


    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mStatusText.setText("Google Play Services required: " +
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
                mStatusText.setText("No network connection available.");
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
                mStatusText.setText("Retrieving data�");
                mResultsText.setText("");
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
                    mStatusText.setText("Error retrieving data!");
                } else if (dataStrings.size() == 0) {
                    mStatusText.setText("No data found.");
                } else {
                    mStatusText.setText("Data retrieved using" +
                            " the Google Calendar API:");
                    mResultsText.setText(TextUtils.join("\n\n", dataStrings));
                }
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
                mStatusText.setText(message);
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
    private boolean isGooglePlayServicesAvailable() {
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
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

}