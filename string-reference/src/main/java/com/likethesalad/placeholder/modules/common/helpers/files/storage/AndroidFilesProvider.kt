package com.likethesalad.placeholder.modules.common.helpers.files.storage

import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import java.io.File

class AndroidFilesProvider(
    private val outputStringFileResolver: OutputStringFileResolver,
    private val incrementalDirsProvider: IncrementalDirsProvider
) : FilesProvider {

    companion object {
        const val VALUES_FOLDER_NAME = "values"
        val STRINGS_SUFFIX_REGEX = Regex("strings(-[a-zA-Z-]+)*")
        val TEMPLATES_SUFFIX_REGEX = Regex("templates(-[a-zA-Z-]+)*")
    }

    override fun getAllExpectedResolvedFiles(): List<File> {
        val resolvedFiles = mutableListOf<File>()
        for (template in getAllTemplatesFiles()) {
            val suffix = TEMPLATES_SUFFIX_REGEX.find(template.name)!!.groupValues[1]
            resolvedFiles.add(
                outputStringFileResolver.getResolvedStringsFile("$VALUES_FOLDER_NAME$suffix")
            )
        }
        return resolvedFiles
    }

    override fun getAllGatheredStringsFiles(): List<File> {
        return incrementalDirsProvider.getRawStringsDir().listFiles()?.toList() ?: emptyList()
    }

    override fun getAllTemplatesFiles(): List<File> {
        return incrementalDirsProvider.getTemplateStringsDir().listFiles()?.toList() ?: emptyList()
    }

    override fun getAllExpectedTemplatesFiles(): List<File> {
        return getAllGatheredStringsFiles().map {
            outputStringFileResolver
                .getTemplateStringsFile(STRINGS_SUFFIX_REGEX.find(it.name)!!.groupValues[1]) // todo confirm
        }
    }
}