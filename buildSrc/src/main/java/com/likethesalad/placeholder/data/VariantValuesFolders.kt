package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.storage.ValuesFoldersExtractor
import com.likethesalad.placeholder.utils.XmlUtils
import java.io.File

class VariantValuesFolders(
    val variantName: String,
    valuesFoldersExtractor: ValuesFoldersExtractor
) {
    private val valuesFolders: List<File> by lazy { valuesFoldersExtractor.getValuesFolders() }
    val valuesStringFiles: Map<String, ValuesStringFiles> by lazy {
        val valuesFilesMap = mutableMapOf<String, ValuesStringFiles>()
        val valuesFolderNames = getUniqueValuesDirName()
        for (valuesFolderName in valuesFolderNames) {
            val filesInValuesFolder = getFilesFromValuesFolders(valuesFolderName)
            if (filesInValuesFolder.isNotEmpty()) {
                valuesFilesMap[valuesFolderName] = ValuesStringFiles(filesInValuesFolder.toSet())
            }
        }

        valuesFilesMap
    }

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
