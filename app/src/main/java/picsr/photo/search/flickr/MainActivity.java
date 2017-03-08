package picsr.photo.search.flickr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import picsr.photo.search.flickr.network.VolleySingleton;

public class MainActivity extends AppCompatActivity {

    final int SDCARD_PERMISSION = 1;
    final String TAG = "MyLog " + this.getClass().getSimpleName();

    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    LinearLayout infoView;
    RelativeLayout rootView;


    //dataList gets all data from gson, then it is split into two sets for two column view
    ArrayList<DataModel> dataList;

    //varying url based on constant flickr api url for searching
    String URL = Constants.URL;

    private SearchView searchView;
    private MenuItem searchMenuItem;
    SearchView.OnQueryTextListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //all things of this activity initialized here
    void init() {

        //initialized to avoid null
        dataList = new ArrayList<>();

        //views
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        //view shown on launch
        infoView = (LinearLayout) findViewById(R.id.infoView);

        rootView = (RelativeLayout) findViewById(R.id.relativeLayout);

        //search listener from action bar
        listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                try {

                    //replaces spaces
                    String finalQuery = query.replaceAll("\\s", "+");

                    //constructs search url
                    URL = Constants.URL.replace("keyword", finalQuery);

                    Log.d(TAG, "newURL =" + URL);
                    showLoading();
                    requestData(URL);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        requestStoragePermission();
        requestData(Constants.URL_RECENT_UPLOADS);
    }


    void requestData(String paramURL) {

        //clear data
        if (!dataList.isEmpty())
            dataList.clear();

        //request data from flickr api using volley library
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, paramURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "onResponse= " + response.toString());
                new parseTask(response).execute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                Log.d(TAG, "onErrorResponse= " + error.getMessage());

                if (error.getMessage().contains("UnknownHostException")) {
                    Snackbar.make(rootView, "No Network", Snackbar.LENGTH_LONG).show();
                    findViewById(R.id.tvNoNetwork).setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(rootView, "Error - " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        }
        );

        VolleySingleton.getInstance().addToRequestQueue(jsonObjectRequest);

    }//getData


    //background task for parsing data
    class parseTask extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject;

        public parseTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args) {

            try {

                if (this.jsonObject.has("photos")) {

                    JSONObject jsonObject = this.jsonObject.getJSONObject("photos");
                    JSONArray jsonArray = jsonObject.getJSONArray("photo");

                    if (jsonArray.length() > 0) {

                        //gson library for faster and easier serialization
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<DataModel>>() {
                        }.getType();
                        dataList = gson.fromJson(jsonArray.toString(), listType);
                        Log.d("serData", "dataList.size=" + dataList.size());

                        //no need of checking for null pointer like traditional way, it's avoided in initialization, BUT checking for empty is required

                        if (!dataList.isEmpty()) {

                            Snackbar.make(rootView, "Loading pictures", Snackbar.LENGTH_SHORT).show();

                        }
                    } else {
                        Snackbar.make(rootView, "Found no results", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(rootView, "Found no photos", Snackbar.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(rootView, "Error - " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!dataList.isEmpty()) {
                DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), dataList);
                recyclerView.setAdapter(dataAdapter);
            }
            hideLoading();
        }

    }//parseTask


    void showLoading() {
        infoView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoading() {
        infoView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(listener);
        return true;
    }

    void requestStoragePermission() {
        // For api Level 23 and above.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SDCARD_PERMISSION);

        }
    }

}

