package com.likethesalad.placeholder.modules.resolveStrings

import com.likethesalad.placeholder.modules.resolveStrings.data.ResolvePlaceholdersArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class ResolvePlaceholdersTask
@Inject constructor(private val args: ResolvePlaceholdersArgs) : DefaultTask() {

    @InputDirectory
    val templatesDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputFiles
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun resolve() {
        args.resolvePlaceholdersAction.resolve(templatesDir.get().asFile, outputDir.get().asFile)
    }
}