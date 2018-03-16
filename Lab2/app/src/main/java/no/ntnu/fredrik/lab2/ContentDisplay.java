package no.ntnu.fredrik.lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ContentDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_display);

        //Gets url from listView activity
        String webPageUrlFromRSS = getIntent().getStringExtra("Listing");

        //Starts a new web view with the link sent from listView
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl(webPageUrlFromRSS);
    }
}
