package com.likethesalad.stem

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.plugins.extension.StemExtension
import com.likethesalad.android.templates.common.tasks.identifier.TemplatesIdentifierTask2
import com.likethesalad.stem.modules.collector.VariantRes
import com.likethesalad.stem.modules.common.helpers.dirs.VariantBuildResolvedDir.Companion.getBuildRelativeResolvedDir
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
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection

class StemPlugin : Plugin<Project> {
    private lateinit var androidExtension: ApplicationExtension
    private lateinit var extension: StemExtension

    companion object {
        private const val EXTENSION_NAME = "androidStem"

        private val artifactTypeAttr = Attribute.of("artifactType", String::class.java)
    }

    override fun apply(project: Project) {
        androidExtension = project.extensions.getByType(ApplicationExtension::class.java)
        extension = createExtension(project)
        val components = project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        val taskContainer = project.tasks
        val outputStringFileResolver = OutputStringFileResolver()
        val androidResourcesHandler = AndroidResourcesHandler(outputStringFileResolver)
        val stemConfiguration = StemConfiguration.create(extension)
        val variantRes = VariantRes(androidExtension)

        components.onVariants { variant ->
            val variantResDirs = variantRes.getResDirs(variant)
            val resolvedDir = project.layout.buildDirectory.dir(getBuildRelativeResolvedDir(variant.name))

            val templatesProvider = taskContainer.register(
                "templates${variant.name.capitalize()}Identifier",
                TemplatesIdentifierTask2::class.java,
                TemplatesIdentifierTask2.Args(
                    variantResDirs,
                    stemConfiguration
                ) {
                    // Ignore res dir if it's the resolved one
                    it.startsWith(resolvedDir.get().asFile.path)
                }
            )

            val gatherTemplatesTask = taskContainer.register(
                "gather${variant.name.capitalize()}StringTemplates",
                GatherTemplatesTask2::class.java,
                GatherTemplatesArgs2(
                    GatherTemplatesAction2(androidResourcesHandler, stemConfiguration),
                    variantResDirs
                )
            )

            gatherTemplatesTask.configure {
                it.libraryResources.from(getFilesFromConfiguration(variant, "android-res"))
                it.templateIdsFile.set(templatesProvider.flatMap { identifierTask -> identifierTask.outputFile })
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

            // Todo check for alternatives
            project.afterEvaluate {
                project.tasks.named("merge${variant.name.capitalize()}Resources").configure {
                    it.dependsOn(resolvePlaceholdersTask)
                }
            }

            // Add new res dirs
            addResolvedResDir(variant, project)
        }
    }

    private fun addResolvedResDir(variant: ApplicationVariant, project: Project) {
        addVariantSrcDir(
            variant.name,
            project.layout.buildDirectory.dir(getBuildRelativeResolvedDir(variant.name))
        )
    }

    private fun addVariantSrcDir(variantName: String, dir: Any) {
        getVariantRes(variantName).srcDir(dir)
    }

    private fun getVariantRes(variantName: String): DefaultAndroidSourceDirectorySet {
        // Todo avoid cast to internal api
        return androidExtension.sourceSets.getByName(variantName).res as DefaultAndroidSourceDirectorySet
    }

    private fun createExtension(project: Project): StemExtension {
        return project.extensions.create(EXTENSION_NAME, StemExtension::class.java)
    }

    private fun getFilesFromConfiguration(variant: Variant, artifactType: String): FileCollection {
        return variant.runtimeConfiguration.incoming
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