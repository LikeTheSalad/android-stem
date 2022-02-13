package com.likethesalad.placeholder.modules.resolveStrings

import com.likethesalad.android.templates.common.utils.DirectoryUtils
import com.likethesalad.placeholder.modules.resolveStrings.data.ResolvePlaceholdersArgs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class ResolvePlaceholdersTask
@Inject constructor(private val args: ResolvePlaceholdersArgs) : DefaultTask() {

    @SkipWhenEmpty
    @InputDirectory
    val templatesDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun resolve() {
        DirectoryUtils.clearIfNeeded(outputDir.get().asFile)
        args.resolvePlaceholdersAction.resolve(templatesDir.get().asFile, outputDir.get().asFile)
    }
}