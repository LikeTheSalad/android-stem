package com.likethesalad.placeholder.modules.common.helpers.android

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDirFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinderFactory
import com.likethesalad.placeholder.modules.common.helpers.files.IncrementalDataCleaner
import com.likethesalad.placeholder.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.placeholder.modules.common.helpers.files.storage.AndroidFilesProvider
import com.likethesalad.placeholder.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.modules.common.models.TasksNamesModelFactory
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.TaskProvider
import org.gradle.api.Task
import java.io.File

@AutoFactory
class AndroidVariantContext(
    val appVariantHelper: AppVariantHelper,
    @Provided private val taskProvider: TaskProvider,
    @Provided private val androidConfigHelperFactory: AndroidConfigHelperFactory,
    @Provided private val tasksNamesModelFactory: TasksNamesModelFactory,
    @Provided private val variantBuildResolvedDirFactory: VariantBuildResolvedDirFactory,
    @Provided private val variantDirsPathFinderFactory: VariantDirsPathFinderFactory,
    @Provided private val buildDirProvider: BuildDirProvider
) {
    val tasksNames by lazy {
        tasksNamesModelFactory.create(appVariantHelper)
    }
    val androidConfigHelper by lazy {
        androidConfigHelperFactory.create(appVariantHelper)
    }
    val incrementalDirsProvider by lazy {
        IncrementalDirsProvider(File(incrementalDir))
    }
    val mergeResourcesTask: Task by lazy {
        taskProvider.findTaskByName<Task>(tasksNames.mergeResourcesName)
    }
    val generateResValuesTask: Task by lazy {
        taskProvider.findTaskByName<Task>(tasksNames.generateResValuesName)
    }
    val incrementalDir: String by lazy {
        buildDirProvider.getBuildDir().absolutePath + "/intermediates/incremental/" + tasksNames.resolvePlaceholdersName
    }
    val variantDirsPathFinder by lazy { variantDirsPathFinderFactory.create(appVariantHelper) }

    private val variantBuildResolvedDir by lazy { variantBuildResolvedDirFactory.create(appVariantHelper) }
    private val outputStringFileResolver =
        OutputStringFileResolver(
            variantBuildResolvedDir,
            incrementalDirsProvider
        )
    val filesProvider =
        AndroidFilesProvider(
            outputStringFileResolver,
            incrementalDirsProvider
        )
    val androidResourcesHandler =
        AndroidResourcesHandler(
            outputStringFileResolver
        )
    val incrementalDataCleaner =
        IncrementalDataCleaner(
            incrementalDirsProvider
        )
}