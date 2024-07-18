package com.js.sd.util

import org.tinylog.Level
import org.tinylog.format.AdvancedMessageFormatter
import org.tinylog.format.MessageFormatter
import org.tinylog.provider.ProviderRegistry
import java.util.Locale

object Logman {

    private val provider = ProviderRegistry.getLoggingProvider()

    fun logInfoMessage(infoMessage: String) {
        logMessage(Level.INFO, null, infoMessage)
    }

    fun logInfoMessage(infoMessage: String, vararg arguments: Any) {
        val formatter = AdvancedMessageFormatter(Locale.ENGLISH, false)
        logMessage(Level.INFO, formatter, infoMessage, *arguments)
    }

    fun logErrorMessage(errorMessage: String) {
        logMessage(Level.ERROR, null, errorMessage);
    }

    private fun logMessage(level: Level, formatter: MessageFormatter?, message: String, vararg arguments: Any) {
        provider.log(
            3,          // depth in stack trace
            null,       // optional tag
            level,      // severity level
            null,       // exception or any other kind of throwable
            formatter,  // message formatter for arguments
            message,    // message to log
            *arguments  // arguments for message
        )
    }
}