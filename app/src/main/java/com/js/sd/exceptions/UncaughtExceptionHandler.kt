package com.js.sd.exceptions

import android.content.Context
import android.content.Intent
import com.js.sd.activities.CrashActivity
import com.js.sd.util.Logman
import kotlin.system.exitProcess

class UncaughtExceptionHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        val intent = Intent(context, CrashActivity::class.java)
        val errorMessage = e.message
        intent.putExtra("error", errorMessage)

        context.startActivity(intent)
        errorMessage?.let { Logman.logErrorMessage(errorMessage) }

        // Terminate the process
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(1)
    }
}