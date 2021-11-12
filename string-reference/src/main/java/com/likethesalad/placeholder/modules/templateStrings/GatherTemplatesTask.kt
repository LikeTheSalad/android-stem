package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class GatherTemplatesTask
@Inject constructor(private val args: GatherTemplatesArgs) : DefaultTask() {

    @InputFiles
    val inDir: FileCollection = args.languageResourceFinderProvider.directory

    @OutputFiles
    fun getTemplatesFiles(): List<File> = args.gatherTemplatesAction.getTemplatesFiles()

    @TaskAction
    fun gatherTemplateStrings() {
        val languageResourceFinder = args.languageResourceFinderProvider.get()
        args.gatherTemplatesAction.gatherTemplateStrings(languageResourceFinder)
    }
}