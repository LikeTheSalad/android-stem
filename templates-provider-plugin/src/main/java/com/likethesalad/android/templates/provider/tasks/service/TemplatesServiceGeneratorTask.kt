package com.likethesalad.android.templates.provider.tasks.service

import com.likethesalad.android.templates.provider.tasks.service.action.TemplatesServiceGeneratorAction
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class TemplatesServiceGeneratorTask @Inject constructor(private val args: Args) : DefaultTask() {

    @InputDirectory
    val rawResourcesDir: DirectoryProperty = project.objects.directoryProperty()

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

    data class Args(
        val actionFactory: TemplatesServiceGeneratorAction.Factory,
        val rawResources: ResourcesProvider
    )
}