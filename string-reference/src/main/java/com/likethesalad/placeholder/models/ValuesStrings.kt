package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.data.Constants
import com.likethesalad.placeholder.data.ValuesXmlFiles
import com.likethesalad.placeholder.utils.ValuesNameUtils

data class ValuesStrings(
    val valuesFolderName: String,
    val valuesXmlFiles: ValuesXmlFiles,
    val parentValuesStrings: ValuesStrings? = null
) {

    val valuesSuffix: String by lazy {
        ValuesNameUtils.getValuesNameSuffix(valuesFolderName)
    }
    private val stringsMap: Map<String, StringResourceModel> by lazy {
        mutableMapOf<String, StringResourceModel>().also {
            addStringsToMap(valuesXmlFiles.stringResources, it)
        }
    }
    private val hasLocalTemplates: Boolean by lazy {
        stringsMap.keys.any { Constants.TEMPLATE_STRING_REGEX.matches(it) }
    }
    private val hasLocalValuesForTemplates: Boolean by lazy {
        val nonTemplatesPlaceholders = getLocalNonTemplatesNames().map { "\${$it}" }
        val mergedTemplatesContents = mergedTemplates.map { it.content }
        nonTemplatesPlaceholders.any { placeholder -> mergedTemplatesContents.any { it.contains(placeholder) } }
    }

    val mergedStrings: List<StringResourceModel> by lazy {
        val mergedMap = mutableMapOf<String, StringResourceModel>()
        if (parentValuesStrings != null) {
            addStringsToMap(parentValuesStrings.mergedStrings, mergedMap)
        }

        addStringsToMap(stringsMap.values, mergedMap)
        mergedMap.values.sorted()
    }
    val mergedTemplates: List<StringResourceModel> by lazy {
        val mergedMap = mutableMapOf<String, StringResourceModel>()
        if (parentValuesStrings != null) {
            val templates = parentValuesStrings.mergedTemplates
            for (it in templates) {
                mergedMap[it.name] = it
            }
        }

        val localTemplates = getLocalTemplates()
        for (it in localTemplates) {
            mergedMap[it.name] = it
        }

        mergedMap.values.sorted()
    }
    val hasTemplatesOrValues: Boolean by lazy {
        hasLocalTemplates || hasLocalValuesForTemplates || parentValuesStrings?.hasTemplatesOrValues == true
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
