package com.js.sd.exceptions

import com.js.sd.util.Logman


open class AppException(message: String) : Exception(message) {
    fun logErrorMessage() {
        message?.let { Logman.logErrorMessage(it) }
    }
}