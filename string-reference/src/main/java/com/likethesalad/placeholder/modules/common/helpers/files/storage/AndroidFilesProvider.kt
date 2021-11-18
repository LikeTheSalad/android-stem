package com.likethesalad.placeholder.modules.common.helpers.files.storage

import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.placeholder.providers.LanguageResourceFinderProvider
import com.likethesalad.tools.resource.api.android.environment.Language
import java.io.File

class AndroidFilesProvider(
    private val outputStringFileResolver: OutputStringFileResolver,
    private val incrementalDirsProvider: IncrementalDirsProvider,
    private val languageResourceFinderProvider: LanguageResourceFinderProvider
) : FilesProvider {

    companion object {
        const val VALUES_FOLDER_NAME = "values"
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

    override fun getAllTemplatesFiles(): List<File> {
        return incrementalDirsProvider.getTemplateStringsDir().listFiles()?.toList() ?: emptyList()
    }

    override fun getAllExpectedTemplatesFiles(): List<File> {
        return languageResourceFinderProvider.get().listLanguages().map {
            outputStringFileResolver.getTemplateStringsFile(getLanguageSuffix(it))
        }
    }

    private fun getLanguageSuffix(language: Language): String {
        return when (language) {
            Language.Default -> ""
            else -> "-${language.id}"
        }
    }
}