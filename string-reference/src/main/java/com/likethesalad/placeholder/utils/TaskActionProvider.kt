package com.likethesalad.placeholder.utils

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidConfigHelperFactory
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContextFactory
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDirFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinderFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathResolverFactory
import com.likethesalad.placeholder.modules.rawStrings.GatherRawStringsAction
import com.likethesalad.placeholder.modules.rawStrings.data.VariantRawStringsFactory
import com.likethesalad.placeholder.modules.rawStrings.data.libraries.LibrariesFilesProviderFactory
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.placeholder.modules.resolveStrings.data.helpers.files.ResolvedDataCleanerFactory
import com.likethesalad.placeholder.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesAction

@AutoFactory
class TaskActionProvider constructor(
    private val appVariantHelper: AppVariantHelper,
    @Provided private val templateResolver: TemplateResolver,
    @Provided private val androidVariantContextFactory: AndroidVariantContextFactory,
    @Provided private val androidConfigHelperFactory: AndroidConfigHelperFactory,
    @Provided private val librariesFilesProviderFactory: LibrariesFilesProviderFactory,
    @Provided private val variantDirsPathResolverFactory: VariantDirsPathResolverFactory,
    @Provided private val variantDirsPathFinderFactory: VariantDirsPathFinderFactory,
    @Provided private val variantBuildResolvedDirFactory: VariantBuildResolvedDirFactory,
    @Provided private val variantRawStringsFactory: VariantRawStringsFactory,
    @Provided private val resolvedDataCleanerFactory: ResolvedDataCleanerFactory
) {

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