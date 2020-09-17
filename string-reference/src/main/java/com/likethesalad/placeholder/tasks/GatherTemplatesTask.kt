package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.tasks.actions.GatherTemplatesAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GatherTemplatesTask : DefaultTask() {

    lateinit var gatherTemplatesAction: GatherTemplatesAction

    @InputFiles
    fun getStringFiles(): List<File> = gatherTemplatesAction.getStringFiles()

    @OutputFiles
    fun getTemplatesFiles(): List<File> = gatherTemplatesAction.getTemplatesFiles()

    @TaskAction
    fun gatherTemplateStrings() = gatherTemplatesAction.gatherTemplateStrings()
}