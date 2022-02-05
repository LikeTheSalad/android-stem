package com.likethesalad.android.templates.provider.tasks.metainf

import com.likethesalad.android.templates.provider.tasks.metainf.action.ServiceMetaInfGeneratorAction
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@Suppress("UnstableApiUsage")
open class ServiceMetaInfGeneratorTask : DefaultTask() {

    @InputDirectory
    val generatedClasspath: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    init {
        outputDir.set(project.layout.buildDirectory.dir("intermediates/incremental/$name"))
    }

    @TaskAction
    fun execute() {
        val action = ServiceMetaInfGeneratorAction(generatedClasspath.get().asFile, outputDir.get().asFile)
        action.execute()
    }
}