package com.js.sd.model

import com.google.gson.annotations.SerializedName

class Student(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("studentID") val studentId: Int,
    @field:SerializedName("address") val address: String,
    @field:SerializedName("latitude") val latitude: Double,
    @field:SerializedName("longitude") val longitude: Double,
    @field:SerializedName("phone") val phone: String,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("timestamp") val timestamp: String
)