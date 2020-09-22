package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.storage.utils.ValuesStringsProvider
import com.likethesalad.placeholder.models.PlaceholderExtension
import com.likethesalad.placeholder.resolver.RecursiveLevelDetector
import com.likethesalad.placeholder.resolver.TemplateResolver
import org.gradle.api.artifacts.Configuration
import java.io.File

//todo delete
class TaskActionProviderFactory(
    private val buildDir: File,
    private val androidProjectHelper: AndroidProjectHelper,
    private val extension: PlaceholderExtension
) {

    private val valuesStringsProvider = ValuesStringsProvider()
    private val templateResolver = TemplateResolver(RecursiveLevelDetector())

    fun create(
        variantName: String,
        variantType: String,
        flavors: List<String>,
        configuration: Configuration
    ): TaskActionProvider {
        return TaskActionProvider(
            buildDir, configuration, variantName, variantType, flavors, androidProjectHelper,
            valuesStringsProvider, templateResolver, extension.keepResolvedFiles, extension.useDependenciesRes
        )
    }
}