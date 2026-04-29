package com.likethesalad.stem.modules.resolveStrings

import com.likethesalad.stem.configuration.StemConfiguration
import com.likethesalad.stem.modules.common.BaseTask
import com.likethesalad.stem.modules.common.helpers.files.OutputStringFileResolver
import com.likethesalad.stem.modules.common.helpers.resources.AndroidResourcesHandler
import com.likethesalad.stem.modules.resolveStrings.resolver.RecursiveLevelDetector
import com.likethesalad.stem.modules.resolveStrings.resolver.TemplateResolver
import com.likethesalad.stem.tools.DirectoryUtils
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

@CacheableTask
open class ResolvePlaceholdersTask : BaseTask() {

    @get:Input
    val placeholderStart: Property<String> = project.objects.property(String::class.java)

    @get:Input
    val placeholderEnd: Property<String> = project.objects.property(String::class.java)

    @SkipWhenEmpty
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val templatesDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun resolve() {
        DirectoryUtils.clearIfNeeded(outputDir.get().asFile)
        val stemConfiguration = StemConfiguration(
            { placeholderStart.get() },
            { placeholderEnd.get() },
            { false }
        )
        ResolvePlaceholdersAction(
            TemplateResolver(stemConfiguration, RecursiveLevelDetector()),
            AndroidResourcesHandler(OutputStringFileResolver())
        ).resolve(templatesDir.get().asFile, outputDir.get().asFile)
    }
}
