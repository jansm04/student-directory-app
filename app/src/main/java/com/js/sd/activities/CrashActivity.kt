package com.js.sd.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.js.sd.R
import com.js.sd.util.Logman
import kotlin.system.exitProcess


class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        val errorView = findViewById<TextView>(R.id.errorText)
        val error = intent.getStringExtra("error")
        errorView.text = error
    }

    fun onCloseButtonClick(view: View?) {
        Logman.logInfoMessage("Closing app...")
        exitProcess(0)
    }
}