package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.data.helpers.AndroidProjectHelper
import com.likethesalad.placeholder.data.storage.libraries.helpers.AllowedNamesProvider
import com.likethesalad.placeholder.data.storage.libraries.helpers.LibrariesNameValidator
import com.likethesalad.placeholder.data.storage.utils.ValuesStringsProvider
import com.likethesalad.placeholder.models.PlaceholderExtension
import com.likethesalad.placeholder.resolver.RecursiveLevelDetector
import com.likethesalad.placeholder.resolver.TemplateResolver
import org.gradle.api.artifacts.Configuration
import java.io.File

class TaskActionProviderFactory(
    private val buildDir: File,
    private val androidProjectHelper: AndroidProjectHelper,
    private val extension: PlaceholderExtension
) {

    private val valuesStringsProvider = ValuesStringsProvider()
    private val templateResolver = TemplateResolver(RecursiveLevelDetector())

    private val allowedNamesProvider =
        AllowedNamesProvider(DependenciesExtensionCleaner.cleanUpDependencies(extension.useStringsFromDependencies))
    private val librariesNameValidator = LibrariesNameValidator(allowedNamesProvider)

    fun create(
        variantName: String,
        variantType: String,
        flavors: List<String>,
        configuration: Configuration
    ): TaskActionProvider {
        return TaskActionProvider(
            buildDir, configuration, variantName, variantType, flavors, androidProjectHelper,
            valuesStringsProvider, librariesNameValidator, templateResolver,
            extension.keepResolvedFiles
        )
    }
}