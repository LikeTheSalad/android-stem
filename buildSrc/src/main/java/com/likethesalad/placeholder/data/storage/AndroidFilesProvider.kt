package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.models.raw.FlavorValuesRawFiles
import com.likethesalad.placeholder.models.raw.MainValuesRawFiles
import com.likethesalad.placeholder.models.raw.RawFiles
import java.io.File

class AndroidFilesProvider(
    private val androidVariantHelper: AndroidVariantHelper
) : FilesProvider {

    companion object {
        const val TEMPLATES_FOLDER_NAME = "templates"
        const val STRINGS_FOLDER_NAME = "strings"
        const val OUTPUT_RESOLVED_FILE_NAME = "resolved.xml"
        const val BASE_VALUES_FOLDER_NAME = "values"

        const val STRINGS_FILE_NAME_FORMAT = "strings%s"
        const val TEMPLATES_FILE_NAME_FORMAT = "templates%s"
        const val VALUES_FOLDER_NAME_FORMAT = "values%s"
        val RAW_VALUES_FILE_REGEX = Regex("^(?!resolved)[A-Za-z0-9_]+\\.xml\$")
        val VALUES_FOLDER_REGEX = Regex("values(-[a-z]{2}(-r[A-Z]{2})*)*")
        val VALUES_SUFFIX_REGEX = Regex("values(-[a-zA-Z-]+)*")
        val STRINGS_SUFFIX_REGEX = Regex("strings(-[a-zA-Z-]+)*")
        val TEMPLATES_SUFFIX_REGEX = Regex("templates(-[a-zA-Z-]+)*")
    }

    override fun getResolvedFileForValuesFolder(valuesFolderName: String): File {
        val resourcesDirs = androidVariantHelper.resourceDirs
        val folder = if (resourcesDirs.hasFlavorDirs) {
            File(resourcesDirs.flavorDirs.first().absolutePath + "/" + valuesFolderName)
        } else {
            File(resourcesDirs.mainDirs.first().absolutePath + "/" + valuesFolderName)
        }
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return File(folder, OUTPUT_RESOLVED_FILE_NAME)
    }

    override fun getAllExpectedResolvedFiles(): List<File> {
        val resolvedFiles = mutableListOf<File>()
        for (template in getAllTemplatesFiles()) {
            resolvedFiles.add(getResolvedFileForValuesFolder(getValuesFolderNameForTemplateFile(template.name)))
        }
        return resolvedFiles
    }

    override fun getGatheredStringsFileForFolder(valuesFolderName: String): File {
        return File(
            getGatheredStringsFolder(),
            "${getStringFileNameForValuesFolder(valuesFolderName)}.json"
        )
    }

    override fun getAllGatheredStringsFiles(): List<File> {
        return getGatheredStringsFolder().listFiles()?.toList() ?: emptyList()
    }

    override fun getTemplateFileForStringFile(stringFile: File): File {
        return File(
            getTemplatesFolder(),
            "${getTemplateFileNameForStringsFile(stringFile.name)}.json"
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
        return getAllGatheredStringsFiles().map { getTemplateFileForStringFile(it) }
    }

    override fun getRawResourcesFilesForFolder(valuesFolderName: String): RawFiles {
        val resourcesDirs = androidVariantHelper.resourceDirs
        return if (androidVariantHelper.isFlavored) {
            getFlavorRawFiles(valuesFolderName, resourcesDirs.mainDirs, resourcesDirs.flavorDirs)
        } else {
            getMainRawFiles(valuesFolderName, resourcesDirs.mainDirs)
        }
    }

    private fun getFlavorRawFiles(
        valuesFolderName: String,
        mainResDirs: Set<File>,
        flavorResDirs: Set<File>
    ): FlavorValuesRawFiles {
        return FlavorValuesRawFiles(
            androidVariantHelper.flavor,
            valuesFolderName,
            getResourcesFiles(valuesFolderName, mainResDirs),
            getResourcesFiles(valuesFolderName, flavorResDirs)
        )
    }

    private fun getMainRawFiles(valuesFolderName: String, mainResDirs: Set<File>): MainValuesRawFiles {
        return MainValuesRawFiles(
            valuesFolderName,
            getResourcesFiles(valuesFolderName, mainResDirs)
        )
    }

    override fun getAllFoldersRawResourcesFiles(): List<RawFiles> {
        val rawFilesModels = mutableListOf<RawFiles>()
        for (folderName in getValuesFolderNames()) {
            rawFilesModels.add(getRawResourcesFilesForFolder(folderName))
        }
        return rawFilesModels
    }

    private fun getValuesFolderNames(): Set<String> {
        val valuesFoldersNames = mutableSetOf<String>()
        val resDirs = androidVariantHelper.resourceDirs
        val mainDirsValueFiles = getAllValuesFolders(resDirs.mainDirs)
        valuesFoldersNames.addAll(mainDirsValueFiles.map { it.name })

        if (resDirs.hasFlavorDirs) {
            val flavorDirsValueFiles = getAllValuesFolders(resDirs.flavorDirs)
            valuesFoldersNames.addAll(flavorDirsValueFiles.map { it.name })
        }

        return valuesFoldersNames
    }

    private fun getResourcesFiles(valuesFolderName: String, resourcesDirs: Set<File>): List<File> {
        val valuesFolders = getValuesFoldersByName(valuesFolderName, resourcesDirs)
        return valuesFolders.map {
            it.listFiles { _, name ->
                RAW_VALUES_FILE_REGEX.matches(name)
            }?.toList() ?: emptyList()
        }.flatten()
    }

    private fun getValuesFoldersByName(valuesFolderName: String, resourcesDirs: Set<File>): List<File> {
        return resourcesDirs.map {
            it.listFiles { _, name ->
                name == valuesFolderName
            }?.toList() ?: emptyList()
        }.flatten()
    }

    private fun getAllValuesFolders(resourcesDirs: Set<File>): List<File> {
        return resourcesDirs.map {
            it.listFiles { _, name ->
                VALUES_FOLDER_REGEX.matches(name)
            }?.toList() ?: emptyList()
        }.flatten()
    }

    private fun getStringFileNameForValuesFolder(valuesFolderName: String): String {
        val match = VALUES_SUFFIX_REGEX.find(valuesFolderName)!!
        val suffix = match.groupValues[1]
        return String.format(STRINGS_FILE_NAME_FORMAT, suffix)
    }

    private fun getValuesFolderNameForTemplateFile(templatesFileName: String): String {
        val match = TEMPLATES_SUFFIX_REGEX.find(templatesFileName)!!
        val suffix = match.groupValues[1]
        return String.format(VALUES_FOLDER_NAME_FORMAT, suffix)
    }

    private fun getTemplateFileNameForStringsFile(stringsFileName: String): String {
        val match = STRINGS_SUFFIX_REGEX.find(stringsFileName)!!
        val suffix = match.groupValues[1]
        return String.format(TEMPLATES_FILE_NAME_FORMAT, suffix)
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