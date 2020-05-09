package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.data.OutputStringFileResolver
import com.likethesalad.placeholder.data.VariantDirsPathFinder
import com.likethesalad.placeholder.data.VariantDirsPathResolver
import com.likethesalad.placeholder.data.VariantRawStrings
import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.storage.*
import com.likethesalad.placeholder.data.storage.libraries.LibrariesFilesProvider
import com.likethesalad.placeholder.data.storage.libraries.LibrariesValuesStringsProvider
import com.likethesalad.placeholder.data.storage.utils.ValuesStringsProvider
import com.likethesalad.placeholder.resolver.TemplateResolver
import com.likethesalad.placeholder.tasks.actions.GatherRawStringsAction
import com.likethesalad.placeholder.tasks.actions.GatherTemplatesAction
import com.likethesalad.placeholder.tasks.actions.ResolvePlaceholdersAction
import org.gradle.api.artifacts.Configuration
import java.io.File

class TaskActionProvider(
    buildDir: File,
    configuration: Configuration,
    variantName: String,
    variantType: String,
    flavors: List<String>,
    androidProjectHelper: AndroidProjectHelper,
    valuesStringsProvider: ValuesStringsProvider,
    templateResolver: TemplateResolver,
    keepResolvedFiles: Boolean,
    useDependenciesRes: Boolean
) {
    val androidVariantHelper = AndroidVariantHelper(androidProjectHelper, variantName)

    val androidConfigHelper = AndroidConfigHelper(configuration)
    private val librariesFilesProvider = LibrariesFilesProvider(androidConfigHelper, useDependenciesRes)
    private val librariesValuesStringsProvider = LibrariesValuesStringsProvider(librariesFilesProvider)

    private val incrementalDirsProvider = IncrementalDirsProvider(androidVariantHelper)
    private val variantDirsPathResolver = VariantDirsPathResolver(variantName, flavors, variantType)
    private val variantDirsPathFinder = VariantDirsPathFinder(variantDirsPathResolver, androidProjectHelper)
    private val variantBuildResolvedDir = VariantBuildResolvedDir(
        variantName,
        buildDir,
        androidProjectHelper.androidExtension,
        keepResolvedFiles
    )
    private val outputStringFileResolver = OutputStringFileResolver(
        variantBuildResolvedDir,
        incrementalDirsProvider
    )
    private val filesProvider = AndroidFilesProvider(outputStringFileResolver, incrementalDirsProvider)
    private val androidResourcesHandler = AndroidResourcesHandler(outputStringFileResolver)
    private val incrementalDataCleaner = IncrementalDataCleaner(incrementalDirsProvider)

    val gatherRawStringsAction: GatherRawStringsAction by lazy {
        val variantRawStrings = VariantRawStrings(
            variantDirsPathFinder, valuesStringsProvider,
            librariesValuesStringsProvider
        )
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
            ResolvedDataCleaner(variantName, variantDirsPathFinder)
        )
    }
}