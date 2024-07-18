package com.js.sd.cache.sqlite

object SQLiteHelper {
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                UserContract.UserEntry.COLUMN_NAME_STUDENT_NAME + " TEXT," +
                UserContract.UserEntry.COLUMN_NAME_STUDENT_ID + " INTEGER," +
                UserContract.UserEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                UserContract.UserEntry.COLUMN_NAME_LATITUDE + " REAL," +
                UserContract.UserEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                UserContract.UserEntry.COLUMN_NAME_PHONE + " TEXT," +
                UserContract.UserEntry.COLUMN_NAME_TIMESTAMP + " TEXT)"

    const val SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME
}