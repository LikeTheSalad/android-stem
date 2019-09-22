package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.data.resources.ResourcesHandler
import com.likethesalad.placeholder.data.storage.FilesProvider
import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.models.StringsGatheredModel
import com.likethesalad.placeholder.models.raw.FlavorValuesRawFiles
import com.likethesalad.placeholder.models.raw.RawFiles
import com.likethesalad.placeholder.utils.AndroidXmlResDocument
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GatherRawStringsTask : DefaultTask() {

    lateinit var filesProvider: FilesProvider
    lateinit var resourcesHandler: ResourcesHandler

    @TaskAction
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
                    rawFilesModel.valuesFolderName,
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