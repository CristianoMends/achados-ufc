package com.edu.achadosufc.utils

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


fun getRelativeTime(dateString: String?): String {
    if (dateString.isNullOrBlank()) {
        return "data indisponível"
    }

    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val dateTime = java.time.LocalDateTime.parse(dateString, formatter)
        val now = java.time.LocalDateTime.now()

        val seconds = ChronoUnit.SECONDS.between(dateTime, now)
        val minutes = ChronoUnit.MINUTES.between(dateTime, now)
        val hours = ChronoUnit.HOURS.between(dateTime, now)
        val days = ChronoUnit.DAYS.between(dateTime, now)
        val months = ChronoUnit.MONTHS.between(dateTime, now)
        val years = ChronoUnit.YEARS.between(dateTime, now)

        when {
            days < 0 -> "em ${dateTime.format(formatter)}"
            seconds < 60 -> "agora mesmo"
            minutes < 60 -> "há $minutes ${if (minutes == 1L) "minuto" else "minutos"}"
            hours < 24 -> "há $hours ${if (hours == 1L) "hora" else "horas"}"
            days == 1L -> "há 1 dia"
            months < 1 -> "há $days dias"
            else -> "${
                dateTime.dayOfMonth.toString().padStart(2, '0')
            } de ${
                dateTime.month.getDisplayName(
                    java.time.format.TextStyle.FULL,
                    java.util.Locale("pt", "BR")
                )
            } de ${dateTime.year}"
        }
    } catch (e: Exception) {
        "data inválida"
    }
}