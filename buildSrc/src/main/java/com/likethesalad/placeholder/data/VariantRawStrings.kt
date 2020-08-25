package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.storage.ValuesFoldersExtractor
import com.likethesalad.placeholder.data.storage.libraries.LibrariesValuesStringsProvider
import com.likethesalad.placeholder.data.storage.utils.ValuesStringsProvider
import com.likethesalad.placeholder.models.ValuesStrings
import com.likethesalad.placeholder.models.VariantXmlFiles
import java.io.File

class VariantRawStrings(
    private val variantDirsPathFinder: VariantDirsPathFinder,
    private val valuesStringsProvider: ValuesStringsProvider,
    private val librariesValuesStringsProvider: LibrariesValuesStringsProvider
) {

    companion object {
        private const val BASE_VALUES_FOLDER_NAME = "values"
    }

    fun getValuesStrings(generatedResDirs: Set<File>): Collection<ValuesStrings> {
        val variantStringFilesList = getVariantStringFilesList(generatedResDirs)
        val uniqueValuesFolderNames = getUniqueValuesFolderNames(variantStringFilesList.map {
            it.valuesXmlFiles.keys
        })

        val valuesStringsPerFolder = getValuesStringsMapPerFolder(
            uniqueValuesFolderNames.filter { it != BASE_VALUES_FOLDER_NAME },
            variantStringFilesList
        )

        return valuesStringsPerFolder.values
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

    private fun getVariantStringFilesList(generatedResDir: Set<File>): List<VariantXmlFiles> {
        val dirsPaths = variantDirsPathFinder
            .getExistingPathsResDirs(getExtraMainResDirs(generatedResDir))
        val variantValuesFoldersList =
            dirsPaths.map { VariantValuesFolders(it.variantName, ValuesFoldersExtractor(it.paths)) }
        return variantValuesFoldersList.map { VariantXmlFiles(it.variantName, it.valuesXmlFiles) }
    }

    private fun getExtraMainResDirs(generatedResDirs: Set<File>): List<File>? {
        val existingNotEmptyDirs = generatedResDirs.filter {
            it.exists() && it.listFiles()?.isNotEmpty() == true
        }

        if (existingNotEmptyDirs.isNotEmpty()) {
            return existingNotEmptyDirs
        }

        return null
    }

}
