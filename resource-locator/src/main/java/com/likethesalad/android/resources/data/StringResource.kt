package com.likethesalad.android.resources.data

data class StringResource(val value: String, val attributes: List<Attribute>) {

    fun getName(): String {
        return attributes.first { it.name == "name" }.value
    }

    data class Attribute(val name: String, val value: String, val namespace: String?)
}