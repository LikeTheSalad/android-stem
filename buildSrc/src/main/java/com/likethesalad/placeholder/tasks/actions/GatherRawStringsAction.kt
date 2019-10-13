package com.likethesalad.placeholder.tasks.actions

import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.raw.FlavorValuesRawFiles
import com.likethesalad.placeholder.models.raw.RawFiles
import com.likethesalad.placeholder.utils.AndroidXmlResDocument
import java.io.File

class GatherRawStringsAction(
    private val filesProvider: FilesProvider,
    private val resourcesHandler: ResourcesHandler
) {

    fun getInputFiles(): List<File> {
        return filesProvider.getAllFoldersRawResourcesFiles().map {
            it.getRawFilesMetaList().flatten()
        }.flatten()
    }

    fun getOutputFile(): File {
        return filesProvider.getGatheredStringsFile()
    }

    fun gatherStrings() {
        for (folderRawFiles in filesProvider.getAllFoldersRawResourcesFiles()) {
            gatherStringsForFolder(folderRawFiles)
        }
    }

    private fun gatherStringsForFolder(rawFilesModel: RawFiles) {
        val mainStrings = getStringListFromFiles(rawFilesModel.mainValuesRawFiles)

        val complimentaryStrings: List<List<StringResourceModel>> = when (rawFilesModel) {
            is FlavorValuesRawFiles -> listOf(getStringListFromFiles(rawFilesModel.complimentaryRawFiles))
            else -> listOf()
        }

        if (mainStrings.isNotEmpty() || complimentaryStrings.isNotEmpty()) {
            resourcesHandler.saveGatheredStrings(
                StringsGatheredModel(
                    rawFilesModel.suffix,
                    mainStrings,
                    complimentaryStrings
                )
            )
        }
    }

    private fun getStringListFromFiles(files: List<File>): List<StringResourceModel> {
        return files.map {
            AndroidXmlResDocument.readFromFile(it)
        }.flatMap { it.getStringResourceList() }
    }
}