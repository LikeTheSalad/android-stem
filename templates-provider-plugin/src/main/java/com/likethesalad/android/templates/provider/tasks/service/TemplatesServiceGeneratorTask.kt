package com.likethesalad.android.templates.provider.tasks.service

import com.likethesalad.android.templates.common.utils.CommonConstants
import com.likethesalad.android.templates.common.utils.DirectoryUtils
import com.likethesalad.android.templates.provider.tasks.service.action.TemplatesServiceGeneratorAction
import com.likethesalad.tools.agpcompat.api.tasks.DirProducerTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class TemplatesServiceGeneratorTask @Inject constructor(private val args: Args) : DirProducerTask() {

    @InputFile
    val templateIdsFile: RegularFileProperty = project.objects.fileProperty()

    init {
        group = CommonConstants.RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
        outputDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun execute() {
        val outputDir = outputDir.get().asFile
        DirectoryUtils.clearIfNeeded(outputDir)
        val action = args.actionFactory.create(project.name, outputDir, templateIdsFile.get().asFile)
        action.execute()
    }

    data class Args(
        val actionFactory: TemplatesServiceGeneratorAction.Factory
    )
}