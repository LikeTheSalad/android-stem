package com.likethesalad.android.templates.provider.locator

import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.ResourceLocatorEntryPoint
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.ResourceSourceConfiguration
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.utils.CommonSourceConfigurationCreator

class TemplateResourcesProviderEntryPoint(
    private val commonSourceConfigurationCreator: CommonSourceConfigurationCreator,
) : ResourceLocatorEntryPoint {

    override fun getResourceSourceConfigurations(variantTree: VariantTree): List<ResourceSourceConfiguration> {
        val rawConfiguration = commonSourceConfigurationCreator.createAndroidRawConfiguration(variantTree)

        return listOf(rawConfiguration)
    }
}