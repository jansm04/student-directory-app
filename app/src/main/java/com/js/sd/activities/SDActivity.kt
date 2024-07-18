package com.js.sd.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.js.sd.R
import com.js.sd.exceptions.PermissionDeniedException
import com.google.android.material.bottomnavigation.BottomNavigationView

open class SDActivity : AppCompatActivity() {

    open fun handleHomeButton(item: MenuItem) {}

    open fun handleMapButton(item: MenuItem) {}

    open fun handleWebButton(item: MenuItem) {}

    protected fun startActivityWithSameData(targetActivity: Class<*>) {
        val newIntent = Intent(this, targetActivity)
        if (intent.extras != null) {
            newIntent.putExtras(intent.extras!!)
        }
        startActivity(newIntent)
    }

    protected fun moveTaskToBackgroundOnBack() {
        val onBackPressedDispatcher = onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        })
    }

    protected fun setCheckedTab(tabId: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.getItem(tabId).setChecked(true)
    }

    protected fun checkPermissions(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected fun requestPermissions(activity: Activity, permission: String, permissionID: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), permissionID)
    }

    @Throws(PermissionDeniedException::class)
    protected fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        permissionID: Int,
        callback: Runnable
    ) {
        if (requestCode == permissionID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.run()
            } else {
                val message = "Permission denied."
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                throw PermissionDeniedException(message)
            }
        }
    }

}