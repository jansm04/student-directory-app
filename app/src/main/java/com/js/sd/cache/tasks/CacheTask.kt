package com.js.sd.cache.tasks

import android.content.ContentValues
import com.js.sd.cache.sqlite.SQLiteManager
import com.js.sd.cache.sqlite.UserContract
import com.js.sd.model.Student
import com.js.sd.util.Logman
import com.js.sd.util.Timeman
import java.util.TimerTask

class CacheTask(
    private val sqlManager: SQLiteManager,
    private val students: List<Student>
) : TimerTask() {

    override fun run() {
        Logman.logInfoMessage("Caching user data...")
        val db = sqlManager.writableDatabase
        for (student in students) {
            val values = getContentValues(student)
            val newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values)
            Logman.logInfoMessage("New row created. ID: $newRowId")
        }
    }

    private fun getContentValues(student: Student): ContentValues {
        val values = ContentValues()
        values.put(UserContract.UserEntry.COLUMN_NAME_STUDENT_NAME, student.name)
        values.put(UserContract.UserEntry.COLUMN_NAME_STUDENT_ID, student.studentId)
        values.put(UserContract.UserEntry.COLUMN_NAME_ADDRESS, student.address)
        values.put(UserContract.UserEntry.COLUMN_NAME_LATITUDE, student.latitude)
        values.put(UserContract.UserEntry.COLUMN_NAME_LONGITUDE, student.longitude)
        values.put(UserContract.UserEntry.COLUMN_NAME_PHONE, student.phone)
        values.put(UserContract.UserEntry.COLUMN_NAME_TIMESTAMP, Timeman.getCurrentTimestamp())
        return values
    }
}