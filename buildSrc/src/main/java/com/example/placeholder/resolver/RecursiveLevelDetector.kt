package com.example.placeholder.resolver

import com.example.placeholder.data.Constants
import com.example.placeholder.models.StringResourceModel

class RecursiveLevelDetector {

    fun orderTemplatesByRecursiveLevel(templates: List<StringResourceModel>)
            : List<List<StringResourceModel>> {
        // Wrap templates:
        val templatesMap = templates.map { it.name to TemplateRecursiveLevel(it) }.toMap()

        // Get each's recursive level:
        for (it in templatesMap.values) {
            if (!it.hasLevelDefined) {
                it.recursiveLevel = getRecursiveLevel(it, templatesMap)
            }
        }

        // Group them by level:
        val recursiveLevelGroups = templatesMap.values.groupBy { it.recursiveLevel }
            .mapValues { it.value.map { p -> p.stringResourceModel } }

        // Turn them into an ordered metaList of lists:
        val templatesByLevel = mutableListOf<List<StringResourceModel>>()
        for (level in 0 until recursiveLevelGroups.size) {
            templatesByLevel.add(recursiveLevelGroups.getValue(level))
        }

        return templatesByLevel
    }

    private fun getRecursiveLevel(
        template: TemplateRecursiveLevel,
        allTemplates: Map<String, TemplateRecursiveLevel>,
        idPath: MutableList<String> = mutableListOf()
    ): Int {

        val templatesAsPlaceholder = Constants.TEMPLATE_AS_PLACEHOLDER_REGEX
            .findAll(template.stringResourceModel.content)

        return if (templatesAsPlaceholder.any()) {

            // Extract templates from text:
            val templatesPlaceholders = templatesAsPlaceholder.toList().map { it.groupValues[1] }.toSet()
                .map { allTemplates.getValue(it) }

            // Check for circular dependencies:
            if (templatesPlaceholders.any { it.stringResourceModel.name in idPath }) {
                throw IllegalArgumentException("Circular dependency on string templates")
            }

            // Add the current template id to the idPath metaList to avoid circular dependencies:
            idPath.add(template.stringResourceModel.name)

            // Check recursively for each template's level of dependencies
            val levels = templatesPlaceholders.map {
                if (!it.hasLevelDefined) {
                    it.recursiveLevel = getRecursiveLevel(it, allTemplates, idPath)
                }
                it.recursiveLevel + 1
            }

            // Grab the highest level, that will be this current template level
            levels.max()!!
        } else {
            0
        }
    }

    class TemplateRecursiveLevel(
        val stringResourceModel: StringResourceModel
    ) {
        var hasLevelDefined = false
            private set
        var recursiveLevel = NO_LEVEL
            set(value) {
                field = value
                hasLevelDefined = value != NO_LEVEL
            }

        companion object {
            const val NO_LEVEL = -1
        }
    }
}