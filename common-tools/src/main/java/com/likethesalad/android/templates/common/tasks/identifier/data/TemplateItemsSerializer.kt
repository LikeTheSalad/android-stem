package com.likethesalad.android.templates.common.tasks.identifier.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateItemsSerializer @Inject constructor() {

    companion object {
        private const val LIST_SEPARATOR = ","
        private const val SINGLE_ITEM_VALUES_SEPARATOR = ":"
    }

    fun serialize(templates: List<TemplateItem>): String {
        return templates.distinct()
            .joinToString(LIST_SEPARATOR) { "${it.name}$SINGLE_ITEM_VALUES_SEPARATOR${it.type}" }
    }

    fun deserialize(string: String): List<TemplateItem> {
        val separatedItems = string.split(LIST_SEPARATOR).filter { it.isNotEmpty() }

        if (separatedItems.isEmpty()) {
            return emptyList()
        }

        return separatedItems
            .map { stringToTemplateItem(it) }
    }

    private fun stringToTemplateItem(value: String): TemplateItem {
        val values = value.split(SINGLE_ITEM_VALUES_SEPARATOR)
        return TemplateItem(values[0], values[1])
    }
}