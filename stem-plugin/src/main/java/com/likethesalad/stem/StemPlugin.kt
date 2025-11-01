package com.likethesalad.stem

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.likethesalad.stem.configuration.StemConfiguration
import com.likethesalad.stem.modules.collector.task.RawStringCollectorTask
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersAction2
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersTask2
import com.likethesalad.stem.modules.resolveStrings.data.ResolvePlaceholdersArgs2
import com.likethesalad.stem.modules.resolveStrings.resolver.RecursiveLevelDetector
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.stem.modules.templateStrings.GatherTemplatesAction2
import com.likethesalad.stem.modules.templateStrings.GatherTemplatesTask2
import com.likethesalad.stem.modules.templateStrings.data.GatherTemplatesArgs2
import java.io.File
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.file.FileCollection

class StemPlugin : Plugin<Project> {
    private lateinit var androidExtension: ApplicationExtension
    private lateinit var extension: StemExtension

    companion object {
        private const val EXTENSION_NAME = "androidStem"
        private val RESOLVED_DIR_BUILD_RELATIVE_PATH = "generated${File.separator}resolved"

        private val artifactTypeAttr = Attribute.of("artifactType", String::class.java)

        fun getBuildRelativeResolvedDir(variantName: String): String {
            return "$RESOLVED_DIR_BUILD_RELATIVE_PATH${File.separator}$variantName"
        }
    }

    override fun apply(project: Project) {
        androidExtension = project.extensions.getByType(ApplicationExtension::class.java)
        extension = createExtension(project)
        val components = getAndroidComponents(project)
        val taskContainer = project.tasks
        val outputStringFileResolver = OutputStringFileResolver()
        val androidResourcesHandler = AndroidResourcesHandler(outputStringFileResolver)
        val stemConfiguration = StemConfiguration.create(extension)
        val stemProviderConfig = createBucket(project, "stemProvider")

        components.onVariants { variant ->
            val resolvedDir = project.layout.buildDirectory.dir(getBuildRelativeResolvedDir(variant.name))
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
                    it.libraryResDirs.from(getFilesFromConfiguration(stemProviderClasspath, "android-res"))
                    it.outputFile.set(project.layout.buildDirectory.file("intermediates/${it.name}/values.bin"))
                }

            val gatherTemplatesTask = taskContainer.register(
                "gather${variant.name.capitalize()}StringTemplates",
                GatherTemplatesTask2::class.java,
                GatherTemplatesArgs2(
                    GatherTemplatesAction2(androidResourcesHandler, stemConfiguration)
                )
            )

            gatherTemplatesTask.configure {
                it.stringValuesProto.set(rawStringCollectorTask.flatMap { rawValues -> rawValues.outputFile })
            }

            val resolvePlaceholdersTask = taskContainer.register(
                "resolve${variant.name.capitalize()}Placeholders",
                ResolvePlaceholdersTask2::class.java,
                ResolvePlaceholdersArgs2(
                    ResolvePlaceholdersAction2(
                        TemplateResolver(
                            stemConfiguration,
                            RecursiveLevelDetector()
                        ), androidResourcesHandler
                    )
                )
            )

            resolvePlaceholdersTask.configure {
                it.templatesDir.set(gatherTemplatesTask.flatMap { gatherTemplates -> gatherTemplates.outDir })
                it.outputDir.set(resolvedDir)
            }

            variant.sources.res?.addGeneratedSourceDirectory(
                resolvePlaceholdersTask,
                ResolvePlaceholdersTask2::outputDir
            )
        }
    }

    private fun getAndroidComponents(project: Project): ApplicationAndroidComponentsExtension {
        val components = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        if (components.pluginVersion.major < 8) {
            throw IllegalStateException("Android Stem requires a minimum AGP version of 8.0.0, the current version is: " + components.pluginVersion.version)
        }
        return components
    }

    private fun <T> copyAttribute(key: Attribute<T>, from: AttributeContainer, into: AttributeContainer) {
        from.getAttribute(key)?.let {
            into.attribute(key, it)
        }
    }

    private fun createBucket(project: Project, name: String): Configuration {
        return project.configurations.create(name) {
            it.isCanBeResolved = false
            it.isCanBeConsumed = false
        }
    }

    private fun createClasspath(project: Project, bucket: Configuration, name: String): Configuration {
        return project.configurations.create(name) {
            it.extendsFrom(bucket)
            it.isCanBeResolved = true
            it.isCanBeConsumed = false
        }
    }

    private fun createExtension(project: Project): StemExtension {
        return project.extensions.create(EXTENSION_NAME, StemExtension::class.java)
    }

    private fun getFilesFromConfiguration(from: Configuration, artifactType: String): FileCollection {
        return from.incoming
            .artifactView(getAndroidArtifactViewAction(artifactType))
            .artifacts
            .artifactFiles
    }

    private fun getAndroidArtifactViewAction(artifactType: String): Action<ArtifactView.ViewConfiguration> {
        return Action { config ->
            config.isLenient = false
            config.attributes {
                it.attribute(artifactTypeAttr, artifactType)
            }
        }
    }
}