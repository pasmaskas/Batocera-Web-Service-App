package com.example.webapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "batocera_prefs";
    private static final String KEY_IP = "saved_ip";
    private static final String PORT = ":1234";
    private static final String PREFIX = "http://";

    private WebView webView;
    private LinearLayout inputScreen;
    private EditText ipInput;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();

        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        webView = findViewById(R.id.webview);
        inputScreen = findViewById(R.id.inputScreen);
        ipInput = findViewById(R.id.ipInput);
        Button connectButton = findViewById(R.id.connectButton);

        // Alleen cijfers en punten toestaan in het IP-veld
        ipInput.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        setupWebView();

        String savedIp = prefs.getString(KEY_IP, null);
        if (savedIp != null && !savedIp.isEmpty()) {
            // Al eerder ingesteld -> direct openen, geen invulscherm tonen
            showWebView();
            webView.loadUrl(PREFIX + savedIp + PORT);
        } else {
            showInputScreen();
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipInput.getText().toString().trim();
                if (ip.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter an IP address", Toast.LENGTH_SHORT).show();
                    return;
                }
                prefs.edit().putString(KEY_IP, ip).apply();
                showWebView();
                webView.loadUrl(PREFIX + ip + PORT);
            }
        });
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());
    }

    private void showInputScreen() {
        inputScreen.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void showWebView() {
        inputScreen.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
