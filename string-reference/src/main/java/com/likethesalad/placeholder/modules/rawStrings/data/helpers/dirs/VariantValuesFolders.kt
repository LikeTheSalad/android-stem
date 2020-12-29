package com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs

import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesXmlFiles
import com.likethesalad.placeholder.modules.common.helpers.dirs.ValuesFoldersExtractor
import com.likethesalad.placeholder.modules.common.helpers.resources.utils.XmlUtils
import java.io.File

class VariantValuesFolders(
    val variantName: String,
    valuesFoldersExtractor: ValuesFoldersExtractor
) {
    private val valuesFolders: List<File> by lazy { valuesFoldersExtractor.getValuesFolders() }
    val valuesXmlFiles: Map<String, ValuesXmlFiles> by lazy {
        val valuesFilesMap = mutableMapOf<String, ValuesXmlFiles>()
        val valuesFolderNames = getUniqueValuesDirName()
        for (valuesFolderName in valuesFolderNames) {
            val filesInValuesFolder = getFilesFromValuesFolders(valuesFolderName)
            if (filesInValuesFolder.isNotEmpty()) {
                valuesFilesMap[valuesFolderName] =
                    ValuesXmlFiles(
                        filesInValuesFolder.toSet()
                    )
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
