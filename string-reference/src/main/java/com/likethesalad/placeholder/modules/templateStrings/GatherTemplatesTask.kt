package com.likethesalad.placeholder.modules.templateStrings

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class GatherTemplatesTask
@Inject constructor(private val gatherTemplatesAction: GatherTemplatesAction) : DefaultTask() {

    @InputFiles
    fun getStringFiles(): List<File> = gatherTemplatesAction.getStringFiles()

    @OutputFiles
    fun getTemplatesFiles(): List<File> = gatherTemplatesAction.getTemplatesFiles()

    @TaskAction
    fun gatherTemplateStrings() = gatherTemplatesAction.gatherTemplateStrings()
}