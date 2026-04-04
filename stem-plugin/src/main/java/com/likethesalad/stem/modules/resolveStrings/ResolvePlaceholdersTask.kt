package com.likethesalad.stem.modules.resolveStrings

import com.likethesalad.stem.modules.common.BaseTask
import com.likethesalad.stem.modules.resolveStrings.data.ResolvePlaceholdersArgs
import com.likethesalad.stem.tools.DirectoryUtils
import javax.inject.Inject
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
open class ResolvePlaceholdersTask
@Inject constructor(private val args: ResolvePlaceholdersArgs) : BaseTask() {

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
        args.resolvePlaceholdersAction.resolve(templatesDir.get().asFile, outputDir.get().asFile)
    }
}
