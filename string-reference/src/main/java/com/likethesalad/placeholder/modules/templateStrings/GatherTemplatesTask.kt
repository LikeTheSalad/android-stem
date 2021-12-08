package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class GatherTemplatesTask
@Inject constructor(private val args: GatherTemplatesArgs) : DefaultTask() {

    @InputFiles
    val inDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputFiles
    fun getTemplatesFiles(): List<File> = args.gatherTemplatesAction.getTemplatesFiles()

    init {
        inDir.set(args.languageResourceFinderProvider.directoryProvider.getOutputDirProperty())
    }

    @TaskAction
    fun gatherTemplateStrings() {
        val languageResourceFinder = args.languageResourceFinderProvider.get()
        args.gatherTemplatesAction.gatherTemplateStrings(languageResourceFinder)
    }
}