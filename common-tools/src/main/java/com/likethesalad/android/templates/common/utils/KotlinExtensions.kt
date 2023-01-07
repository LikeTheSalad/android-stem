package com.likethesalad.android.templates.common.utils

import java.util.Locale

fun String.upperFirst(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.US
        ) else it.toString()
    }
}