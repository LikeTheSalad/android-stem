package com.likethesalad.android.templates.provider

import com.likethesalad.android.templates.common.plugins.BaseTemplatesProcessorPlugin
import com.likethesalad.android.templates.provider.di.TemplatesProviderComponent
import com.likethesalad.android.templates.provider.di.TemplatesProviderInjector
import com.likethesalad.android.templates.provider.locator.TemplateResourcesProviderEntryPoint
import com.likethesalad.android.templates.provider.locator.listener.TemplatesProviderTaskCreator
import org.gradle.api.Project

class TemplatesProviderPlugin : BaseTemplatesProcessorPlugin() {

    private lateinit var component: TemplatesProviderComponent

    override fun apply(project: Project) {
        super.apply(project)
        TemplatesProviderInjector.init(this)
        component = TemplatesProviderInjector.getComponent()

        stringsLocatorExtension.registerLocator(
            "template",
            TemplateResourcesProviderEntryPoint(stringsLocatorExtension.getCommonSourceConfigurationCreator()),
            TemplatesProviderTaskCreator(
                project,
                component.templatesServiceGeneratorActionFactory(),
                component.templatesIdentifierActionFactory()
            )
        )
    }

    override fun getValidProjectPluginName(): String = "com.android.library"

    override fun getDisplayName(): String = "Templates provider"
}