package com.likethesalad.placeholder

import com.likethesalad.placeholder.data.OutputStringFileResolver
import com.likethesalad.placeholder.data.VariantDirsPathFinder
import com.likethesalad.placeholder.data.VariantDirsPathResolver
import com.likethesalad.placeholder.data.VariantRawStrings
import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.storage.*
import com.likethesalad.placeholder.models.PlaceholderExtension
import com.likethesalad.placeholder.resolver.RecursiveLevelDetector
import com.likethesalad.placeholder.resolver.TemplateResolver
import com.likethesalad.placeholder.tasks.GatherRawStringsTask
import com.likethesalad.placeholder.tasks.GatherTemplatesTask
import com.likethesalad.placeholder.tasks.ResolvePlaceholdersTask
import com.likethesalad.placeholder.tasks.actions.GatherRawStringsAction
import com.likethesalad.placeholder.tasks.actions.GatherTemplatesAction
import com.likethesalad.placeholder.tasks.actions.ResolvePlaceholdersAction
import org.gradle.api.Plugin
import org.gradle.api.Project

class ResolvePlaceholdersPlugin : Plugin<Project> {

    companion object {
        const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "resolver"
    }

    override fun apply(project: Project) {
        project.plugins.withId("com.android.application") {
            val extension = project.extensions.create("stringXmlReference", PlaceholderExtension::class.java)
            val projectHelper = AndroidProjectHelper(project)
            project.afterEvaluate {
                projectHelper.androidExtension.getApplicationVariants().forEach {
                    createResolvePlaceholdersTaskForVariant(
                        project,
                        it.getName(),
                        it.getBuildType().getName(),
                        it.getProductFlavors().map { flavor -> flavor.getName() },
                        projectHelper,
                        extension
                    )
                }
            }
        }
    }

    private fun createResolvePlaceholdersTaskForVariant(
        project: Project,
        variantName: String,
        variantType: String,
        flavors: List<String>,
        androidProjectHelper: AndroidProjectHelper,
        extension: PlaceholderExtension
    ) {


        val androidVariantHelper = AndroidVariantHelper(androidProjectHelper, variantName)
        val incrementalDirsProvider = IncrementalDirsProvider(androidVariantHelper)
        val incrementalDataCleaner = IncrementalDataCleaner(incrementalDirsProvider)
        val variantBuildResolvedDir = VariantBuildResolvedDir(
            variantName,
            project.buildDir,
            androidProjectHelper.androidExtension,
            extension.keepResolvedFiles
        )
        val outputStringFileResolver = OutputStringFileResolver(
            variantBuildResolvedDir,
            incrementalDirsProvider
        )
        val filesProvider = AndroidFilesProvider(outputStringFileResolver, incrementalDirsProvider)
        val androidResourcesHandler = AndroidResourcesHandler(outputStringFileResolver)
        val variantDirsPathResolver = VariantDirsPathResolver(variantName, flavors, variantType)
        val variantDirsPathFinder = VariantDirsPathFinder(variantDirsPathResolver, androidProjectHelper)
        val variantRawStrings = VariantRawStrings(variantDirsPathFinder)
        val resolvedDataCleaner = ResolvedDataCleaner(variantName, variantDirsPathFinder)

        val gatherStringsTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherRawStringsName,
            GatherRawStringsTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.gatherRawStringsAction = GatherRawStringsAction(
                variantRawStrings, androidResourcesHandler,
                incrementalDataCleaner
            )
        }

        val gatherTemplatesTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherStringsTask)

            it.gatherTemplatesAction = GatherTemplatesAction(
                filesProvider, androidResourcesHandler,
                incrementalDataCleaner
            )
        }

        val resolvePlaceholdersTask = project.tasks.create(
            androidVariantHelper.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherTemplatesTask)

            it.resolvePlaceholdersAction = ResolvePlaceholdersAction(
                filesProvider,
                androidResourcesHandler,
                TemplateResolver(RecursiveLevelDetector()),
                resolvedDataCleaner
            )
        }

        if (extension.resolveOnBuild) {
            androidVariantHelper.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
            androidVariantHelper.generateResValuesTask?.mustRunAfter(resolvePlaceholdersTask)
        }
    }
}