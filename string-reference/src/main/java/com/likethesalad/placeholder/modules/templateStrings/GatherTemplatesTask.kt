package com.likethesalad.placeholder.modules.templateStrings

import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import com.likethesalad.placeholder.utils.DirectoryUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class GatherTemplatesTask
@Inject constructor(private val args: GatherTemplatesArgs) : DefaultTask() {

    @InputDirectory
    val commonResourcesDir: DirectoryProperty = project.objects.directoryProperty()

    @InputFile
    val templateIdsFile: RegularFileProperty = project.objects.fileProperty()

    @OutputDirectory
    val outDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        outDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun gatherTemplateStrings() {
        DirectoryUtils.clearIfNeeded(outDir.get().asFile)
        args.gatherTemplatesAction.gatherTemplateStrings(
            outDir.get().asFile,
            args.commonResourcesProvider,
            templateIdsFile.get().asFile
        )
    }
}