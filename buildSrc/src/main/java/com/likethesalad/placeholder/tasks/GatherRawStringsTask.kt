package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.tasks.actions.GatherRawStringsAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GatherRawStringsTask : DefaultTask() {

    lateinit var gatherRawStringsAction: GatherRawStringsAction

    @InputFiles
    fun getInputFiles(): List<File> = gatherRawStringsAction.getInputFiles()

    @OutputFile
    fun getOutputFile(): File = gatherRawStringsAction.getOutputFile()

    @TaskAction
    fun gatherStrings() = gatherRawStringsAction.gatherStrings()
}