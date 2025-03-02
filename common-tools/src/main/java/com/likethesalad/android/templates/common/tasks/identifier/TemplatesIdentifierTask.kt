package com.likethesalad.android.templates.common.tasks.identifier

import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.BaseTask
import com.likethesalad.android.templates.common.tasks.identifier.action.TemplatesIdentifierAction
import com.likethesalad.android.templates.common.utils.upperFirst
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class TemplatesIdentifierTask @Inject constructor(private val args: Args) : BaseTask() {

    companion object {
        private const val TEMPLATES_IDENTIFIER_NAME_FORMAT = "templates%sIdentifier"

        fun generateTaskName(variantName: String): String {
            return TEMPLATES_IDENTIFIER_NAME_FORMAT.format(variantName.upperFirst())
        }
    }

    @InputDirectory
    val localResourcesDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    init {
        outputFile.set(project.layout.buildDirectory.file("intermediates/incremental/$name/templateNames"))
    }

    @TaskAction
    fun execute() {
        val action = TemplatesIdentifierAction.create(
            args.stemConfiguration,
            args.localResources,
            outputFile.get().asFile
        )
        action.execute()
    }

    data class Args(
        val localResources: ResourcesProvider,
        val stemConfiguration: StemConfiguration
    )
}
