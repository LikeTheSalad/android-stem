package com.likethesalad.placeholder.locator.entrypoints.templates

import com.likethesalad.placeholder.locator.entrypoints.common.source.rules.ResolvedXmlSourceFilterRule
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.ResourceLocatorEntryPoint
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.ResourceSourceConfiguration
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.utils.CommonSourceConfigurationCreator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TemplateResourcesEntryPoint @AssistedInject constructor(
    @Assisted private val commonSourceConfigurationCreator: CommonSourceConfigurationCreator,
    private val resolvedXmlSourceFilterRule: ResolvedXmlSourceFilterRule
) : ResourceLocatorEntryPoint {

    @AssistedFactory
    interface Factory {
        fun create(commonSourceConfigurationCreator: CommonSourceConfigurationCreator): TemplateResourcesEntryPoint
    }

    override fun getResourceSourceConfigurations(variantTree: VariantTree): List<ResourceSourceConfiguration> {
        val rawConfiguration = commonSourceConfigurationCreator.createAndroidRawConfiguration(variantTree)
        rawConfiguration.addFilterRule(resolvedXmlSourceFilterRule)

        return listOf(rawConfiguration)
    }
}