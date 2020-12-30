package com.likethesalad.placeholder.modules.rawStrings.models

import com.likethesalad.placeholder.modules.common.helpers.resources.utils.XmlUtils
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFolders
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import java.io.File

class VariantXmlFiles(variantValuesFolders: VariantValuesFolders) {

    val variantName = variantValuesFolders.variantName
    val valuesFolderXmlFiles: List<ValuesFolderXmlFiles> by lazy {
        val valuesFiles = mutableListOf<ValuesFolderXmlFiles>()
        val valuesFolderNames = getUniqueValuesDirName()
        for (valuesFolderName in valuesFolderNames) {
            val filesInValuesFolder = getFilesFromValuesFolders(valuesFolderName)
            if (filesInValuesFolder.isNotEmpty()) {
                valuesFiles.add(ValuesFolderXmlFiles(valuesFolderName, filesInValuesFolder.toSet()))
            }
        }

        valuesFiles
    }

    fun findValuesFolderXmlFilesByName(valuesFolderName: String): ValuesFolderXmlFiles? {
        return valuesFolderXmlFiles.firstOrNull { it.valuesFolderName == valuesFolderName }
    }

    private val valuesFolders: List<File> by lazy { variantValuesFolders.valuesFolders }

    private fun getFilesFromValuesFolders(
        valuesFolderName: String
    ): List<File> {
        val filesInValuesFolder = mutableListOf<File>()

        val foldersWithName = valuesFolders.filter { it.name == valuesFolderName }

        for (folder in foldersWithName) {
            filesInValuesFolder.addAll(folder.listFiles { _, name ->
                XmlUtils.isValidRawXmlFileName(name)
            } ?: emptyArray())
        }

        return filesInValuesFolder
    }

    private fun getUniqueValuesDirName(): Set<String> {
        return valuesFolders.map {
            it.name
        }.toSet()
    }
}