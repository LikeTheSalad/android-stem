package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.data.Constants
import com.likethesalad.placeholder.data.ValuesStringFiles
import com.likethesalad.placeholder.utils.ValuesNameUtils

data class ValuesStrings(
    val variantName: String,
    val valuesFolderName: String,
    val valuesStringFiles: ValuesStringFiles,
    private val parentValuesStrings: ValuesStrings? = null
) {

    val valuesSuffix: String by lazy {
        ValuesNameUtils.getValuesNameSuffix(valuesFolderName)
    }
    private val stringsMap = mutableMapOf<String, StringResourceModel>()
    private val hasLocalTemplates: Boolean by lazy {
        stringsMap.keys.any { Constants.TEMPLATE_STRING_REGEX.matches(it) }
    }
    private val hasLocalValuesForTemplates: Boolean by lazy {
        val nonTemplatesPlaceholders = getLocalNonTemplatesNames().map { "\${$it}" }
        val mergedTemplatesContents = mergedTemplates.map { it.content }
        nonTemplatesPlaceholders.any { placeholder -> mergedTemplatesContents.any { it.contains(placeholder) } }
    }

    init {
        addStringsToMap(valuesStringFiles.stringResources, stringsMap)
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
    val primaryVariantName: String by lazy {
        if (hasLocalTemplates || hasLocalValuesForTemplates) {
            variantName
        } else parentValuesStrings?.primaryVariantName ?: ""
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
