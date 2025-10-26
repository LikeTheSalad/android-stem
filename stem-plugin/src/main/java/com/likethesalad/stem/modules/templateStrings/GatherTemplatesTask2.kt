package com.likethesalad.stem.modules.templateStrings

import com.likethesalad.android.templates.common.tasks.BaseTask
import com.likethesalad.android.templates.common.utils.DirectoryUtils
import com.likethesalad.stem.modules.templateStrings.data.GatherTemplatesArgs2
import javax.inject.Inject
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

open class GatherTemplatesTask2
@Inject constructor(private val args: GatherTemplatesArgs2) : BaseTask() {

    @InputFiles
    val libraryResources: ConfigurableFileCollection = project.objects.fileCollection()

    @OutputDirectory
    val outDir: DirectoryProperty = project.objects.directoryProperty()

    @InputFile
    val templateIdsFile: RegularFileProperty = project.objects.fileProperty()

    init {
        outDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun gatherTemplateStrings() {
        DirectoryUtils.clearIfNeeded(outDir.get().asFile)
        args.gatherTemplatesAction.gatherTemplateStrings(
            outDir.get().asFile,
            args.variantResDirs,
            libraryResources.files,
            templateIdsFile.get().asFile
        )
    }
}
