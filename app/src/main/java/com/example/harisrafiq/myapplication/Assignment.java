package com.example.harisrafiq.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Assignment extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    AssignmentAdapter lviewAdapter;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;

    private final static String classNumber[] = {};
    private final static String datelist[] = {};
    private final static String homework[] = {};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.asignmentlist);

//        lviewAdapter = new AssignmentAdapter(this, classNumber, datelist);
//
//        System.out.println("adapter => "+lviewAdapter.getCount());
//
//        listView.setAdapter(lviewAdapter);

        listView.setOnItemClickListener(this);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Add Assignments", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();

                Intent inten = new Intent(Assignment.this,AddHomeWork.class);
                startActivity(inten);


            }
        });

        DownloadHomeWork task = new DownloadHomeWork();
        try {
            ErrorClass res = task.execute().get();
            if (res.result == true){

                Toast.makeText(this,"download data", Toast.LENGTH_SHORT).show();
                lviewAdapter = new AssignmentAdapter(this, classNumber, datelist);

                System.out.println("adapter => "+lviewAdapter.getCount());

                listView.setAdapter(lviewAdapter);
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_assignment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(this,"Title => "+classNumber[position]+"=> n Description"+datelist[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class DownloadHomeWork extends AsyncTask<URL, Integer, ErrorClass> {

        protected ErrorClass doInBackground(URL... urls) {

                mongoClient = new MongoClient(new MongoClientURI(Configuration.databaseAddress));
                mongoDatabase = mongoClient.getDatabase(Configuration.databaseName);


                if (mongoDatabase != null) {

                    MongoCollection<Document> coll = mongoDatabase.getCollection(Configuration.tbl_Homework);

                    FindIterable<Document> iterDoc = coll.find();
                    MongoCursor cursor = iterDoc.iterator();

                    try {
                         int count = 0;

                        while(cursor.hasNext()) {

                           Document doc = (Document) cursor.next();
                           String classid = doc.getString("class_id");
                           String date = doc.getString("date");
                           String hm = doc.getString("homework");
                            Log.d(date,classid);

//                           classNumber[count] = classid;
//                           datelist[count] = date;
//                           homework[count] = hm;
                           count = count + 1;

                        }

                    } finally {

                        cursor.close();

                    }

                    ErrorClass err0r = new ErrorClass();
                    err0r.result = true;
                    err0r.error_message = "OK";
                    return err0r;


                } else {

                    ErrorClass err0r = new ErrorClass();
                    err0r.result = false;
                    err0r.error_message = "Database not found";
                    return err0r;

                }

            }
        }

}