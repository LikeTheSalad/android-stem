package com.likethesalad.placeholder.modules.rawStrings.models

import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.common.helpers.dirs.utils.ValuesNameUtils
import com.likethesalad.placeholder.modules.common.helpers.files.AndroidXmlResDocument
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles

data class ValuesFolderStrings(
    val valuesFolderName: String,
    val valuesFolderXmlFiles: ValuesFolderXmlFiles,
    val parentValuesFolderStrings: ValuesFolderStrings? = null,
    val stringResourcesProvider: ((ValuesFolderXmlFiles) -> Set<StringResourceModel>)? = null
) {

    val valuesSuffix: String by lazy {
        ValuesNameUtils.getValuesNameSuffix(valuesFolderName)
    }
    private val stringsMap: Map<String, StringResourceModel> by lazy {
        mutableMapOf<String, StringResourceModel>().also {
            val strings =
                stringResourcesProvider?.invoke(valuesFolderXmlFiles) ?: getStringResources(valuesFolderXmlFiles)
            addStringsToMap(strings, it)
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
        if (parentValuesFolderStrings != null) {
            addStringsToMap(parentValuesFolderStrings.mergedStrings, mergedMap)
        }

        addStringsToMap(stringsMap.values, mergedMap)
        mergedMap.values.sorted()
    }
    val mergedTemplates: List<StringResourceModel> by lazy {
        val mergedMap = mutableMapOf<String, StringResourceModel>()
        if (parentValuesFolderStrings != null) {
            val templates = parentValuesFolderStrings.mergedTemplates
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
        hasLocalTemplates || hasLocalValuesForTemplates || parentValuesFolderStrings?.hasTemplatesOrValues == true
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

    private fun getStringResources(valuesFolderXmlFiles: ValuesFolderXmlFiles): Set<StringResourceModel> {
        return valuesFolderXmlFiles.xmlFiles.map {
            AndroidXmlResDocument.readFromFile(it).getStringResourceList()
        }.flatten().toSet()
    }
}
