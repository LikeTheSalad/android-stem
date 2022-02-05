package com.likethesalad.android.templates.provider

import com.likethesalad.android.templates.provider.di.TemplatesProviderComponent
import com.likethesalad.android.templates.provider.di.TemplatesProviderInjector
import com.likethesalad.android.templates.provider.tasks.metainf.ServiceMetaInfGeneratorTask
import com.likethesalad.android.templates.provider.tasks.service.TemplatesServiceGeneratorTask
import com.likethesalad.tools.android.plugin.base.AndroidToolsPluginConsumer
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

@Suppress("UnstableApiUsage")
class TemplatesProviderPlugin : AndroidToolsPluginConsumer() {

    companion object {
        private const val SERVICE_GENERATOR_TASK_TEMPLATE = "templateProvider%sGenerateService"
        private const val META_INF_GENERATOR_TASK_TEMPLATE = "templateProvider%sGenerateMetadata"
    }

    private lateinit var component: TemplatesProviderComponent

    override fun apply(project: Project) {
        super.apply(project)
        component = TemplatesProviderInjector.getComponent()

        androidTools.onVariant {
            attachTasks(project, it)
        }
    }

    private fun attachTasks(project: Project, variant: AndroidVariantData) {
        val serviceGenerator = createServiceGeneratorTask(project, variant.getVariantName())
        val metaInfGenerator = createMetaInfGeneratorTask(project, variant.getVariantName())

        metaInfGenerator.configure { task ->
            task.generatedClasspath.set(serviceGenerator.flatMap { it.outputDir })
        }

        variant.registerGeneratedJavaBinaries(serviceGenerator, serviceGenerator.flatMap { it.outputDir })
        variant.getProcessJavaResourcesProvider().configure {
            it.from(metaInfGenerator)
        }
    }

    private fun createServiceGeneratorTask(
        project: Project,
        variantName: String
    ): TaskProvider<TemplatesServiceGeneratorTask> {
        return project.tasks.register(
            SERVICE_GENERATOR_TASK_TEMPLATE.format(variantName.capitalize()),
            TemplatesServiceGeneratorTask::class.java,
            component.templatesServiceGeneratorArgs()
        )
    }

    private fun createMetaInfGeneratorTask(
        project: Project,
        variantName: String
    ): TaskProvider<ServiceMetaInfGeneratorTask> {
        return project.tasks.register(
            META_INF_GENERATOR_TASK_TEMPLATE.format(variantName.capitalize()),
            ServiceMetaInfGeneratorTask::class.java
        )
    }
}