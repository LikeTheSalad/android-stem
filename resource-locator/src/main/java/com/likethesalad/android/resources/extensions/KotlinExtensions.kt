package com.likethesalad.android.resources.extensions

import com.likethesalad.android.protos.StringResource

fun StringResource.name(): String {
    return attributes.first { it.name == "name" }.text
}
