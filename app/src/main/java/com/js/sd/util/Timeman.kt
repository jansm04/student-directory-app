package com.js.sd.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Timeman {

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}