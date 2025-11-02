package com.likethesalad.stem.tools.extensions

import com.likethesalad.android.protos.StringResource
import com.likethesalad.android.protos.ValuesStringResources

fun StringResource.name(): String {
    return attributes.first { it.name == "name" }.text
}

fun ValuesStringResources.get(key: String): List<StringResource>? {
    return values[key]?.strings
}