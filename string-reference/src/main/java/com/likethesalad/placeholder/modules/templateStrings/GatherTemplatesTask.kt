package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import com.likethesalad.placeholder.utils.DirectoryUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class GatherTemplatesTask
@Inject constructor(private val args: GatherTemplatesArgs) : DefaultTask() {

    @InputDirectory
    val inDir: DirectoryProperty = project.objects.directoryProperty()

    @InputFiles
    val templates: FileCollection = project.files(args.gatherTemplatesAction.getTemplatesSourceFiles())

    @OutputDirectory
    val outDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        inDir.set(args.languageResourcesHandlerProvider.directoryProvider.getOutputDirProperty())
        outDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun gatherTemplateStrings() {
        DirectoryUtils.clearIfNeeded(outDir.get().asFile)
        val languageResourceFinder = args.languageResourcesHandlerProvider.get()
        args.gatherTemplatesAction.gatherTemplateStrings(outDir.get().asFile, languageResourceFinder)
    }
}