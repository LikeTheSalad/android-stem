package com.likethesalad.android.templates.provider

import com.likethesalad.android.templates.provider.di.TemplatesProviderComponent
import com.likethesalad.android.templates.provider.di.TemplatesProviderInjector
import com.likethesalad.android.templates.provider.task.TemplatesServiceGeneratorTask
import com.likethesalad.tools.android.plugin.base.AndroidToolsPluginConsumer
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

@Suppress("UnstableApiUsage")
class TemplatesProviderPlugin : AndroidToolsPluginConsumer() {

    companion object {
        private const val SERVICE_GENERATOR_TASK_TEMPLATE = "templateProvider%sGenerateMetadata"
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

        variant.registerGeneratedJavaBinaries(serviceGenerator, serviceGenerator.flatMap { it.outputDir })
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
}