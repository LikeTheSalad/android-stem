package com.likethesalad.android.templates.common.tasks.templates.data

class TemplateItemsSerializer {

    companion object {
        private const val LIST_SEPARATOR = ","
        private const val SINGLE_ITEM_VALUES_SEPARATOR = ":"
    }

    fun serialize(templates: List<TemplateItem>): String {
        return templates.distinct()
            .joinToString(LIST_SEPARATOR) { "${it.name}$SINGLE_ITEM_VALUES_SEPARATOR${it.type}" }
    }

    fun deserialize(string: String): List<TemplateItem> {
        return string.split(LIST_SEPARATOR)
            .map { stringToTemplateItem(it) }
    }

    private fun stringToTemplateItem(value: String): TemplateItem {
        val values = value.split(SINGLE_ITEM_VALUES_SEPARATOR)
        return TemplateItem(values[0], values[1])
    }
}