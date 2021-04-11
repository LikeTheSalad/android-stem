package com.likethesalad.placeholder.modules.rawStrings

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class GatherRawStringsTask
@Inject constructor(private val gatherRawStringsAction: GatherRawStringsAction) : DefaultTask() {

    @InputFiles
    lateinit var dependenciesRes: FileCollection

    @InputFiles
    lateinit var gradleGeneratedStrings: FileCollection

    @TaskAction
    fun gatherStrings() = gatherRawStringsAction.gatherStrings(gradleGeneratedStrings.files)
}