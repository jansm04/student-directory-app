package com.js.sd.cache

import com.js.sd.cache.sqlite.SQLiteManager
import com.js.sd.cache.tasks.CacheTask
import com.js.sd.cache.tasks.ClearTask
import com.js.sd.exceptions.InvalidTimeException
import com.js.sd.model.Student
import java.util.Timer

class CacheManager(
    private val sqlManager: SQLiteManager,
    private val students: List<Student>
) {

    private lateinit var cacheTimer: Timer
    private lateinit var clearTimer: Timer

    @Throws(InvalidTimeException::class)
    fun startCachingInterval(seconds: Long) {
        if (seconds < 0) {
            throw InvalidTimeException("Caching interval must be a non-negative integer.")
        }
        val milliseconds = seconds * 1000
        cacheTimer = Timer()
        cacheTimer.schedule(CacheTask(sqlManager, students), 0, milliseconds)
    }

    @Throws(InvalidTimeException::class)
    fun startClearingInterval(seconds: Long) {
        if (seconds < 0) {
            throw InvalidTimeException("Clearing interval must be a non-negative integer.")
        }
        val milliseconds = seconds * 1000
        clearTimer = Timer()
        clearTimer.schedule(ClearTask(sqlManager), 1000, milliseconds) // add 1 second delay
    }

    fun stopCachingInterval() {
        cacheTimer.cancel()
    }

    fun stopClearingInterval() {
        clearTimer.cancel()
    }
}