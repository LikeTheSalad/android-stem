package com.likethesalad.stem.modules.templateStrings

import com.likethesalad.android.templates.common.tasks.BaseTask
import com.likethesalad.android.templates.common.utils.DirectoryUtils
import com.likethesalad.stem.modules.templateStrings.data.GatherTemplatesArgs
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class GatherTemplatesTask
@Inject constructor(private val args: GatherTemplatesArgs) : BaseTask() {

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