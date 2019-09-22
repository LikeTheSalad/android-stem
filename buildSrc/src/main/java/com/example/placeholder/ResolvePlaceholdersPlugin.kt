package com.example.placeholder

import com.example.placeholder.data.helpers.AndroidProjectHelper
import com.example.placeholder.data.helpers.AndroidVariantHelper
import com.example.placeholder.data.resources.AndroidResourcesHandler
import com.example.placeholder.data.storage.AndroidFilesProvider
import com.example.placeholder.resolver.RecursiveLevelDetector
import com.example.placeholder.resolver.TemplateResolver
import com.example.placeholder.tasks.GatherRawStringsTask
import com.example.placeholder.tasks.GatherTemplatesTask
import com.example.placeholder.tasks.ResolvePlaceholdersTask
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
            it.resourcesHandler = androidResourcesHandler
            it.filesProvider = filesProvider
        }

        val gatherTemplatesTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherStringsTask)

            it.resourcesHandler = androidResourcesHandler
            it.filesProvider = filesProvider
        }

        val resolvePlaceholdersTask = project.tasks.create(
            androidVariantHelper.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherTemplatesTask)

            it.resourcesHandler = androidResourcesHandler
            it.filesProvider = filesProvider
            it.templateResolver = TemplateResolver(RecursiveLevelDetector())
        }

        androidVariantHelper.generateResValuesTask.dependsOn(resolvePlaceholdersTask)
    }
}