package com.likethesalad.android.resources.data

data class StringResource(val value: String, val attributes: List<Attribute>) {

    companion object {
        fun named(name: String, value: String, attributes: List<Attribute> = emptyList()): StringResource {
            val finalAttributes = mutableListOf<Attribute>()
            finalAttributes.addAll(attributes)
            finalAttributes.add(Attribute("name", name, null))

            return StringResource(value, finalAttributes)
        }
    }

    fun getName(): String {
        return attributes.first { it.name == "name" }.value
    }

    data class Attribute(val name: String, val value: String, val namespace: String?)
}