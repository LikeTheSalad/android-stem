package com.likethesalad.android.templates.provider.tasks.service.action

import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.android.templates.provider.api.TemplatesProvider
import com.likethesalad.android.templates.provider.tasks.service.action.helpers.ClassNameGenerator
import com.likethesalad.tools.plugin.metadata.api.PluginMetadata
import com.likethesalad.tools.resource.api.android.data.AndroidResourceType
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers.named
import java.io.File

class TemplatesServiceGeneratorAction @AssistedInject constructor(
    @Assisted private val projectName: String,
    @Assisted private val outputDir: File,
    @Assisted private val rawResources: ResourcesProvider,
    private val classNameGenerator: ClassNameGenerator,
    private val pluginMetadata: PluginMetadata,
    private val templateItemsSerializer: TemplateItemsSerializer
) {

    @AssistedFactory
    interface Factory {
        fun create(
            projectName: String,
            outputDir: File,
            rawResources: ResourcesProvider
        ): TemplatesServiceGeneratorAction
    }

    fun execute() {
        val className = classNameGenerator.generate(projectName)
        val classBytes = createProviderImplementation(className)

        val outputFile = createOutputFile(className)
        outputFile.writeBytes(classBytes)
    }

    private fun createOutputFile(className: String): File {
        val classFilePath = className.replace(".", "/")
        val file = File(outputDir, "$classFilePath.class")
        val classDir = file.parentFile
        if (!classDir.exists()) {
            classDir.mkdirs()
        }

        return file
    }

    private fun createProviderImplementation(className: String): ByteArray {
        val pluginVersion = pluginMetadata.version
        val templates = getTemplates()

        return ByteBuddy()
            .subclass(TemplatesProvider::class.java)
            .name(className)
            .method(named("getId")).intercept(FixedValue.value(projectName))
            .method(named("getTemplates")).intercept(FixedValue.value(templates))
            .method(named("getPluginVersion")).intercept(FixedValue.value(pluginVersion))
            .make()
            .bytes
    }

    private fun getTemplates(): String {
        val stringResources = getRawStringResources()
        val templateIds = stringResources.map {
            TemplateItem(it.name(), it.type().getName())
        }

        return templateItemsSerializer.serialize(templateIds)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRawStringResources(): List<StringAndroidResource> {
        val collection = rawResources.resources.getMergedResourcesForLanguage(Language.Default)
        val stringResources = collection.getResourcesByType(AndroidResourceType.StringType)

        return stringResources as List<StringAndroidResource>
    }
}