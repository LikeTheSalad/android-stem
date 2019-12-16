package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantStringFiles

class VariantRawStrings(private val variantDirsPathFinder: VariantDirsPathFinder) {

    companion object {
        private const val BASE_VALUES_FOLDER_NAME = "values"
    }

    val valuesStrings: Collection<ValuesStrings> by lazy {
        val variantStringFilesList = getVariantStringFilesList()
        val uniqueValuesFolderNames = getUniqueValuesFolderNames(variantStringFilesList.map {
            it.valuesStringFiles.keys
        })

        val valuesStringsPerFolder = getValuesStringsMapPerFolder(
            uniqueValuesFolderNames.filter { it != BASE_VALUES_FOLDER_NAME },
            variantStringFilesList
        )

        valuesStringsPerFolder.values
    }

    private fun getValuesStringsMapPerFolder(
        uniqueValuesFolderNames: List<String>,
        variantStringFilesList: List<VariantStringFiles>
    ): Map<String, ValuesStrings> {
        val valuesStringsPerFolder = mutableMapOf<String, ValuesStrings>()

        val baseValuesStrings = getValuesStringsForFolderName(
            BASE_VALUES_FOLDER_NAME,
            variantStringFilesList, null
        )
        if (baseValuesStrings != null) {
            valuesStringsPerFolder[BASE_VALUES_FOLDER_NAME] = baseValuesStrings
        }

        for (valuesFolderName in uniqueValuesFolderNames) {
            val valuesStrings: ValuesStrings? = getValuesStringsForFolderName(
                valuesFolderName,
                variantStringFilesList,
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
        variantStringFilesList: List<VariantStringFiles>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        var lastValuesStrings: ValuesStrings? = parentStrings
        for (valuesStringFiles in variantStringFilesList) {
            val valuesStrings = getValuesStringsFromVariantValuesFolder(
                valuesStringFiles.variantName,
                valuesFolderName,
                valuesStringFiles.valuesStringFiles,
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
        valuesStringFilesMap: Map<String, ValuesStringFiles>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        val valuesStringFiles = valuesStringFilesMap[valuesFolderName] ?: return null
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

    private fun getVariantStringFilesList(): List<VariantStringFiles> {
        val dirsPaths = variantDirsPathFinder.existingPathsResDirs
        val variantValuesFoldersList = dirsPaths.map { VariantValuesFolders(it.variantName, it.paths) }
        return variantValuesFoldersList.map { VariantStringFiles(it.variantName, it.valuesStringFiles) }
    }

}
