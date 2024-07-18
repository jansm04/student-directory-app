package com.js.sd.cache.tasks

import com.js.sd.cache.sqlite.SQLiteManager
import com.js.sd.cache.sqlite.UserContract
import com.js.sd.exceptions.AppException
import com.js.sd.https.ApiClient
import com.js.sd.model.Student
import com.js.sd.properties.Properties
import com.js.sd.util.Logman
import com.js.sd.util.Timeman
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response

import java.util.TimerTask

class ClearTask(
    private val sqlManager: SQLiteManager
) : TimerTask() {
    override fun run() {
        val db = sqlManager.writableDatabase
        val currentTimestamp = Timeman.getCurrentTimestamp()
        val whereClause = UserContract.UserEntry.COLUMN_NAME_TIMESTAMP + " <= ?"
        val whereArgs = arrayOf(currentTimestamp)
        val cursor = db.query(
            UserContract.UserEntry.TABLE_NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null,
            1000.toString()
        )

        val students = ArrayList<Student>()
        val rowIds = StringBuilder("(")
        while (cursor.moveToNext()) {
            students.add(
                Student(
                    cursor.getString    (cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_STUDENT_NAME)),
                    cursor.getInt       (cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_STUDENT_ID)),
                    cursor.getString    (cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_ADDRESS)),
                    cursor.getDouble    (cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_LATITUDE)),
                    cursor.getDouble    (cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_LONGITUDE)),
                    cursor.getString    (cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_PHONE)),
                    "",
                    currentTimestamp
                )
            )
            rowIds.append(cursor.getInt(0)).append(",")
        }
        cursor.close()
        rowIds.deleteCharAt(rowIds.length - 1).append(")")

        val size = students.size
        Logman.logInfoMessage("Posting {} records to API endpoint at {}.", size, currentTimestamp)

        try {
            val apiService = ApiClient.createService(Properties.USERNAME, Properties.PASSWORD)
            val call = apiService.postStudents(students)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Logman.logInfoMessage("Successfully posted {} record to API endpoint at {}.", size, currentTimestamp)

                    // clear cache if data is successfully posted
                    val deleteClause = "_id IN $rowIds"
                    val deleteArgs = emptyArray<String>()
                    val deletedRows = db.delete(
                        UserContract.UserEntry.TABLE_NAME,
                        deleteClause,
                        deleteArgs
                    )
                    Logman.logInfoMessage("Successfully deleted {} rows.", deletedRows)
                }

                override fun onFailure(call: Call<Void>, throwable: Throwable) {
                    Logman.logErrorMessage("An error occurred trying to post the data. As a result, the cache was not yet cleared.")
                }
            })
        } catch (e: AppException) {
            e.logErrorMessage()
        }
    }
}