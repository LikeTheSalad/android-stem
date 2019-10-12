package com.likethesalad.placeholder

import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.data.resources.AndroidResourcesHandler
import com.likethesalad.placeholder.data.storage.AndroidFilesProvider
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
        val projectHelper = AndroidProjectHelper(project)
        project.afterEvaluate {
            projectHelper.androidExtension.getApplicationVariants().forEach {
                createResolvePlaceholdersTaskForVariant(project, it.getName(), it.getFlavorName(), projectHelper)
            }
        }
    }

    private fun createResolvePlaceholdersTaskForVariant(
        project: Project,
        variantName: String,
        flavorName: String,
        androidProjectHelper: AndroidProjectHelper
    ) {

        val androidVariantHelper = AndroidVariantHelper(androidProjectHelper, variantName, flavorName)
        val filesProvider = AndroidFilesProvider(androidVariantHelper)
        val androidResourcesHandler = AndroidResourcesHandler(filesProvider)

        val gatherStringsTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherRawStringsName,
            GatherRawStringsTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.gatherRawStringsAction = GatherRawStringsAction(filesProvider, androidResourcesHandler)
        }

        val gatherTemplatesTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherStringsTask)

            it.gatherTemplatesAction = GatherTemplatesAction(filesProvider, androidResourcesHandler)
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
                TemplateResolver(RecursiveLevelDetector())
            )
        }

        androidVariantHelper.generateResValuesTask.dependsOn(resolvePlaceholdersTask)
    }
}