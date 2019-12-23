package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.models.raw.RawFiles
import java.io.File

class AndroidFilesProvider(
    private val androidVariantHelper: AndroidVariantHelper
) : FilesProvider {

    companion object {
        const val TEMPLATES_FOLDER_NAME = "templates"
        const val STRINGS_FOLDER_NAME = "strings"
        const val OUTPUT_RESOLVED_FILE_NAME = "resolved.xml"

        const val GATHERED_STRINGS_FILE_NAME = "strings"
        const val TEMPLATES_FILE_NAME = "templates"
        const val VALUES_FOLDER_NAME = "values"
        val RAW_VALUES_FILE_REGEX = Regex("^(?!resolved\\.xml)[A-Za-z0-9_]+\\.xml\$")
        val VALUES_FOLDER_REGEX = Regex("values(-[a-z]{2}(-r[A-Z]{2})*)*")
        val STRINGS_SUFFIX_REGEX = Regex("strings(-[a-zA-Z-]+)*")
        val TEMPLATES_SUFFIX_REGEX = Regex("templates(-[a-zA-Z-]+)*")
    }

    override fun getAllExpectedResolvedFiles(): List<File> {
//        val resolvedFiles = mutableListOf<File>()
//        for (template in getAllTemplatesFiles()) {
//            val suffix = TEMPLATES_SUFFIX_REGEX.find(template.name)!!.groupValues[1]
//            resolvedFiles.add(getResolvedFile(suffix))
//        }
//        return resolvedFiles
        TODO()
    }

    override fun getGatheredStringsFile(suffix: String): File {
        return File(
            getGatheredStringsFolder(),
            "$GATHERED_STRINGS_FILE_NAME$suffix.json"
        )
    }

    override fun getAllGatheredStringsFiles(): List<File> {
        return getGatheredStringsFolder().listFiles()?.toList() ?: emptyList()
    }

    override fun getTemplateFile(suffix: String): File {
        return File(
            getTemplatesFolder(),
            "$TEMPLATES_FILE_NAME$suffix.json"
        )
    }

    private fun getTemplatesFolder(): File {
        val folder = File(
            "${androidVariantHelper.incrementalDir}/$TEMPLATES_FOLDER_NAME"
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    override fun getAllTemplatesFiles(): List<File> {
        return getTemplatesFolder().listFiles()?.toList() ?: emptyList()
    }

    override fun getAllExpectedTemplatesFiles(): List<File> {
        return getAllGatheredStringsFiles().map {
            getTemplateFile(STRINGS_SUFFIX_REGEX.find(it.name)!!.groupValues[1])
        }
    }

    override fun getAllFoldersRawResourcesFiles(): List<RawFiles> {
//        val rawFilesModels = mutableListOf<RawFiles>()
//        for (folderName in getValuesFolderNames()) {
//            rawFilesModels.add(getRawResourcesFilesForFolder(folderName))
//        }
//        return rawFilesModels
        TODO()
    }

    private fun getGatheredStringsFolder(): File {
        val folder = File(
            "${androidVariantHelper.incrementalDir}/$STRINGS_FOLDER_NAME"
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

}