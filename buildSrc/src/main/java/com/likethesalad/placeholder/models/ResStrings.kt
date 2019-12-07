package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.data.Constants

class ResStrings(
    strings: List<StringResourceModel>,
    private val parentResStrings: ResStrings? = null
) {
    private val stringsMap = mutableMapOf<String, StringResourceModel>()

    init {
        initializeLocalMapWithStrings(strings)
    }

    fun getMergedStrings(): List<StringResourceModel> {
        val mergedMap = mutableMapOf<String, StringResourceModel>()
        if (parentResStrings != null) {
            addParentStrings(parentResStrings, mergedMap)
        }

        addLocalStrings(mergedMap)
        return mergedMap.values.sorted()
    }

    fun hasTemplates(): Boolean {
        return stringsMap.keys.any { Constants.TEMPLATE_STRING_REGEX.matches(it) }
    }

    private fun addLocalStrings(mergedMap: MutableMap<String, StringResourceModel>) {
        for ((key, value) in stringsMap) {
            mergedMap[key] = value
        }
    }

    private fun addParentStrings(
        parentResStrings: ResStrings,
        mergedMap: MutableMap<String, StringResourceModel>
    ) {
        for (it in parentResStrings.getMergedStrings()) {
            mergedMap[it.name] = it
        }
    }

    private fun initializeLocalMapWithStrings(strings: List<StringResourceModel>) {
        for (it in strings) {
            stringsMap[it.name] = it
        }
    }
}
