package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.storage.ValuesFoldersExtractor
import com.likethesalad.placeholder.data.storage.libraries.LibrariesValuesStringsProvider
import com.likethesalad.placeholder.data.storage.utils.ValuesStringsProvider
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantXmlFiles

class VariantRawStrings(
    private val variantDirsPathFinder: VariantDirsPathFinder,
    private val valuesStringsProvider: ValuesStringsProvider,
    private val librariesValuesStringsProvider: LibrariesValuesStringsProvider
) {

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

        val baseValuesStrings = valuesStringsProvider.getValuesStringsForFolderFromVariants(
            BASE_VALUES_FOLDER_NAME,
            variantXmlFilesList,
            librariesValuesStringsProvider.getValuesStringsFor(BASE_VALUES_FOLDER_NAME, null)
        )
        if (baseValuesStrings != null) {
            valuesStringsPerFolder[BASE_VALUES_FOLDER_NAME] = baseValuesStrings
        }

        for (valuesFolderName in uniqueValuesFolderNames) {
            val valuesStrings: ValuesStrings? = valuesStringsProvider.getValuesStringsForFolderFromVariants(
                valuesFolderName,
                variantXmlFilesList,
                librariesValuesStringsProvider.getValuesStringsFor(valuesFolderName, baseValuesStrings)
            )

            if (valuesStrings != null) {
                valuesStringsPerFolder[valuesFolderName] = valuesStrings
            }
        }

        return valuesStringsPerFolder
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
