package com.vn.note.ext

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val HH_MM_24H = "HH:mm"
const val MM_DD_YYYY = "MM/dd/yyyy"

fun Long.convertMillisToFormattedString(format: String = MM_DD_YYYY): String {
    val currentTime = Calendar.getInstance()
    val inputTime = Calendar.getInstance()

    return when {
        isSameDay(currentTime, inputTime) -> this.convertLongToDateMillis(HH_MM_24H)
        isSameWeek(currentTime, inputTime) -> convertToVietNameDay(inputTime)
        else -> this.convertLongToDateMillis(format)
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isSameWeek(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
}

fun Long?.convertLongToDateMillis(format: String = MM_DD_YYYY): String {
    if (this == null) return ""
    return DateFormat.format(format, Date(this)).toString()
}

fun convertToVietNameDay(inputTime: Calendar): String {
    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    return dayOfWeekFormat.format(inputTime.time)
}