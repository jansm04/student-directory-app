package com.js.sd.cache.sqlite

import android.provider.BaseColumns

class UserContract private constructor() {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "data"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME_STUDENT_NAME = "name"
        const val COLUMN_NAME_STUDENT_ID = "student_id"
        const val COLUMN_NAME_ADDRESS = "address"
        const val COLUMN_NAME_LATITUDE = "latitude"
        const val COLUMN_NAME_LONGITUDE = "longitude"
        const val COLUMN_NAME_PHONE = "phone"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
    }
}