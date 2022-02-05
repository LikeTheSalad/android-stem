package com.likethesalad.android.templates.provider.task

import com.likethesalad.android.templates.provider.task.action.TemplatesServiceGeneratorAction
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UnstableApiUsage")
open class TemplatesServiceGeneratorTask @Inject constructor(private val args: Args) : DefaultTask() {

    @OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun execute() {
        val action = args.actionFactory.create(project.name, outputDir.get().asFile)
        action.execute()
    }

    @Singleton
    data class Args @Inject constructor(val actionFactory: TemplatesServiceGeneratorAction.Factory)
}