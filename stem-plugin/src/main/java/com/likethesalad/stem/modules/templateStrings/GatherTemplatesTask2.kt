package com.likethesalad.stem.modules.templateStrings

import com.likethesalad.android.protos.ValuesStringResources
import com.likethesalad.android.templates.common.tasks.BaseTask
import com.likethesalad.android.templates.common.utils.DirectoryUtils
import com.likethesalad.stem.modules.templateStrings.data.GatherTemplatesArgs2
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GatherTemplatesTask2
@Inject constructor(private val args: GatherTemplatesArgs2) : BaseTask() {
    @get:InputFile
    abstract val stringValuesProto: RegularFileProperty

    @get:OutputDirectory
    abstract val outDir: DirectoryProperty

    init {
        outDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun gatherTemplateStrings() {
        DirectoryUtils.clearIfNeeded(outDir.get().asFile)

        val stringValues = stringValuesProto.get().asFile.inputStream().use {
            ValuesStringResources.ADAPTER.decode(it)
        }

        args.gatherTemplatesAction.gatherTemplateStrings(
            outDir.get().asFile,
            stringValues
        )
    }
}
