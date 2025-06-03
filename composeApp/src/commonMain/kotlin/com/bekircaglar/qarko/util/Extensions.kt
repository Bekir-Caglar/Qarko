package com.bekircaglar.qarko.util


fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { it.capitalize() }
}


fun Double.toPriceString(): String {
    val absValue = if (this < 0) -this else this
    val intPart = absValue.toLong()
    val decimalPart = ((absValue - intPart) * 100).toInt()
    val sign = if (this < 0) "-" else ""
    val intStr = intPart.toString().reversed().chunked(3).joinToString(".").reversed()
    val decimalStr = if (decimalPart < 10) "0$decimalPart" else "$decimalPart"
    return "${sign}₺$intStr,$decimalStr"
}