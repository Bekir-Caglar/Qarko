package com.bekircaglar.qarko.util


fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { it.capitalize() }
}
