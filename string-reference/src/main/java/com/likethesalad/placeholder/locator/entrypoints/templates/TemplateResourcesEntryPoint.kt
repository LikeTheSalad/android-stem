package com.likethesalad.placeholder.locator.entrypoints.templates

import com.likethesalad.placeholder.locator.entrypoints.templates.source.TemplateDirsXmlSourceFilterRule
import com.likethesalad.placeholder.locator.entrypoints.templates.source.TemplatesSourceConfiguration
import com.likethesalad.placeholder.modules.common.helpers.dirs.TemplatesDirHandler
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.ResourceLocatorEntryPoint
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import com.likethesalad.tools.resource.locator.android.extension.configuration.source.ResourceSourceConfiguration

class TemplateResourcesEntryPoint(
    private val templatesDirHandlerFactory: TemplatesDirHandler.Factory,
    private val templateDirsXmlSourceFilterRuleFactory: TemplateDirsXmlSourceFilterRule.Factory,
    private val templatesSourceConfigurationFactory: TemplatesSourceConfiguration.Factory
) : ResourceLocatorEntryPoint {

    override fun getResourceSourceConfigurations(variantTree: VariantTree): List<ResourceSourceConfiguration> {
        val templatesDirHandler = templatesDirHandlerFactory.create(variantTree)
        val templatesSourceConfiguration = templatesSourceConfigurationFactory.create(variantTree, templatesDirHandler)
        val templateDirsXmlSourceFilterRule = templateDirsXmlSourceFilterRuleFactory.create(variantTree)
        templatesSourceConfiguration.addFilterRule(templateDirsXmlSourceFilterRule)

        return listOf(templatesSourceConfiguration)
    }

    override fun onLocatorCreated(variantTree: VariantTree, info: ResourceLocatorInfo) {
        // No operation
    }
}