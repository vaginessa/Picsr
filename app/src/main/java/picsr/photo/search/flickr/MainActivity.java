package picsr.photo.search.flickr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

    ProgressBar progressBar;

    //Latest recylcerview is used instead of list or gridview
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    LinearLayout infoPart;
    RelativeLayout rootView;

    final String TAG = "MyLog " + this.getClass().getSimpleName();

    //alldatalist gets all data from gson, then it is split into two sets for two column view
    ArrayList<DataModel> allDataList;

    //varying url based on constant flickr api url
    String URL = Constants.URL;

    private SearchView mSearchView;
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

        //initialized to avoid null pointer exception
        allDataList = new ArrayList<>();

        //views
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //view shown on launch
        infoPart = (LinearLayout) findViewById(R.id.infoPart);

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
                    requestData();

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
    }


    void requestData() {

        showLoading();

        //clear data
        allDataList.clear();

        //request data from flickr api using volley library
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "onResponse= " + response.toString());
                new parseTask(response).execute();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                Log.d(TAG, "onErrorResponse= " + error.toString());
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
                JSONObject jsonObject = this.jsonObject.getJSONObject("photos");
                JSONArray jsonArray = jsonObject.getJSONArray("photo");

                //gson library for faster and easier serialization
                Gson gson = new Gson();
                Type listType = new TypeToken<List<DataModel>>() {
                }.getType();
                allDataList = gson.fromJson(jsonArray.toString(), listType);
                Log.d("serData", "dataList.size=" + allDataList.size());

                //no need of checking for null pointer like traditional way, it's avoided in initialization, BUT checking for empty is required

                if( !allDataList.isEmpty() ) {

                    Snackbar.make(rootView, "Found "+allDataList.size() +" results", Snackbar.LENGTH_SHORT).show();

                }else{
                    Snackbar.make(rootView, "Found no results", Snackbar.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            if( !allDataList.isEmpty() ) {
                DataAdapter dataAdapter = new DataAdapter(getApplicationContext(), allDataList);
                recyclerView.setAdapter(dataAdapter);
            }
            hideLoading();
        }

    }//parseTask


    void showLoading() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoading() {
        infoPart.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(listener);
        return true;
    }

}

