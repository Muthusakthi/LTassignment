package com.trade.leather.ltassignment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    String JSONURL = "http://microblogging.wingnity.com/JSONParsingTutorial/jsonActors";
    String data;
    String name1,desc1,image1;
    ArrayList namelist = new ArrayList<String>();
    ArrayList desclist = new ArrayList<String>();
    ArrayList imagelist = new ArrayList<String>();
    TextView textView;
    RecyclerView recyclerView;
    ProgressDialog pdLoading;
    Button sendquery;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sendquery = (Button)findViewById(R.id.send);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        sendquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Thank You",Toast.LENGTH_LONG).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        loadapidata();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                loadapidata();

            }
        });


    }

    private void loadapidata() {
        final DatabaseHelper dbManager = new DatabaseHelper(this);
        //dbManager.open();
        final SQLiteDatabase sql = dbManager.getWritableDatabase();
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            requestQueue = Volley.newRequestQueue(MainActivity.this);
            pdLoading = new ProgressDialog(MainActivity.this);
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
            // JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(JSONURL, new Response.Listener<JSONArray>() {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, JSONURL,
                    new Response.Listener<String>() {
//Comment line used for Jsonobjectrequest to get data from api
                //  JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, JSONURL,
                  //  new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(String response) {
                          //  public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                              //  JSONObject jsonObject = new JSONObject(response.toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("actors");
                                for (int i = 0; i <= jsonArray.length(); i++) {
                                    JSONObject results = jsonArray.getJSONObject(i);
                                    String name = results.getString("name");
                                    String desc = results.getString("description");
                                    String image = results.getString("image");
                                    ContentValues cv1 = new ContentValues();
                                    cv1.put(DatabaseHelper.SUBJECT, name);
                                    cv1.put(DatabaseHelper.DESC, desc);
                                    cv1.put(DatabaseHelper.IMAGE, image);
                                    sql.insert(DatabaseHelper.TABLE_NAME, null, cv1);


                                    // textView.setText(s);

                                }
                                //
                                // RecyclerView.Adapter adapter = new DataAdapter(datas);
                                //  recyclerView.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SQLiteDatabase sql1 = dbManager.getReadableDatabase();
                            //The basic purpose of a  cursor is to point to a single row of the result fetched by the query.
                            Cursor cursor = sql1.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null,
                                    null);
                            if (cursor != null && cursor.getCount() > 0) {
                                if (cursor.moveToFirst()) {
                                    do {

                                        name1 = cursor.getString(cursor
                                                .getColumnIndex(DatabaseHelper.SUBJECT));
                                        desc1 = cursor.getString(cursor
                                                .getColumnIndex(DatabaseHelper.DESC));
                                        image1 = cursor.getString(cursor
                                                .getColumnIndex(DatabaseHelper.IMAGE));
                                        if (desc1.length() > 25) {
                                            desc1 = desc1.substring(0, 27) + "...";
                                        }
                                        namelist.add(name1);
                                        desclist.add(desc1);
                                        imagelist.add(image1);
                                    } while (cursor.moveToNext());
                                }
                                //datas.add(name1);
                            }
                            pdLoading.dismiss();
                            swipeContainer.setRefreshing(false);
                            RecyclerView.Adapter adapter = new DataAdapter(namelist, desclist, imagelist);
                            recyclerView.setAdapter(adapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pdLoading.dismiss();
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Slow internet connection.Please refresh.", Toast.LENGTH_LONG).show();
                }
            }

            );
            requestQueue.add(stringRequest);
        }else  {
            AlertDialog.Builder alert_build = new AlertDialog.Builder(MainActivity.this);
            alert_build.setTitle("Network Information");
            alert_build.setMessage("Please check your Internet connection");
            alert_build.setCancelable(true);
            alert_build.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
            /* Go to Settings page Intent */

                    Intent i =  new Intent(Settings.ACTION_SETTINGS);
                    startActivity(i);
                    dialog.dismiss();

                }
            });
            alert_build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    swipeContainer.setRefreshing(false);

                }
            });
            AlertDialog alert_show = alert_build.create();
            alert_show.show();
            swipeContainer.setRefreshing(false);

        }
    }
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        AlertDialog.Builder alert_build = new AlertDialog.Builder(MainActivity.this);
        alert_build.setTitle("Exit");
        alert_build.setMessage("Are you sure to Exit?");
        alert_build.setCancelable(true);
        alert_build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            /* Go to Settings page Intent */
                //  finish();
                swipeContainer.setRefreshing(false);
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        });
        alert_build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                swipeContainer.setRefreshing(false);
                pdLoading.dismiss();
            }
        });
        AlertDialog alert_show = alert_build.create();
        alert_show.show();

    }}