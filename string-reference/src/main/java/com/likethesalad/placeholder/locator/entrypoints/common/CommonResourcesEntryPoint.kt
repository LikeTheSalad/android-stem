package com.likethesalad.placeholder.locator.entrypoints.common

import com.likethesalad.placeholder.locator.entrypoints.common.source.configuration.TemplateProvidersResourceSourceConfiguration
import com.likethesalad.placeholder.locator.entrypoints.common.source.rules.ResolvedXmlSourceFilterRule
import com.likethesalad.placeholder.locator.entrypoints.common.utils.TemplatesProviderJarsFinder
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
    private val resolvedXmlSourceFilterRule: ResolvedXmlSourceFilterRule,
    private val templateProvidersResourceSourceConfigurationFactory: TemplateProvidersResourceSourceConfiguration.Factory
) : ResourceLocatorEntryPoint {

    @AssistedFactory
    interface Factory {
        fun create(commonSourceConfigurationCreator: CommonSourceConfigurationCreator): CommonResourcesEntryPoint
    }

    override fun getResourceSourceConfigurations(variantTree: VariantTree): List<ResourceSourceConfiguration> {
        val rawConfiguration = commonSourceConfigurationCreator.createAndroidRawConfiguration(variantTree)
        val templatesProviderJarsFinder = TemplatesProviderJarsFinder(variantTree.androidVariantData.getLibrariesJars())
        addExclusionRules(rawConfiguration)
        return listOf(
            rawConfiguration,
            commonSourceConfigurationCreator.createAndroidGeneratedResConfiguration(variantTree),
            templateProvidersResourceSourceConfigurationFactory.create(variantTree, templatesProviderJarsFinder)
        )
    }

    override fun onLocatorCreated(variantTree: VariantTree, info: ResourceLocatorInfo) {
        // No operation
    }

    private fun addExclusionRules(configuration: ResourceSourceConfiguration) {
        configuration.addFilterRule(resolvedXmlSourceFilterRule)
    }
}