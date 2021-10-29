package com.likethesalad.placeholder.modules.common.helpers.android

import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinder
import com.likethesalad.placeholder.modules.common.helpers.files.IncrementalDataCleaner
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.placeholder.modules.common.helpers.files.storage.AndroidFilesProvider
import com.likethesalad.placeholder.modules.common.helpers.files.storage.FilesProvider
import com.likethesalad.placeholder.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.modules.common.helpers.resources.ResourcesHandler
import com.likethesalad.placeholder.modules.common.models.TasksNamesModel
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.gradle.api.Task
import java.io.File

class AndroidVariantContext @AssistedInject constructor(
    @Assisted val androidVariantData: AndroidVariantData,
    @Assisted val resourceSerializer: ResourceSerializer,
    androidConfigHelperFactory: AndroidConfigHelper.Factory,
    tasksNamesModelFactory: TasksNamesModel.Factory,
    variantBuildResolvedDirFactory: VariantBuildResolvedDir.Factory,
    variantDirsPathFinderFactory: VariantDirsPathFinder.Factory,
    private val taskProvider: TaskProvider,
    private val buildDirProvider: BuildDirProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            androidVariantData: AndroidVariantData,
            resourceSerializer: ResourceSerializer
        ): AndroidVariantContext
    }

    val tasksNames by lazy {
        tasksNamesModelFactory.create(androidVariantData)
    }
    val androidConfigHelper by lazy {
        androidConfigHelperFactory.create(androidVariantData)
    }
    val incrementalDirsProvider by lazy {
        IncrementalDirsProvider(File(incrementalDir))
    }
    val mergeResourcesTask: Task by lazy {
        taskProvider.findTaskByName(tasksNames.mergeResourcesName)
    }
    val generateResValuesTask: Task by lazy {
        taskProvider.findTaskByName(tasksNames.generateResValuesName)
    }
    val incrementalDir: String by lazy {
        buildDirProvider.getBuildDir().absolutePath + "/intermediates/incremental/" + tasksNames.resolvePlaceholdersName
    }
    val variantDirsPathFinder by lazy { variantDirsPathFinderFactory.create(androidVariantData) }

    private val variantBuildResolvedDir by lazy { variantBuildResolvedDirFactory.create(androidVariantData) }
    private val outputStringFileResolver = OutputStringFileResolver(
        variantBuildResolvedDir,
        incrementalDirsProvider
    )
    val filesProvider: FilesProvider = AndroidFilesProvider(
        outputStringFileResolver,
        incrementalDirsProvider
    )
    val androidResourcesHandler: ResourcesHandler = AndroidResourcesHandler(
        outputStringFileResolver,
        resourceSerializer
    )
    val incrementalDataCleaner = IncrementalDataCleaner(
        incrementalDirsProvider
    )
}