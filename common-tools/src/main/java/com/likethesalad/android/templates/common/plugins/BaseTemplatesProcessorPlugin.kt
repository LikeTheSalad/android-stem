package com.likethesalad.android.templates.common.plugins

import com.likethesalad.android.string.resources.locator.StringResourceLocatorPlugin
import com.likethesalad.android.templates.common.plugins.extension.StemExtension
import com.likethesalad.tools.android.plugin.base.AndroidToolsPluginConsumer
import com.likethesalad.tools.resource.locator.android.extension.AndroidResourceLocatorExtension
import org.gradle.api.Project

abstract class BaseTemplatesProcessorPlugin : AndroidToolsPluginConsumer() {

    protected lateinit var stringsLocatorExtension: AndroidResourceLocatorExtension
    protected lateinit var extension: StemExtension

    companion object {
        private const val EXTENSION_NAME = "androidStem"
    }

    override fun apply(project: Project) {
        validateHostProjectValidForThisPlugin(project)
        super.apply(project)
        project.plugins.apply(StringResourceLocatorPlugin::class.java)
        extension = createExtension(project)
        stringsLocatorExtension = project.extensions.getByType(AndroidResourceLocatorExtension::class.java)
    }

    private fun createExtension(project: Project): StemExtension {
        return project.extensions.create(EXTENSION_NAME, StemExtension::class.java)
    }

    private fun validateHostProjectValidForThisPlugin(project: Project) {
        val validHostPluginName = getValidProjectPluginName()
        if (!project.plugins.hasPlugin(validHostPluginName)) {
            val displayName = getDisplayName()
            throw IllegalStateException("The $displayName can only be applied to projects of type '$validHostPluginName'")
        }
    }

    protected abstract fun getValidProjectPluginName(): String

    protected abstract fun getDisplayName(): String
}