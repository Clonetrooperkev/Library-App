package com.example.android.uamp.ui;

import android.os.AsyncTask;

import com.example.android.uamp.ui.MainCalendarActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainCalendarActivity mActivity;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainCalendarActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainCalendarActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            mActivity.updateStatus("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        String pageToken = null;

/*      //this adds a calendar named primary
        com.google.api.services.calendar.model.Calendar calendar = new Calendar();
        calendar.setSummary("primary");
        calendar.setTimeZone("America/Los_Angeles");
        Calendar createdCalendar = mActivity.mService.calendars().insert(calendar).execute();
        System.out.println(createdCalendar.getId());

        //This lists all the available calendars for the service account
        do {
            CalendarList calendarList = mActivity.mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
                System.out.println(calendarListEntry.getSummary());
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        // Show the access rules for the primary calendar
        Acl acl = mActivity.mService.acl().list("primary").execute();
        for (AclRule rule : acl.getItems()) {
            System.out.println(rule.getId() + ": " + rule.getRole());
        }

        //Make a new owner for the primary calendar
        AclRule rule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();
        scope.setType("user").setValue("clonetrooperkev@gmail.com");
        rule.setScope(scope).setRole("owner");
        AclRule createdRule = mActivity.mService.acl().insert("primary", rule).execute();
        System.out.println(createdRule.getId());
*/
        Events events = mActivity.mService.events().list("primary")
                .setMaxResults(50)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
                    //String.format("%s (%s)", event.getColorId(), start));

        }
        return eventStrings;
    }

}