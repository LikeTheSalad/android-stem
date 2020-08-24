package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.tasks.actions.GatherRawStringsAction
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

open class GatherRawStringsTask : DefaultTask() {

    lateinit var gatherRawStringsAction: GatherRawStringsAction

    @InputFiles
    lateinit var dependenciesRes: FileCollection

    @InputFiles
    lateinit var gradleGeneratedStrings: FileCollection

    @TaskAction
    fun gatherStrings() = gatherRawStringsAction.gatherStrings(gradleGeneratedStrings.files)
}