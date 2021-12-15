package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class GatherTemplatesTask
@Inject constructor(private val args: GatherTemplatesArgs) : DefaultTask() {

    @InputDirectory
    val inDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val outDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        inDir.set(args.languageResourceFinderProvider.directoryProvider.getOutputDirProperty())
        outDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun gatherTemplateStrings() {
        val languageResourceFinder = args.languageResourceFinderProvider.get()
        args.gatherTemplatesAction.gatherTemplateStrings(outDir.get().asFile, languageResourceFinder)
    }
}