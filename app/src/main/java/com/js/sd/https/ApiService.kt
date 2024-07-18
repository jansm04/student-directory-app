package com.js.sd.https

import com.js.sd.model.Student
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api3.php")
    fun getStudents(): Call<List<Student>>

    @POST("api2.php")
    fun postStudents(@Body students: List<Student>): Call<Void>
}