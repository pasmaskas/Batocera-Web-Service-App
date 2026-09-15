package com.example.webapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "batocera_prefs";
    private static final String KEY_IP = "saved_ip";
    private static final String KEY_MAC = "saved_mac";
    private static final String PREFIX = "http://";
    private static final String PORT = ":1234";

    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int POLL_INTERVAL_MS = 3000;
    private static final int MAX_POLL_ATTEMPTS = 40; // ~2 minuten

    private WebView webView;
    private LinearLayout inputScreen;
    private LinearLayout waitingScreen;
    private FrameLayout videoContainer;
    private EditText ipInput;
    private EditText macInput;
    private TextView waitingStatusText;
    private ProgressBar waitingProgress;
    private Button waitingActionButton;
    private TextView changeUrlText;
    private SharedPreferences prefs;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isPolling = false;
    private int pollAttempts = 0;

    // Bijhouden wat er nodig is om de fullscreen video weer te sluiten
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    interface ConnectionCallback {
        void onResult(boolean reachable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        webView = findViewById(R.id.webview);
        inputScreen = findViewById(R.id.inputScreen);
        waitingScreen = findViewById(R.id.waitingScreen);
        videoContainer = findViewById(R.id.videoContainer);
        ipInput = findViewById(R.id.ipInput);
        macInput = findViewById(R.id.macInput);
        waitingStatusText = findViewById(R.id.waitingStatusText);
        waitingProgress = findViewById(R.id.waitingProgress);
        waitingActionButton = findViewById(R.id.waitingActionButton);
        changeUrlText = findViewById(R.id.changeUrlText);
        Button connectButton = findViewById(R.id.connectButton);

        // Alleen cijfers en punten toestaan in het IP-veld
        ipInput.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        setupWebView();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipInput.getText().toString().trim();
                String mac = macInput.getText().toString().trim();

                if (ip.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter an IP address", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_IP, ip);
                editor.putString(KEY_MAC, mac);
                editor.apply();

                attemptConnect(buildUrl(ip));
            }
        });

        waitingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleWaitingAction();
            }
        });

        changeUrlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPolling = false;
                ipInput.setText(prefs.getString(KEY_IP, ""));
                macInput.setText(prefs.getString(KEY_MAC, ""));
                showInputScreen();
            }
        });

        String savedIp = prefs.getString(KEY_IP, null);
        if (savedIp != null && !savedIp.isEmpty()) {
            attemptConnect(buildUrl(savedIp));
        } else {
            showInputScreen();
        }
    }

    private static String buildUrl(String ip) {
        return PREFIX + ip + PORT;
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());

        // Dit zorgt voor fullscreen HTML5-video (bv. <video> met de fullscreen-knop)
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customView = view;
                customViewCallback = callback;

                videoContainer.addView(view);
                videoContainer.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                hideSystemBars();
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;

                videoContainer.removeView(customView);
                videoContainer.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);

                customView = null;
                if (customViewCallback != null) {
                    customViewCallback.onCustomViewHidden();
                    customViewCallback = null;
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                showSystemBars();
            }
        });
    }

    /** Verbergt statusbalk en navigatiebalk volledig (alleen tijdens fullscreen video). */
    private void hideSystemBars() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Ook onder de camera-uitsparing (notch) door tekenen, zodat echt het hele
        // scherm gebruikt wordt tijdens fullscreen video.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }
    }

    /** Zet statusbalk en navigatiebalk terug naar normaal zichtbaar. */
    private void showSystemBars() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            getWindow().setAttributes(params);
        }
    }

    // ---------- Schermen wisselen ----------

    private void showInputScreen() {
        inputScreen.setVisibility(View.VISIBLE);
        waitingScreen.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
    }

    private void showWaitingScreen() {
        inputScreen.setVisibility(View.GONE);
        waitingScreen.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void showWebView() {
        inputScreen.setVisibility(View.GONE);
        waitingScreen.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    // ---------- Verbinding checken / wachten ----------

    private void attemptConnect(final String url) {
        showWaitingScreen();
        waitingStatusText.setText("Checking connection...");
        waitingProgress.setVisibility(View.VISIBLE);
        waitingActionButton.setVisibility(View.GONE);

        checkReachable(url, new ConnectionCallback() {
            @Override
            public void onResult(boolean reachable) {
                if (reachable) {
                    isPolling = false;
                    showWebView();
                    webView.loadUrl(url);
                } else {
                    setWaitingOffline();
                }
            }
        });
    }

    private void setWaitingOffline() {
        waitingProgress.setVisibility(View.GONE);
        String mac = prefs.getString(KEY_MAC, "");
        if (!mac.isEmpty()) {
            waitingStatusText.setText("Batocera appears to be offline");
            waitingActionButton.setText("⚡ Power On");
        } else {
            waitingStatusText.setText("Can't reach Batocera Web Services");
            waitingActionButton.setText("Check Again");
        }
        waitingActionButton.setEnabled(true);
        waitingActionButton.setVisibility(View.VISIBLE);
    }

    private void handleWaitingAction() {
        final String ip = prefs.getString(KEY_IP, "");
        final String url = buildUrl(ip);
        final String mac = prefs.getString(KEY_MAC, "");

        if (!mac.isEmpty()) {
            sendWakeOnLan(mac);
            startPolling(url);
        } else {
            attemptConnect(url);
        }
    }

    private void startPolling(final String url) {
        isPolling = true;
        pollAttempts = 0;
        waitingActionButton.setEnabled(false);
        waitingActionButton.setText("Turning on...");
        waitingProgress.setVisibility(View.VISIBLE);
        waitingStatusText.setText("Waiting for Batocera to start...");
        pollStep(url);
    }

    private void pollStep(final String url) {
        if (!isPolling) return;
        pollAttempts++;

        checkReachable(url, new ConnectionCallback() {
            @Override
            public void onResult(boolean reachable) {
                if (!isPolling) return;

                if (reachable) {
                    isPolling = false;
                    showWebView();
                    webView.loadUrl(url);
                } else if (pollAttempts >= MAX_POLL_ATTEMPTS) {
                    isPolling = false;
                    waitingProgress.setVisibility(View.GONE);
                    waitingActionButton.setEnabled(true);
                    waitingActionButton.setText("⚡ Power On");
                    waitingStatusText.setText("Still offline. Check the PC and try again.");
                } else {
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pollStep(url);
                        }
                    }, POLL_INTERVAL_MS);
                }
            }
        });
    }

    /**
     * Checkt of het opgegeven adres bereikbaar is door een korte socket-verbinding
     * te proberen. Voert de callback altijd op de UI-thread uit.
     */
    private void checkReachable(final String urlString, final ConnectionCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean reachable = false;
                try {
                    URL url = new URL(urlString);
                    String host = url.getHost();
                    int port = url.getPort();
                    if (port == -1) {
                        port = url.getDefaultPort();
                    }
                    if (port == -1) {
                        port = "https".equals(url.getProtocol()) ? 443 : 80;
                    }

                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);
                    socket.close();
                    reachable = true;
                } catch (MalformedURLException e) {
                    reachable = false;
                } catch (Exception e) {
                    reachable = false;
                }

                final boolean result = reachable;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(result);
                    }
                });
            }
        }).start();
    }

    /**
     * Stuurt een Wake-on-LAN "magic packet" naar het opgegeven MAC-adres.
     * Vereist dat WOL aanstaat op de doelmachine (BIOS + OS) en dat het
     * toestel op hetzelfde lokale netwerk zit.
     */
    private void sendWakeOnLan(final String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] macBytes = getMacBytes(macAddress);
                    byte[] bytes = new byte[6 + 16 * macBytes.length];

                    for (int i = 0; i < 6; i++) {
                        bytes[i] = (byte) 0xFF;
                    }
                    for (int i = 6; i < bytes.length; i += macBytes.length) {
                        System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
                    }

                    InetAddress address = InetAddress.getByName("255.255.255.255");
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    socket.send(packet);
                    socket.close();
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to send power on signal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("[:\\-]");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address, expected format AA:BB:CC:DD:EE:FF");
        }
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(hex[i], 16);
        }
        return bytes;
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            webView.getWebChromeClient().onHideCustomView();
        } else if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
