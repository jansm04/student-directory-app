package com.js.sd.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import com.js.sd.R
import com.js.sd.activities.constants.Tabs
import com.js.sd.properties.Properties
import com.js.sd.util.Logman

class WebActivity : SDActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        // to exit app on return button
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setCheckedTab(Tabs.WEB)
        moveTaskToBackgroundOnBack()
        loadWebView()
    }

    private fun loadWebView() {
        Logman.logInfoMessage("Loading web view...")
        val webView = findViewById<WebView>(R.id.web)
        webView.loadUrl(Properties.WEB_VIEW_URL)
    }

    override fun handleHomeButton(item: MenuItem) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun handleMapButton(item: MenuItem) {
        startActivityWithSameData(MapActivity::class.java)
    }
}