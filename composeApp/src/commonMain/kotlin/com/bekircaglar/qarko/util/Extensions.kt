package com.bekircaglar.qarko.util

import androidx.compose.ui.graphics.Color

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { it.capitalize() }
}
