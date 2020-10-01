package com.likethesalad.placeholder.utils

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.data.OutputStringFileResolver
import com.likethesalad.placeholder.data.VariantDirsPathFinderFactory
import com.likethesalad.placeholder.data.VariantDirsPathResolverFactory
import com.likethesalad.placeholder.data.VariantRawStringsFactory
import com.likethesalad.placeholder.data.helpers.AndroidConfigHelperFactory
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelperFactory
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.storage.*
import com.likethesalad.placeholder.data.storage.libraries.LibrariesFilesProviderFactory
import com.likethesalad.placeholder.data.storage.libraries.LibrariesValuesStringsProvider
import com.likethesalad.placeholder.models.TasksNamesModelFactory
import com.likethesalad.placeholder.resolver.TemplateResolver
import com.likethesalad.placeholder.tasks.actions.GatherRawStringsAction
import com.likethesalad.placeholder.tasks.actions.GatherTemplatesAction
import com.likethesalad.placeholder.tasks.actions.ResolvePlaceholdersAction

@AutoFactory
class TaskActionProvider constructor(
    private val appVariantHelper: AppVariantHelper,
    @Provided private val templateResolver: TemplateResolver,
    @Provided private val tasksNamesModelFactory: TasksNamesModelFactory,
    @Provided private val androidVariantHelperFactory: AndroidVariantHelperFactory,
    @Provided private val androidConfigHelperFactory: AndroidConfigHelperFactory,
    @Provided private val librariesFilesProviderFactory: LibrariesFilesProviderFactory,
    @Provided private val variantDirsPathResolverFactory: VariantDirsPathResolverFactory,
    @Provided private val variantDirsPathFinderFactory: VariantDirsPathFinderFactory,
    @Provided private val variantBuildResolvedDirFactory: VariantBuildResolvedDirFactory,
    @Provided private val variantRawStringsFactory: VariantRawStringsFactory,
    @Provided private val resolvedDataCleanerFactory: ResolvedDataCleanerFactory
) {
    private val tasksNamesModel by lazy { tasksNamesModelFactory.create(appVariantHelper) }

    val androidVariantHelper by lazy { androidVariantHelperFactory.create(tasksNamesModel) }

    val androidConfigHelper by lazy { androidConfigHelperFactory.create(appVariantHelper) }

    private val librariesFilesProvider by lazy { librariesFilesProviderFactory.create(androidConfigHelper) }
    private val librariesValuesStringsProvider = LibrariesValuesStringsProvider(librariesFilesProvider)

    private val incrementalDirsProvider = IncrementalDirsProvider(androidVariantHelper)

    private val variantDirsPathResolver by lazy { variantDirsPathResolverFactory.create(appVariantHelper) }
    private val variantDirsPathFinder by lazy { variantDirsPathFinderFactory.create(variantDirsPathResolver) }
    private val variantBuildResolvedDir by lazy { variantBuildResolvedDirFactory.create(appVariantHelper) }
    private val outputStringFileResolver = OutputStringFileResolver(
        variantBuildResolvedDir,
        incrementalDirsProvider
    )
    private val filesProvider = AndroidFilesProvider(outputStringFileResolver, incrementalDirsProvider)
    private val androidResourcesHandler = AndroidResourcesHandler(outputStringFileResolver)
    private val incrementalDataCleaner = IncrementalDataCleaner(incrementalDirsProvider)

    val gatherRawStringsAction: GatherRawStringsAction by lazy {
        val variantRawStrings = variantRawStringsFactory.create(variantDirsPathFinder, librariesValuesStringsProvider)
        GatherRawStringsAction(
            variantRawStrings, androidResourcesHandler,
            incrementalDataCleaner
        )
    }

    val gatherTemplatesAction: GatherTemplatesAction by lazy {
        GatherTemplatesAction(
            filesProvider, androidResourcesHandler,
            incrementalDataCleaner
        )
    }

    val resolvePlaceholdersAction: ResolvePlaceholdersAction by lazy {
        ResolvePlaceholdersAction(
            filesProvider,
            androidResourcesHandler,
            templateResolver,
            resolvedDataCleanerFactory.create(appVariantHelper, variantDirsPathFinder)
        )
    }
}