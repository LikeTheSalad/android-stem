package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.storage.ValuesFoldersExtractor
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantXmlFiles

class VariantRawStrings(private val variantDirsPathFinder: VariantDirsPathFinder) {

    companion object {
        private const val BASE_VALUES_FOLDER_NAME = "values"
    }

    val valuesStrings: Collection<ValuesStrings> by lazy {
        val variantStringFilesList = getVariantStringFilesList()
        val uniqueValuesFolderNames = getUniqueValuesFolderNames(variantStringFilesList.map {
            it.valuesXmlFiles.keys
        })

        val valuesStringsPerFolder = getValuesStringsMapPerFolder(
            uniqueValuesFolderNames.filter { it != BASE_VALUES_FOLDER_NAME },
            variantStringFilesList
        )

        valuesStringsPerFolder.values
    }

    private fun getValuesStringsMapPerFolder(
        uniqueValuesFolderNames: List<String>,
        variantXmlFilesList: List<VariantXmlFiles>
    ): Map<String, ValuesStrings> {
        val valuesStringsPerFolder = mutableMapOf<String, ValuesStrings>()

        val baseValuesStrings = getValuesStringsForFolderName(
            BASE_VALUES_FOLDER_NAME,
            variantXmlFilesList,
            null//todo add libs 'values' folder strings here?
        )
        if (baseValuesStrings != null) {
            valuesStringsPerFolder[BASE_VALUES_FOLDER_NAME] = baseValuesStrings
        }

        for (valuesFolderName in uniqueValuesFolderNames) {
            val valuesStrings: ValuesStrings? = getValuesStringsForFolderName(
                valuesFolderName,
                variantXmlFilesList,
                baseValuesStrings
            )

            if (valuesStrings != null) {
                valuesStringsPerFolder[valuesFolderName] = valuesStrings
            }
        }

        return valuesStringsPerFolder
    }

    private fun getValuesStringsForFolderName(
        valuesFolderName: String,
        variantXmlFilesList: List<VariantXmlFiles>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        var lastValuesStrings: ValuesStrings? = parentStrings
        for (valuesStringFiles in variantXmlFilesList) {
            val valuesStrings = getValuesStringsFromVariantValuesFolder(
                valuesStringFiles.variantName,
                valuesFolderName,
                valuesStringFiles.valuesXmlFiles,
                lastValuesStrings
            )
            if (valuesStrings != null) {
                lastValuesStrings = valuesStrings
            }
        }
        if (lastValuesStrings != parentStrings) {
            return lastValuesStrings
        }
        return null
    }

    private fun getValuesStringsFromVariantValuesFolder(
        variantName: String,
        valuesFolderName: String,
        valuesXmlFilesMap: Map<String, ValuesXmlFiles>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        val valuesStringFiles = valuesXmlFilesMap[valuesFolderName] ?: return null
        return ValuesStrings(
            variantName,
            valuesFolderName,
            valuesStringFiles,
            parentStrings
        )
    }

    private fun getUniqueValuesFolderNames(valuesFolderNames: List<Set<String>>): Set<String> {
        return valuesFolderNames.flatten().toSet()
    }

    private fun getVariantStringFilesList(): List<VariantXmlFiles> {
        val dirsPaths = variantDirsPathFinder.existingPathsResDirs
        val variantValuesFoldersList =
            dirsPaths.map { VariantValuesFolders(it.variantName, ValuesFoldersExtractor(it.paths)) }
        return variantValuesFoldersList.map { VariantXmlFiles(it.variantName, it.valuesXmlFiles) }
    }

}
