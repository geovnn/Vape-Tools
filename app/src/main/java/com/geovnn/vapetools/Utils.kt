package com.geovnn.vapetools

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun safeStringToDouble(input: String): String? {
    return if(input=="") {
        input
    }else if (input.toDoubleOrNull()!=null) {
        input
    } else {
        null
    }
}

fun formatMillisToDate(milliseconds: Long?): String {
    return if (milliseconds!=null) {
        val instant = Instant.ofEpochMilli(milliseconds)
        val zoneId = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyy").withZone(zoneId)
        formatter.format(instant)
    } else {
        ""
    }
}

fun getDaysSinceDateInMillis(dateInMillis: Long?): String {
    return if (dateInMillis!=null) {
        val currentDateMillis = System.currentTimeMillis()
        val differenceInMillis = currentDateMillis - dateInMillis
        val daysSinceDate = differenceInMillis / 86_400_000
        daysSinceDate.toString()
    } else {
        ""
    }
}