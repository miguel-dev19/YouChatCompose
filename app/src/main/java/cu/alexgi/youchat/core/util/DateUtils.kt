package cu.alexgi.youchat.core.util

import java.text.SimpleDateFormat
import java.util.*

fun String.toFileSize(): String {
    val bytes = this.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
    }
}

fun Long.toFileSize(): String {
    return when {
        this < 1024 -> "$this B"
        this < 1024 * 1024 -> "${this / 1024} KB"
        else -> "${"%.1f".format(this / (1024.0 * 1024.0))} MB"
    }
}

fun String.conversionFecha(): String {
    if (length < 8) return this
    val dd = "${this[6]}${this[7]}"
    val mm = "${this[4]}${this[5]}"
    val aa = "${this[0]}${this[1]}${this[2]}${this[3]}"
    return "$dd/$mm/$aa"
}

fun String.conversionHora(): String {
    if (length < 12) return this
    val h = (this[8] - '0') * 10 + (this[9] - '0')
    val m = "${this[10]}${this[11]}"
    return when {
        h == 0 -> "12:$m am"
        h > 12 -> "${h-12}:$m pm"
        h == 12 -> "12:$m pm"
        else -> "$h:$m am"
    }
}

fun generateOrderId(): String {
    val sdf = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
    return sdf.format(Date())
}
