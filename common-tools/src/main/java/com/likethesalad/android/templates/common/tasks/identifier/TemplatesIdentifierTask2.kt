package com.likethesalad.android.templates.common.tasks.identifier

import com.likethesalad.android.protos.ValuesStringResources
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.BaseTask
import com.likethesalad.android.templates.common.tasks.identifier.action.TemplatesIdentifierAction2
import javax.inject.Inject
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class TemplatesIdentifierTask2 @Inject constructor(private val args: Args) : BaseTask() {

    @get:InputFile
    abstract val stringValuesProto: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.set(project.layout.buildDirectory.file("intermediates/incremental/$name/templateNames.txt"))
    }

    @TaskAction
    fun execute() {
        val stringValues = stringValuesProto.get().asFile.inputStream().use {
            ValuesStringResources.ADAPTER.decode(it)
        }

        val action = TemplatesIdentifierAction2.create(
            args.stemConfiguration,
            stringValues,
            outputFile.get().asFile
        )
        action.execute()
    }

    data class Args(
        val stemConfiguration: StemConfiguration
    )
}
