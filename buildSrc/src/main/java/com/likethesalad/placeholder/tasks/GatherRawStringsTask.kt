package com.likethesalad.placeholder.tasks

import com.likethesalad.placeholder.tasks.actions.GatherRawStringsAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class GatherRawStringsTask : DefaultTask() {

    lateinit var gatherRawStringsAction: GatherRawStringsAction

    @TaskAction
    fun gatherStrings() = gatherRawStringsAction.gatherStrings()
}