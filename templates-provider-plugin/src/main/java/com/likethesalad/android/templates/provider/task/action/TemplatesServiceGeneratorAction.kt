package com.likethesalad.android.templates.provider.task.action

import com.likethesalad.android.templates.provider.api.TemplatesProvider
import com.likethesalad.android.templates.provider.task.action.helpers.ClassNameGenerator
import com.likethesalad.tools.plugin.metadata.api.PluginMetadata
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
    private val classNameGenerator: ClassNameGenerator,
    private val pluginMetadata: PluginMetadata
) {

    @AssistedFactory
    interface Factory {
        fun create(
            projectName: String,
            outputDir: File
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
        return ""
    }
}