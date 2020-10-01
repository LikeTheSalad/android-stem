package com.likethesalad.placeholder

import com.android.build.gradle.AppExtension
import com.likethesalad.placeholder.di.AppInjector
import com.likethesalad.placeholder.models.PlaceholderExtension
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.PlaceholderExtensionProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.tasks.GatherRawStringsTask
import com.likethesalad.placeholder.tasks.GatherTemplatesTask
import com.likethesalad.placeholder.tasks.ResolvePlaceholdersTask
import com.likethesalad.placeholder.utils.TaskActionProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class ResolvePlaceholdersPlugin : Plugin<Project>, AndroidExtensionProvider, BuildDirProvider,
    TaskProvider, PlaceholderExtensionProvider {

    companion object {
        const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "resolver"
        const val EXTENSION_NAME = "stringXmlReference"
    }

    private lateinit var project: Project
    private lateinit var extension: PlaceholderExtension
    private lateinit var androidExtension: AppExtension

    override fun apply(project: Project) {
        androidExtension = project.extensions.getByType(AppExtension::class.java)
        project.plugins.withId("com.android.application") {
            this.project = project
            AppInjector.init(this)
            extension = project.extensions.create(EXTENSION_NAME, PlaceholderExtension::class.java)
            project.afterEvaluate {
                val taskActionProviderFactory = AppInjector.getTaskActionProviderFactory()
                val variantDataExtractorFactory = AppInjector.getVariantDataExtractorFactory()

                androidExtension.applicationVariants.forEach {
                    createResolvePlaceholdersTaskForVariant(
                        taskActionProviderFactory.create(variantDataExtractorFactory.create(it)),
                        extension.resolveOnBuild
                    )
                }
            }
        }
    }

    private fun createResolvePlaceholdersTaskForVariant(
        taskActionProvider: TaskActionProvider,
        resolveOnBuild: Boolean
    ) {
        val androidVariantHelper = taskActionProvider.androidVariantHelper

        val gatherStringsTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherRawStringsName,
            GatherRawStringsTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.gatherRawStringsAction = taskActionProvider.gatherRawStringsAction
            it.dependenciesRes = taskActionProvider.androidConfigHelper.librariesResDirs
            it.gradleGeneratedStrings = androidVariantHelper.generateResValuesTask.outputs.files
        }

        val gatherTemplatesTask = project.tasks.create(
            androidVariantHelper.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherStringsTask)

            it.gatherTemplatesAction = taskActionProvider.gatherTemplatesAction
        }

        val resolvePlaceholdersTask = project.tasks.create(
            androidVariantHelper.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java
        ) {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherTemplatesTask)

            it.resolvePlaceholdersAction = taskActionProvider.resolvePlaceholdersAction
        }

        if (resolveOnBuild) {
            androidVariantHelper.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
        }
    }

    override fun getExtension(): AppExtension {
        return androidExtension
    }

    override fun getBuildDir(): File {
        return project.buildDir
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Task> findTaskByName(name: String): T {
        return project.tasks.findByName(name) as T
    }

    override fun getPlaceholderExtension(): PlaceholderExtension {
        return extension
    }
}