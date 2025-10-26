package com.likethesalad.android.templates.common.tasks.identifier.data

object TemplateItemsSerializer2 {
    private const val LIST_SEPARATOR = ","

    fun serialize(templateNameIds: List<String>): String {
        return templateNameIds.distinct()
            .joinToString(LIST_SEPARATOR) { it }
    }

    fun deserialize(string: String): List<String> {
        val separatedItems = string.split(LIST_SEPARATOR).filter { it.isNotEmpty() }

        if (separatedItems.isEmpty()) {
            return emptyList()
        }

        return separatedItems
    }
}