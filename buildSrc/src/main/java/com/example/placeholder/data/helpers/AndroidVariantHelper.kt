package com.example.placeholder.data.helpers

import com.example.placeholder.models.ResDirs
import com.example.placeholder.models.TasksNamesModel
import org.gradle.api.Task

class AndroidVariantHelper(
    val projectHelper: AndroidProjectHelper,
    val buildVariant: String,
    val flavor: String
) {
    companion object {
        const val MAIN_FLAVOR_NAME = "main"
    }

    val isFlavored = flavor.isNotEmpty()
    val tasksNames = TasksNamesModel(buildVariant)

    val generateResValuesTask: Task by lazy {
        projectHelper.project.tasks.findByName(tasksNames.generateResValuesName)!!
    }

    val resourceDirs: ResDirs by lazy {
        val sourceSets = projectHelper.androidExtension.getSourceSets()
        val mainDirs = sourceSets.getValue(MAIN_FLAVOR_NAME).getRes().getSrcDirs()
        if (isFlavored)
            ResDirs(mainDirs, sourceSets.getValue(flavor).getRes().getSrcDirs())
        else
            ResDirs(mainDirs, setOf())
    }

    val incrementalDir: String by lazy {
        projectHelper.project.buildDir.absolutePath + "/intermediates/incremental/" + tasksNames.resolvePlaceholdersName
    }

}