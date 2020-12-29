package com.likethesalad.placeholder.modules.common.models

data class StringResourceModel(
    val attributes: Map<String, String>,
    val content: String
) : Comparable<StringResourceModel> {

    companion object {
        const val ATTR_NAME = "name"
        const val ATTR_TRANSLATABLE = "translatable"
    }

    constructor(name: String, content: String) : this(mapOf(ATTR_NAME to name), content)

    val name = attributes.getValue(ATTR_NAME)
    val translatable: Boolean = attributes.getOrDefault(ATTR_TRANSLATABLE, "true").toBoolean()

    override fun compareTo(other: StringResourceModel): Int {
        return name.compareTo(other.name)
    }
}