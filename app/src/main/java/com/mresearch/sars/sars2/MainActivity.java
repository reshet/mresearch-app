package com.mresearch.sars.sars2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;


public class MainActivity extends Activity {

    //private static final String SARS2_URL = "http://192.168.0.101:8095/";
    //private static final String SARS2_URL = "http://dev.survey-archive.com:8095/";
    private static final String SARS2_URL = "http://sars.survey-archive.com";
    private WebView mWebView;
    final Activity activity = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        //mWebView.setWebViewClient(new HelloWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("SARS2-web-view: ", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
            public void onProgressChanged(WebView view, int progress) {
                activity.setTitle("Загрузка...");
                activity.setProgress(progress * 100);

                if(progress == 100) {
                    activity.setTitle(R.string.app_name);
                }
            }

        });

        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(getApplication().getCacheDir().toString());
        settings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            settings.setDatabasePath("/data/data/" + mWebView.getContext().getPackageName() + "/databases/");
            String appCachePath = getApplicationContext().getCacheDir()
                    .getAbsolutePath();
            boolean existsPath = new File(appCachePath).exists();
            File dir = getCacheDir();

            if (!dir.exists()) {
                dir.mkdirs();
            }
            settings.setAppCachePath(appCachePath);
            settings.setAppCacheMaxSize(1024 * 1024 * 8);
            settings.setAllowFileAccess(true);
        }
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
        if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        else{
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (savedInstanceState == null)
        {
            // указываем страницу загрузки
            mWebView.loadUrl(SARS2_URL);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        mWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
