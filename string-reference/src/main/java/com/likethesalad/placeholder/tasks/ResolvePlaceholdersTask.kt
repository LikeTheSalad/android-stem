package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.tasks.actions.ResolvePlaceholdersAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ResolvePlaceholdersTask : DefaultTask() {

    lateinit var resolvePlaceholdersAction: ResolvePlaceholdersAction

    @InputFiles
    fun getTemplatesFiles(): List<File> = resolvePlaceholdersAction.getTemplatesFiles()

    @OutputFiles
    fun getResolvedFiles(): List<File> = resolvePlaceholdersAction.getResolvedFiles()

    @TaskAction
    fun resolve() = resolvePlaceholdersAction.resolve()
}