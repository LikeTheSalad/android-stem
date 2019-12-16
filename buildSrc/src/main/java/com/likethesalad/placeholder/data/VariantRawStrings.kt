package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.models.ValuesStrings

class VariantRawStrings(private val variantDirsPathFinder: VariantDirsPathFinder) {

    companion object {
        private const val BASE_VALUES_FOLDER_NAME = "values"
    }

    val valuesStrings: Collection<ValuesStrings> by lazy {
        val valuesStringFilesMapList = getStringFilesMapList()
        val uniqueValuesFolderNames = getUniqueValuesFolderNames(valuesStringFilesMapList.map { it.keys })

        val valuesStringsPerFolder = getValuesStringsMapPerFolder(
            uniqueValuesFolderNames.filter { it != BASE_VALUES_FOLDER_NAME },
            valuesStringFilesMapList
        )

        valuesStringsPerFolder.values
    }

    private fun getValuesStringsMapPerFolder(
        uniqueValuesFolderNames: List<String>,
        valuesStringFilesMapList: List<Map<String, ValuesStringFiles>>
    ): Map<String, ValuesStrings> {
        val valuesStringsPerFolder = mutableMapOf<String, ValuesStrings>()

        val baseValuesStrings = getValuesStringsForFolderName(
            BASE_VALUES_FOLDER_NAME,
            valuesStringFilesMapList, null
        )
        if (baseValuesStrings != null) {
            valuesStringsPerFolder[BASE_VALUES_FOLDER_NAME] = baseValuesStrings
        }

        for (valuesFolderName in uniqueValuesFolderNames) {
            val valuesStrings: ValuesStrings? = getValuesStringsForFolderName(
                valuesFolderName,
                valuesStringFilesMapList,
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
        valuesStringFilesMapList: List<Map<String, ValuesStringFiles>>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        var lastValuesStrings: ValuesStrings? = parentStrings
        for (valuesStringFilesMap in valuesStringFilesMapList) {
            val valuesStrings = getValuesStringsFromVariantValuesFolder(
                valuesFolderName,
                valuesStringFilesMap,
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
        valuesFolderName: String,
        valuesStringFilesMap: Map<String, ValuesStringFiles>,
        parentStrings: ValuesStrings?
    ): ValuesStrings? {
        val valuesStringFiles = valuesStringFilesMap[valuesFolderName] ?: return null
        return ValuesStrings(
            valuesFolderName,
            valuesStringFiles,
            parentStrings
        )
    }

    private fun getUniqueValuesFolderNames(valuesFolderNames: List<Set<String>>): Set<String> {
        return valuesFolderNames.flatten().toSet()
    }

    private fun getStringFilesMapList(): List<Map<String, ValuesStringFiles>> {
        val dirsPaths = variantDirsPathFinder.existingPathsResDirs
        val variantValuesFoldersList = dirsPaths.map { VariantValuesFolders(it.variantName, it.paths) }
        return variantValuesFoldersList.map { it.valuesStringFiles }
    }

}
