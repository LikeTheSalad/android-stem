package com.example.placeholder.models

data class StringResourceModel(
    val attributes: Map<String, String>,
    val content: String
) : Comparable<StringResourceModel> {

    companion object {
        private const val ATTR_NAME = "name"
    }

    constructor(name: String, content: String) : this(mapOf(ATTR_NAME to name), content)

    val name = attributes.getValue(ATTR_NAME)

    override fun compareTo(other: StringResourceModel): Int {
        return name.compareTo(other.name)
    }
}