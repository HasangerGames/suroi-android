package com.zereshk.suroi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val suroiWebView = WebView(this)
        setContentView(suroiWebView)
        val systemUI = WindowCompat.getInsetsController(window, window.decorView)
        systemUI.hide(WindowInsetsCompat.Type.systemBars())
        systemUI.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        suroiWebView.webChromeClient = ChromeClient(this)
        suroiWebView.settings.javaScriptEnabled = true
        suroiWebView.settings.domStorageEnabled = true
        var suroiLink = "https://suroi.io"
        suroiLink = intent.data?.toString() ?: suroiLink
        if (isOnline(this)) suroiWebView.loadUrl(suroiLink)
        else suroiWebView.loadUrl("file:///android_asset/offline.html")
        suroiWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (!request.url.toString().startsWith("https://suroi.io/")) {
                    view.context.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                    return true
                }
                return false
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.evaluateJavascript("""
            document.querySelectorAll('.btn-kofi').forEach(function(element) {
                element.style.display = 'none';
            });
        """, null)
            }
        }
        Shake(this, getSystemService(SENSOR_SERVICE) as SensorManager){
            suroiWebView.evaluateJavascript(
                "document.getElementById('weapon-clip-ammo-count').click();",null)
        }
    }
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}