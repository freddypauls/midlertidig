package no.ntnu.fredrik.lab2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class listView extends AppCompatActivity {

    //Declares variables
    ArrayList<String> titles;
    ArrayList<String> links;
    ListView viewList;
    int maxPrintedArticles;
    int refreshTimer;
    String Url;
    Handler i = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        //gets shared preference from userPreference
        final int max = UserPreference.getNumbersofRowsnumbers(this);
        final int intervals = UserPreference.getNumbersOfTimerButtons(this);
        String urlFromUserPreferences = UserPreference.getRSSFeedFromUser(this);

        //Values from the shared preferences
        maxPrintedArticles = max;
        refreshTimer = intervals * 60000;
        Url = urlFromUserPreferences;

        //Gets ids
        viewList = findViewById(R.id.listView);


        // Activates when user clicks on an article and send you to the DisplayContent view where a web view gets made with the article displayed
        viewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //stores what link user clicks on
                Uri uri = Uri.parse(links.get(i));
                //Turns uri into String

                String stringUri = uri.toString();

                //starts new intent and passes url as a String
                Intent intent = new Intent(listView.this, ContentDisplay.class);
                intent.putExtra("Listing", stringUri);
                startActivity(intent);
            }
        });

        new ProcessInBackground().execute();
    }

    public void goToSettings(View view) {

        // Makes intent and sends to the next view
        Intent intent = new Intent(this, UserPreference.class);
        startActivity(intent);


    }

    protected void onResume() {
        //start handler as activity become visible

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(listView.this);
        i.postDelayed(new Runnable() {
            public void run() {
                // Run code

                System.out.println("Fetching again");
                new ProcessInBackground().execute();

                runnable=this;
                i.postDelayed(runnable, refreshTimer);
            }
        }, refreshTimer);

        super.onResume();
    }

    @Override
    protected void onPause() {
        i.removeCallbacks(runnable); //stop when activity not visible
        super.onPause();
    }

    //Sets URL for RSS
    public InputStream getInputStream(URL url){

        try{

            return url.openConnection().getInputStream();
        }

        catch(IOException e){

            return null;
        }
    }

    //Background task
    @SuppressLint("StaticFieldLeak")
    public class ProcessInBackground extends AsyncTask<Integer,Void, Exception>{

        ProgressDialog ProgressDialog = new ProgressDialog(listView.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressDialog.setMessage("Loading...");
            ProgressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            try {

                //Sets Titles and links
                titles = new ArrayList<>();
                links = new ArrayList<>();

                //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(listView.this);
                String rssURL = Url;
                int maxItems = maxPrintedArticles;


                // Stores the URL in URL
                URL url = new URL(rssURL);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                //Stores value of when inside item tag in xml document
                boolean insideItem = false;

                //stores current type of tag in xml
                int eventType = xpp.getEventType();

                //Stops at the end of the document
                while(eventType != XmlPullParser.END_DOCUMENT ) {

                    if (eventType == XmlPullParser.START_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")){

                            insideItem= true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")) {

                            if (insideItem){

                                if (titles.size() < maxItems) {
                                    titles.add(xpp.nextText());
                                }

                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")) {

                            if (insideItem) {

                                if (links.size() < maxItems) {
                                    links.add(xpp.nextText());
                                }

                            }
                        }
                    }

                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){

                        insideItem = false;
                    }

                    eventType =xpp.next();
                }
            }

            //If something fails with URL
            catch(MalformedURLException e){
                exception = e;
            }

            catch (XmlPullParserException e){
                exception = e;
            }

            catch(IOException e){
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.this, android.R.layout.simple_list_item_1, titles );

            viewList.setAdapter(adapter);

            ProgressDialog.dismiss();
        }
    }
}
