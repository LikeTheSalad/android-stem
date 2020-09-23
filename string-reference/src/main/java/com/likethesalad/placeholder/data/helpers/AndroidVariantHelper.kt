package com.likethesalad.placeholder.data.helpers

import com.likethesalad.placeholder.models.TasksNamesModel
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.utils.AutoFactory
import com.likethesalad.placeholder.utils.Provided
import org.gradle.api.Task

@AutoFactory
class AndroidVariantHelper(
    private val tasksNames: TasksNamesModel,
    @Provided private val taskProvider: TaskProvider,
    @Provided private val buildDirProvider: BuildDirProvider
) {

    val mergeResourcesTask: Task by lazy {
        taskProvider.findTaskByName<Task>(tasksNames.mergeResourcesName)
    }

    val generateResValuesTask: Task by lazy {
        taskProvider.findTaskByName<Task>(tasksNames.generateResValuesName)
    }

    val incrementalDir: String by lazy {
        buildDirProvider.getBuildDir().absolutePath + "/intermediates/incremental/" + tasksNames.resolvePlaceholdersName
    }

}