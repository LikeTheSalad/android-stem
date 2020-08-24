package com.likethesalad.placeholder.data.helpers

import com.likethesalad.placeholder.models.TasksNamesModel
import org.gradle.api.Task

class AndroidVariantHelper(
    projectHelper: AndroidProjectHelper,
    buildVariant: String
) {

    val tasksNames = TasksNamesModel(buildVariant)

    val mergeResourcesTask: Task by lazy {
        projectHelper.project.tasks.findByName(tasksNames.mergeResourcesName)!!
    }

    val generateResValuesTask: Task by lazy {
        projectHelper.project.tasks.findByName(tasksNames.generateResValuesName)!!
    }

    val incrementalDir: String by lazy {
        projectHelper.project.buildDir.absolutePath + "/intermediates/incremental/" + tasksNames.resolvePlaceholdersName
    }

}