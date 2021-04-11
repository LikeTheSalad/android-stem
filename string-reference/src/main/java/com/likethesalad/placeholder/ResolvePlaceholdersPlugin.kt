package com.likethesalad.placeholder

import com.android.build.gradle.AppExtension
import com.likethesalad.placeholder.di.AppInjector
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.rawStrings.GatherRawStringsTask
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersTask
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesTask
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.PlaceholderExtensionProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.utils.TaskActionProviderHolder
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
                val taskActionProviderHolder = AppInjector.getTaskActionProviderHolder()
                val appVariantHelperFactory = AppInjector.getAppVariantHelperFactory()
                val androidVariantContextFactory = AppInjector.getAndroidVariantContextFactory()

                androidExtension.applicationVariants.forEach {
                    val androidVariantContext = androidVariantContextFactory.create(appVariantHelperFactory.create(it))
                    createResolvePlaceholdersTaskForVariant(
                        androidVariantContext,
                        taskActionProviderHolder,
                        extension.resolveOnBuild
                    )
                }
            }
        }
    }

    private fun createResolvePlaceholdersTaskForVariant(
        androidVariantContext: AndroidVariantContext,
        taskActionProviderHolder: TaskActionProviderHolder,
        resolveOnBuild: Boolean
    ) {
        val gatherRawStringsActionProvider = taskActionProviderHolder.gatherRawStringsActionProvider
        val gatherTemplatesActionProvider = taskActionProviderHolder.gatherTemplatesActionProvider
        val resolvePlaceholdersActionProvider = taskActionProviderHolder.resolvePlaceholdersActionProvider

        val gatherStringsTask = project.tasks.register(
            androidVariantContext.tasksNames.gatherRawStringsName,
            GatherRawStringsTask::class.java,
            gatherRawStringsActionProvider.provide(androidVariantContext)
        )

        gatherStringsTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependenciesRes = androidVariantContext.androidConfigHelper.librariesResDirs
            it.gradleGeneratedStrings = androidVariantContext.generateResValuesTask.outputs.files
        }

        val gatherTemplatesTask = project.tasks.register(
            androidVariantContext.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java,
            gatherTemplatesActionProvider.provide(androidVariantContext)
        )

        gatherTemplatesTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherStringsTask)
        }

        val resolvePlaceholdersTask = project.tasks.register(
            androidVariantContext.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java,
            resolvePlaceholdersActionProvider.provide(androidVariantContext)
        )

        resolvePlaceholdersTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherTemplatesTask)
        }

        if (resolveOnBuild) {
            androidVariantContext.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
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