package com.likethesalad.placeholder

import com.likethesalad.android.string.resources.locator.StringResourceLocatorPlugin
import com.likethesalad.placeholder.di.AppInjector
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersTask
import com.likethesalad.placeholder.modules.resolveStrings.data.ResolvePlaceholdersArgs
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesTask
import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.PlaceholderExtensionProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.utils.TaskActionProviderHolder
import com.likethesalad.tools.android.plugin.data.AndroidExtension
import com.likethesalad.tools.android.plugin.extension.AndroidToolsPluginExtension
import com.likethesalad.tools.resource.locator.android.extension.ResourceLocatorExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import java.io.File

class ResolvePlaceholdersPlugin : Plugin<Project>, AndroidExtensionProvider, BuildDirProvider,
    TaskProvider, PlaceholderExtensionProvider {

    companion object {
        const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "resolver"
        const val EXTENSION_NAME = "stringXmlReference"
    }

    private lateinit var project: Project
    private lateinit var extension: PlaceholderExtension
    private lateinit var androidExtension: AndroidExtension
    private lateinit var stringsLocatorExtension: ResourceLocatorExtension

    override fun apply(project: Project) {
        this.project = project
        AppInjector.init(this)
        project.plugins.apply(StringResourceLocatorPlugin::class.java)
        androidExtension = project.extensions.getByType(AndroidToolsPluginExtension::class.java).androidExtension
        extension = project.extensions.create(EXTENSION_NAME, PlaceholderExtension::class.java)
        stringsLocatorExtension = project.extensions.getByType(ResourceLocatorExtension::class.java)
        val taskActionProviderHolder = AppInjector.getTaskActionProviderHolder()
        val androidVariantContextFactory = AppInjector.getAndroidVariantContextFactory()

        stringsLocatorExtension.onResourceLocatorTaskCreated { taskContainer ->
            createResolvePlaceholdersTaskForVariant(
                androidVariantContextFactory.create(taskContainer.taskContext.variantTree.androidVariantData),
                taskActionProviderHolder, taskContainer.outputDir,
                true
            )
        }
    }

    private fun createResolvePlaceholdersTaskForVariant(
        androidVariantContext: AndroidVariantContext,
        taskActionProviderHolder: TaskActionProviderHolder,
        collectedResourcesDir: FileCollection,
        resolveOnBuild: Boolean
    ) {
        val gatherTemplatesActionProvider = taskActionProviderHolder.gatherTemplatesActionProvider
        val resolvePlaceholdersActionProvider = taskActionProviderHolder.resolvePlaceholdersActionProvider

        val gatherTemplatesTask = project.tasks.register(
            androidVariantContext.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java,
            GatherTemplatesArgs(gatherTemplatesActionProvider.provide(androidVariantContext), stringsLocatorExtension)
        )

        gatherTemplatesTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.inDir = collectedResourcesDir
        }

        val resolvePlaceholdersTask = project.tasks.register(
            androidVariantContext.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java,
            ResolvePlaceholdersArgs(resolvePlaceholdersActionProvider.provide(androidVariantContext))
        )

        resolvePlaceholdersTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherTemplatesTask)
        }

        if (resolveOnBuild) {
            androidVariantContext.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
        }
    }

    override fun getExtension(): AndroidExtension {
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