package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.data.Constants

class ResStrings(
    strings: List<StringResourceModel>,
    private val parentResStrings: ResStrings? = null
) {
    private val stringsMap = mutableMapOf<String, StringResourceModel>()

    init {
        addStringsToMap(strings, stringsMap)
    }

    fun getMergedStrings(): List<StringResourceModel> {
        val mergedMap = mutableMapOf<String, StringResourceModel>()
        if (parentResStrings != null) {
            addStringsToMap(parentResStrings.getMergedStrings(), mergedMap)
        }

        addStringsToMap(stringsMap.values, mergedMap)
        return mergedMap.values.sorted()
    }

    fun hasLocalTemplates(): Boolean {
        return stringsMap.keys.any { Constants.TEMPLATE_STRING_REGEX.matches(it) }
    }

    fun getMergedTemplates(): List<StringResourceModel> {
        val mergedMap = mutableMapOf<String, StringResourceModel>()
        if (parentResStrings != null) {
            val templates = parentResStrings.getMergedTemplates()
            for (it in templates) {
                mergedMap[it.name] = it
            }
        }

        val localTemplates = getLocalTemplates()
        for (it in localTemplates) {
            mergedMap[it.name] = it
        }

        return mergedMap.values.sorted()
    }

    fun hasLocalValuesForTemplates(): Boolean {
        val nonTemplatesPlaceholders = getLocalNonTemplatesNames().map { "\${$it}" }
        val mergedTemplatesContents = getMergedTemplates().map { it.content }
        return nonTemplatesPlaceholders.any { placeholder -> mergedTemplatesContents.any { it.contains(placeholder) } }
    }

    private fun getLocalNonTemplatesNames(): List<String> {
        return stringsMap.keys.filter { !Constants.TEMPLATE_STRING_REGEX.matches(it) }
    }

    private fun getLocalTemplates(): List<StringResourceModel> {
        return stringsMap.values.filter { Constants.TEMPLATE_STRING_REGEX.matches(it.name) }
    }

    private fun addStringsToMap(
        strings: Collection<StringResourceModel>,
        map: MutableMap<String, StringResourceModel>
    ) {
        for (it in strings) {
            map[it.name] = it
        }
    }
}
