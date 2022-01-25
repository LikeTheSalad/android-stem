package com.likethesalad.placeholder.locator.entrypoints.common

import com.likethesalad.placeholder.locator.entrypoints.common.source.ResolvedXmlSourceFilterRule
import com.likethesalad.placeholder.locator.entrypoints.templates.source.TemplateDirsXmlSourceFilterRule
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.ResourceLocatorEntryPoint
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.ResourceSourceConfiguration
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.utils.CommonSourceConfigurationCreator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CommonResourcesEntryPoint @AssistedInject constructor(
    @Assisted private val commonSourceConfigurationCreator: CommonSourceConfigurationCreator,
    private val templateDirsXmlSourceFilterRuleFactory: TemplateDirsXmlSourceFilterRule.Factory,
    private val projectDirsProvider: ProjectDirsProvider
) : ResourceLocatorEntryPoint {

    @AssistedFactory
    interface Factory {
        fun create(commonSourceConfigurationCreator: CommonSourceConfigurationCreator): CommonResourcesEntryPoint
    }

    override fun getResourceSourceConfigurations(variantTree: VariantTree): List<ResourceSourceConfiguration> {
        val rawConfiguration = commonSourceConfigurationCreator.createAndroidRawConfiguration(variantTree)
        addExclusionRules(variantTree, rawConfiguration)
        return listOf(
            rawConfiguration,
            commonSourceConfigurationCreator.createAndroidGeneratedResConfiguration(variantTree),
            commonSourceConfigurationCreator.createAndroidAndroidLibrariesConfiguration(variantTree)
        )
    }

    override fun onLocatorCreated(variantTree: VariantTree, info: ResourceLocatorInfo) {
        // No operation
    }

    private fun addExclusionRules(variantTree: VariantTree, configuration: ResourceSourceConfiguration) {
        val templatesExclusionRule = templateDirsXmlSourceFilterRuleFactory.create(variantTree)
        val resolvedStringsExclusionRule =
            ResolvedXmlSourceFilterRule("${projectDirsProvider.getBuildDir()}/${VariantBuildResolvedDir.RESOLVED_DIR_BUILD_RELATIVE_PATH}")

        configuration.addFilterRule(resolvedStringsExclusionRule)
        configuration.addFilterRule(templatesExclusionRule)
    }
}