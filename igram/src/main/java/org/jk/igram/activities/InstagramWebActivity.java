package org.jk.igram.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.jk.igram.activities.utils.InstagramUtils;
import org.lib.jk.igram.R;

public class InstagramWebActivity extends AppCompatActivity {
    private String mUrl;
    private WebView mWebView;
    private TextView mTitle;
    public static String KEY_URL = "KEY_URL";

    public void getIntentExtras() {
        mUrl = getIntent().getStringExtra(KEY_URL);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        getIntentExtras();
        setUpWebView();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

    }

    private void setUpWebView() {
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new OAuthWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);

    }

    private class OAuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(InstagramUtils.mCallbackUrl)) {
                String urls[] = url.split("=");
                setResult(RESULT_OK, new Intent().putExtra("DATA", urls[1]));
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            setResult(RESULT_OK, new Intent().putExtra("ERROR", description));
            finish();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
