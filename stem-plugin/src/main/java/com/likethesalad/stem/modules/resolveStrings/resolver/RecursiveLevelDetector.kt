package com.likethesalad.stem.modules.resolveStrings.resolver

import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.extensions.name
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecursiveLevelDetector @Inject constructor() {

    fun orderTemplatesByRecursiveLevel(
        templates: List<StringResource>,
        templateContainerFinder: TemplateContainerFinder
    ): List<List<StringResource>> {
        // Wrap templates:
        val templatesMap = templates.associate {
            it.name() to TemplateRecursiveLevel(it)
        }

        // Get each's recursive level:
        for (it in templatesMap.values) {
            if (!it.hasLevelDefined) {
                it.recursiveLevel = getRecursiveLevel(it, templateContainerFinder, templatesMap)
            }
        }

        // Group them by level:
        val recursiveLevelGroups = templatesMap.values.groupBy { it.recursiveLevel }
            .mapValues { it.value.map { p -> p.stringResourceModel } }

        // Turn them into an ordered metaList of lists:
        val templatesByLevel = mutableListOf<List<StringResource>>()
        for (level in 0 until recursiveLevelGroups.size) {
            templatesByLevel.add(recursiveLevelGroups.getValue(level))
        }

        return templatesByLevel
    }

    private fun getRecursiveLevel(
        template: TemplateRecursiveLevel,
        templateContainerFinder: TemplateContainerFinder,
        allTemplates: Map<String, TemplateRecursiveLevel>,
        idPath: MutableList<String> = mutableListOf()
    ): Int {

        val templatesAsPlaceholder =
            templateContainerFinder.getTemplateNamesFrom(template.stringResourceModel.text)

        return if (templatesAsPlaceholder.any()) {

            // Extract templates from text:
            val templatesPlaceholders = templatesAsPlaceholder.map { allTemplates.getValue(it) }

            // Check for circular dependencies:
            if (templatesPlaceholders.any { it.stringResourceModel.name() in idPath }) {
                throw IllegalArgumentException("Circular dependency on string templates")
            }

            // Add the current template id to the idPath metaList to avoid circular dependencies:
            idPath.add(template.stringResourceModel.name())

            // Check recursively for each template's level of dependencies
            val levels = templatesPlaceholders.map {
                if (!it.hasLevelDefined) {
                    it.recursiveLevel = getRecursiveLevel(it, templateContainerFinder, allTemplates, idPath)
                }
                it.recursiveLevel + 1
            }

            // Grab the highest level, that will be this current template level
            levels.maxOrNull()!!
        } else {
            0
        }
    }

    class TemplateRecursiveLevel(
        val stringResourceModel: StringResource
    ) {
        var hasLevelDefined = false
            private set
        var recursiveLevel =
            NO_LEVEL
            set(value) {
                field = value
                hasLevelDefined = value != NO_LEVEL
            }

        companion object {
            const val NO_LEVEL = -1
        }
    }
}