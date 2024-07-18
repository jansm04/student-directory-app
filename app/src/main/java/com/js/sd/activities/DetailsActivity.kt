package com.js.sd.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.js.sd.R
import com.js.sd.activities.constants.Requests
import com.js.sd.exceptions.PermissionDeniedException
import com.js.sd.util.Formatman
import com.js.sd.util.Logman

class DetailsActivity : SDActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Logman.logInfoMessage("Starting details activity...")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        init()
    }

    private fun init() {
        val intent = intent

        val name = intent.getStringExtra("name")
        val studentID = intent.getIntExtra("id", 0)
        val address = intent.getStringExtra("address")
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val latitude = intent.getDoubleExtra("latitude", 0.0)

        if (name != null)
            Logman.logInfoMessage("Loading details for {}...", name)

        setTextView(R.id.name_var, name)
        setTextView(R.id.id_var, studentID.toString())
        setTextView(R.id.address_var, address)
        setTextView(R.id.long_var, longitude.toString())
        setTextView(R.id.lat_var, latitude.toString())

        loadPhoneNumber(intent)
    }

    private fun loadPhoneNumber(intent: Intent) {
        // view student phone number
        val phone = intent.getStringExtra("phone")
        if (phone != null) {
            val formattedNumber = Formatman.getFormattedNumber(phone)
            val phoneText = setTextView(R.id.phone_var, formattedNumber)
            phoneText.setOnClickListener { v: View? ->
                if (!checkPermissions(this, Manifest.permission.CALL_PHONE)) {
                    requestPermissions(
                        this,
                        Manifest.permission.CALL_PHONE,
                        Requests.REQUEST_PHONE_CALL
                    )
                } else {
                    startCall()
                }
            }
        }
    }

    private fun setTextView(id: Int, text: String?): TextView {
        val textView = findViewById<TextView>(id)
        textView.text = text
        return textView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            onRequestPermissionsResult(
                requestCode,
                grantResults,
                Requests.REQUEST_PHONE_CALL
            ) { this.startCall() }
        } catch (e: PermissionDeniedException) {
            e.logErrorMessage()
        }
    }

    private fun startCall() {
        val phoneText = findViewById<TextView>(R.id.phone_var)
        val phone = phoneText.text.toString()
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.setData(Uri.parse("tel:$phone"))
        Logman.logInfoMessage("Calling {}...", phone)
        startActivity(callIntent)
    }

    override fun handleHomeButton(item: MenuItem) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun handleMapButton(item: MenuItem) {
        startActivityWithSameData(MapActivity::class.java)
    }

    override fun handleWebButton(item: MenuItem) {
        startActivityWithSameData(WebActivity::class.java)
    }
}