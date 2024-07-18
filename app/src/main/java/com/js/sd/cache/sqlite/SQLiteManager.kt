package com.js.sd.cache.sqlite

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.js.sd.util.Logman

class SQLiteManager(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(SQLiteHelper.SQL_CREATE_ENTRIES)
            Logman.logInfoMessage("Successfully created new table.")
        } catch (e: SQLException) {
            e.message?.let { Logman.logErrorMessage(it) }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 1) {
            try {
                db.execSQL(SQLiteHelper.SQL_DELETE_ENTRIES)
                onCreate(db)
                Logman.logInfoMessage("Successfully upgraded database.")
            } catch (e: SQLException) {
                e.message?.let { Logman.logErrorMessage(it) }
            }
        }
    }

    companion object {
        const val DATABASE_VERSION: Int = 1
        const val DATABASE_NAME: String = "Data.db"
    }
}