package com.likethesalad.stem.tools.extensions

import com.likethesalad.android.protos.StringResource
import com.likethesalad.android.protos.ValuesStringResources
import java.util.Locale

fun StringResource.name(): String {
    return attributes.first { it.name == "name" }.text
}

fun ValuesStringResources.get(key: String): List<StringResource>? {
    return values[key]?.strings
}

// Todo remove
fun String.upperFirst(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.US
        ) else it.toString()
    }
}
