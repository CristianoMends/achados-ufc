package com.edu.achadosufc.ui.components

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun getRelativeTime(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH)
    val dateTime = LocalDateTime.parse(dateString.substringBefore(" (").trim(), formatter)
    val date = dateTime.toLocalDate()
    val now = LocalDate.now(ZoneId.systemDefault()) // Usar o fuso horário do sistema para 'agora'
    val days = ChronoUnit.DAYS.between(date, now)

    return if (days == 0L) "hoje" else "há $days dias"
}
