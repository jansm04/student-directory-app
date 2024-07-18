package com.js.sd.util

object Formatman {

    fun getIdText(studentID: Int): String {
        return "ID: $studentID"
    }

    fun getCoordinatesText(latitude: Double, longitude: Double): String {
        return "($latitude, $longitude)"
    }

    fun getFormattedNumber(phone: String): String {
        return phone.replaceFirst(
            "(\\d{3})(\\d{3})(\\d+)".toRegex(),
            "($1)-$2-$3"
        )
    }
}