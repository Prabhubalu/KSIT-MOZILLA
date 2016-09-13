package com.ksit.ksitmozillaclub;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity implements EventRecyclerAdapter.AdapterCallback {

    private List<Event> eventList;
    private EventRecyclerAdapter eventRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Initialize RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        // Downloading Data from below URL
        final String url = "https://api.myjson.com/bins/3z492";
        new AsyncHttpTask().execute(url);
    }

    @Override
    public void onMethodCallback(int eventId, String eventName) {
        Log.d("EventsActivity", "Event ID: " + eventId + " Event Name: " + eventName);

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);

        // Customize Title
        adBuilder.setTitle("Event Registration");

        // Setting Custom Layout to Dialog
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.event_registration_dialog, null);
        adBuilder.setView(dialogView);

        // Attaching Layout Elements
        TextView tvRegDesc = (TextView) dialogView.findViewById(R.id.tv_event_reg_desc);
        tvRegDesc.setText("Registration for " + eventName + " event.");

        final EditText etName = (EditText) dialogView.findViewById(R.id.et_name);
        final EditText etUSN = (EditText) dialogView.findViewById(R.id.et_usn);
        final EditText etEmail = (EditText) dialogView.findViewById(R.id.et_email);
        final EditText etPhone = (EditText) dialogView.findViewById(R.id.et_phone);

        adBuilder.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String regName = etName.getText().toString();
                String regUSN = etUSN.getText().toString();
                String regEmail = etEmail.getText().toString();
                String regPhone = etPhone.getText().toString();

                Toast.makeText(EventsActivity.this, "Name: " + regName + "\nUSN: " + regUSN + "\nEmail: " + regEmail + "\nPhone: " + regPhone, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        adBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = adBuilder.create();
        alertDialog.show();
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = 0;
            HttpURLConnection httpURLConnection;

            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                int statusCode = httpURLConnection.getResponseCode();

                if (statusCode == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null)
                        response.append(line);

                    parseResult(response.toString());
                    result = 1;
                } else
                    result = 0;
            } catch (Exception e) {
                Log.d("EventsActivity", e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            mProgressBar.setVisibility(View.INVISIBLE);

            if (result == 1) {
                eventRecyclerAdapter = new EventRecyclerAdapter(EventsActivity.this, eventList);
                mRecyclerView.setAdapter(eventRecyclerAdapter);
            } else {
                Toast.makeText(EventsActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray events = response.optJSONArray("events");
            eventList = new ArrayList<>();

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.optJSONObject(i);
                Event eventItem = new Event();
                eventItem.setId(event.optInt("id"));
                eventItem.setDatetime(event.optString("datetime"));
                eventItem.setDetails(event.optString("details"));
                eventItem.setImgSrc(event.optString("imgsrc"));
                eventItem.setName(event.optString("name"));
                eventItem.setVenue(event.optString("venue"));

                eventList.add(eventItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
