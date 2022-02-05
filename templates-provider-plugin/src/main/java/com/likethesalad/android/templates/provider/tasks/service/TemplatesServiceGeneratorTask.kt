package com.likethesalad.android.templates.provider.tasks.service

import com.likethesalad.android.templates.provider.tasks.service.action.TemplatesServiceGeneratorAction
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

    init {
        outputDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun execute() {
        val action = args.actionFactory.create(project.name, outputDir.get().asFile)
        action.execute()
    }

    @Singleton
    data class Args @Inject constructor(val actionFactory: TemplatesServiceGeneratorAction.Factory)
}