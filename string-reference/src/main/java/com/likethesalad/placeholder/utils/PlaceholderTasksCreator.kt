package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.configuration.ResolvePlaceholderConfiguration
import com.likethesalad.placeholder.locator.listener.TypeLocatorCreationListener
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersTask
import com.likethesalad.placeholder.modules.resolveStrings.data.ResolvePlaceholdersArgs
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesTask
import com.likethesalad.placeholder.modules.templateStrings.data.GatherTemplatesArgs
import com.likethesalad.placeholder.providers.TaskContainerProvider
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceholderTasksCreator @Inject constructor(
    taskContainerProvider: TaskContainerProvider,
    private val androidVariantContextFactory: AndroidVariantContext.Factory,
    private val taskActionProviderHolder: TaskActionProviderHolder,
    private val configuration: ResolvePlaceholderConfiguration
) : TypeLocatorCreationListener.Callback {

    companion object {
        const val RESOURCE_TYPE_COMMON = "common"
        const val RESOURCE_TYPE_TEMPLATE = "template"
    }

    private val taskContainer by lazy { taskContainerProvider.getTaskContainer() }

    override fun onLocatorsReady(variantTree: VariantTree, locatorsByType: Map<String, ResourceLocatorInfo>) {
        val androidVariantContext = androidVariantContextFactory.create(variantTree)
        val commonResourcesInfo = locatorsByType.getValue(RESOURCE_TYPE_COMMON)
        val templateResourcesInfo = locatorsByType.getValue(RESOURCE_TYPE_TEMPLATE)
        createResolvePlaceholdersTaskForVariant(androidVariantContext, commonResourcesInfo, templateResourcesInfo)
    }

    private fun createResolvePlaceholdersTaskForVariant(
        androidVariantContext: AndroidVariantContext,
        commonResourcesInfo: ResourceLocatorInfo,
        templateResourcesInfo: ResourceLocatorInfo
    ) {
        val gatherTemplatesActionProvider = taskActionProviderHolder.gatherTemplatesActionProvider
        val resolvePlaceholdersActionProvider = taskActionProviderHolder.resolvePlaceholdersActionProvider

        val gatherTemplatesTask = taskContainer.register(
            androidVariantContext.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java,
            GatherTemplatesArgs(
                gatherTemplatesActionProvider.provide(androidVariantContext),
                commonResourcesInfo.resourcesProvider,
                templateResourcesInfo.resourcesProvider
            )
        )

        gatherTemplatesTask.configure {
            it.group = ResolvePlaceholdersPlugin.RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.commonResourcesDir.set(commonResourcesInfo.taskInfo.outputDirectoryProvider.getOutputDirProperty())
            it.templateResourcesDir.set(templateResourcesInfo.taskInfo.outputDirectoryProvider.getOutputDirProperty())
        }

        val resolvePlaceholdersTask = taskContainer.register(
            androidVariantContext.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java,
            ResolvePlaceholdersArgs(resolvePlaceholdersActionProvider.provide(androidVariantContext))
        )

        resolvePlaceholdersTask.configure {
            it.group = ResolvePlaceholdersPlugin.RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.templatesDir.set(gatherTemplatesTask.flatMap { gatherTemplates -> gatherTemplates.outDir })
            it.outputDir.set(androidVariantContext.variantBuildResolvedDir.resolvedDir)
        }

        if (configuration.resolveOnBuild()) {
            androidVariantContext.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
        }
    }
}