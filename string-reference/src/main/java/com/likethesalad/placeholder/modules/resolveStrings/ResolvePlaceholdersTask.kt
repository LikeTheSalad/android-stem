package com.likethesalad.placeholder.modules.resolveStrings

import com.likethesalad.placeholder.modules.resolveStrings.data.ResolvePlaceholdersArgs
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class ResolvePlaceholdersTask
@Inject constructor(private val args: ResolvePlaceholdersArgs) : DefaultTask() {

    @InputFiles
    fun getTemplatesFiles(): List<File> = args.resolvePlaceholdersAction.getTemplatesFiles()

    @OutputFiles
    fun getResolvedFiles(): List<File> = args.resolvePlaceholdersAction.getResolvedFiles()

    @TaskAction
    fun resolve() = args.resolvePlaceholdersAction.resolve()
}