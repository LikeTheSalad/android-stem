package com.likethesalad.stem.modules.common.helpers.android

import com.likethesalad.stem.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.stem.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.stem.modules.common.models.TasksNamesModel
import com.likethesalad.stem.providers.ProjectDirsProvider
import com.likethesalad.stem.providers.TaskProvider
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.gradle.api.Task

class AndroidVariantContext @AssistedInject constructor(
    @Assisted val variantTree: VariantTree,
    tasksNamesModelFactory: TasksNamesModel.Factory,
    variantBuildResolvedDirFactory: VariantBuildResolvedDir.Factory,
    resourceSerializer: ResourceSerializer,
    private val taskProvider: TaskProvider,
    private val projectDirsProvider: ProjectDirsProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(variantTree: VariantTree): AndroidVariantContext
    }

    val androidVariantData = variantTree.androidVariantData
    val tasksNames by lazy {
        tasksNamesModelFactory.create(androidVariantData)
    }
    val mergeResourcesTask: Task by lazy {
        taskProvider.findTaskByName(tasksNames.mergeResourcesName)
    }
    val incrementalDir: String by lazy {
        projectDirsProvider.getBuildDir().absolutePath + "/intermediates/incremental/" + tasksNames.resolvePlaceholdersName
    }
    val variantBuildResolvedDir by lazy { variantBuildResolvedDirFactory.create(androidVariantData) }

    private val outputStringFileResolver = OutputStringFileResolver()
    val androidResourcesHandler: ResourcesHandler = AndroidResourcesHandler(
        outputStringFileResolver,
        resourceSerializer
    )
}