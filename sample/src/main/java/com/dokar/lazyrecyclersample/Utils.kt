package com.dokar.lazyrecyclersample

import java.util.Calendar
import java.util.Calendar.YEAR
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

fun Long.isToday(): Boolean {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance()
    target.timeInMillis = this
    return now.get(YEAR) == target.get(YEAR) && now.get(DAY_OF_YEAR) == target.get(DAY_OF_YEAR)
}

fun Long.isSameMinute(target: Long): Boolean {
    if (this == target) {
        return true
    }
    val c1 = Calendar.getInstance()
    c1.timeInMillis = this
    val c2 = Calendar.getInstance()
    c2.timeInMillis = target
    return c1.get(YEAR) == c2.get(YEAR) && c1.get(DAY_OF_YEAR) == c2.get(DAY_OF_YEAR)
            && c1.get(HOUR_OF_DAY) == c2.get(HOUR_OF_DAY) && c1.get(MINUTE) == c2.get(MINUTE)
}