package com.likethesalad.stem

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.likethesalad.stem.configuration.StemConfiguration
import com.likethesalad.stem.modules.collector.task.RawStringCollectorTask
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersAction
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersTask
import com.likethesalad.stem.modules.resolveStrings.data.ResolvePlaceholdersArgs
import com.likethesalad.stem.modules.resolveStrings.resolver.RecursiveLevelDetector
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.stem.modules.templateStrings.GatherTemplatesAction
import com.likethesalad.stem.modules.templateStrings.GatherTemplatesTask
import com.likethesalad.stem.modules.templateStrings.data.GatherTemplatesArgs
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.file.FileCollection

class StemPlugin : Plugin<Project> {
    private lateinit var extension: StemExtension

    companion object {
        private const val EXTENSION_NAME = "androidStem"
        private val artifactTypeAttr = Attribute.of("artifactType", String::class.java)
    }

    override fun apply(project: Project) {
        extension = createExtension(project)
        val components = getAndroidComponents(project)
        val taskContainer = project.tasks
        val outputStringFileResolver = OutputStringFileResolver()
        val androidResourcesHandler = AndroidResourcesHandler(outputStringFileResolver)
        val stemConfiguration = StemConfiguration.create(extension)
        val stemProviderConfig = project.configurations.create("stemProvider") {
            it.isCanBeResolved = false
            it.isCanBeConsumed = false
        }

        components.onVariants { variant ->
            val stemProviderClasspath = createClasspath(project, stemProviderConfig, variant.name + "StemProvider")
            val runtimeAttributes = variant.runtimeConfiguration.attributes
            runtimeAttributes.keySet().forEach { key ->
                copyAttribute(
                    key,
                    runtimeAttributes,
                    stemProviderClasspath.attributes
                )
            }

            val rawStringCollectorTask =
                taskContainer.register("${variant.name}RawStringCollector", RawStringCollectorTask::class.java) {
                    it.localResDirs.set(variant.sources.res!!.static)
                    it.libraryResDirs.from(getResDirsFromConfiguration(stemProviderClasspath))
                    it.outputFile.set(project.layout.buildDirectory.file("intermediates/stem/${it.name}/values.bin"))
                }

            val gatherTemplatesTask = taskContainer.register(
                "${variant.name}GatherStringTemplates",
                GatherTemplatesTask::class.java,
                GatherTemplatesArgs(
                    GatherTemplatesAction(androidResourcesHandler, stemConfiguration)
                )
            )

            gatherTemplatesTask.configure {
                it.outDir.set(project.layout.buildDirectory.dir("intermediates/stem/${it.name}"))
                it.stringValuesProto.set(rawStringCollectorTask.flatMap { rawValues -> rawValues.outputFile })
            }

            val resolvePlaceholdersTask = taskContainer.register(
                "${variant.name}ResolvePlaceholders",
                ResolvePlaceholdersTask::class.java,
                ResolvePlaceholdersArgs(
                    ResolvePlaceholdersAction(
                        TemplateResolver(
                            stemConfiguration,
                            RecursiveLevelDetector()
                        ), androidResourcesHandler
                    )
                )
            )

            resolvePlaceholdersTask.configure {
                it.templatesDir.set(gatherTemplatesTask.flatMap { gatherTemplates -> gatherTemplates.outDir })
                it.outputDir.set(project.layout.buildDirectory.dir("generated/stem/${variant.name}"))
            }

            variant.sources.res?.addGeneratedSourceDirectory(
                resolvePlaceholdersTask,
                ResolvePlaceholdersTask::outputDir
            )
        }
    }

    private fun getAndroidComponents(project: Project): ApplicationAndroidComponentsExtension {
        val components = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        val pluginVersion = components.pluginVersion

        if (pluginVersion.major >= 8 && pluginVersion.minor >= 4) {
            return components
        }

        throw IllegalStateException("Android Stem requires a minimum Android Gradle Plugin version of 8.4.0. The current version is: $pluginVersion")
    }

    private fun <T> copyAttribute(key: Attribute<T>, from: AttributeContainer, into: AttributeContainer) {
        from.getAttribute(key)?.let {
            into.attribute(key, it)
        }
    }

    private fun createClasspath(project: Project, bucket: Configuration, name: String): Configuration {
        return project.configurations.create(name) {
            it.extendsFrom(bucket)
            it.isTransitive = false
            it.isCanBeResolved = true
            it.isCanBeConsumed = false
        }
    }

    private fun createExtension(project: Project): StemExtension {
        return project.extensions.create(EXTENSION_NAME, StemExtension::class.java)
    }

    private fun getResDirsFromConfiguration(from: Configuration): FileCollection {
        return from.incoming
            .artifactView(getResDirViewAction())
            .artifacts
            .artifactFiles
    }

    private fun getResDirViewAction(): Action<ArtifactView.ViewConfiguration> {
        return Action { config ->
            config.isLenient = false
            config.attributes {
                it.attribute(artifactTypeAttr, "android-res")
            }
        }
    }
}