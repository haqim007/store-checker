package dev.haqim.pitjarusapp.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun getCurrentMonthNameAndYear(locale: Locale = Locale("id", "ID")): String{
    val calendar = Calendar.getInstance()
    val monthFormat = SimpleDateFormat("MMMM", locale)
    val yearFormat = SimpleDateFormat("yyyy", locale)

    val currentMonthName = monthFormat.format(calendar.time)
    val currentYear = yearFormat.format(calendar.time)
    
    return "$currentMonthName $currentYear"
}

fun getCurrentDate(pattern: String = "dd-MM-yyyy", locale: Locale = Locale.getDefault()): String {
    val currentDate = Date()
    val dateFormat = SimpleDateFormat(pattern, locale)

    return dateFormat.format(currentDate)
}