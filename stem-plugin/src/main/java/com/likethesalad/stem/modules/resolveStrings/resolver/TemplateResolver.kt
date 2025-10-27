package com.likethesalad.stem.modules.resolveStrings.resolver

import com.likethesalad.android.protos.StringResource
import com.likethesalad.android.resources.extensions.name
import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.stem.modules.templateStrings.models.StringsTemplatesModel

class TemplateResolver(
    private val configuration: StemConfiguration,
    private val recursiveLevelDetector: RecursiveLevelDetector
) {

    fun resolveTemplates(stringsTemplatesModel: StringsTemplatesModel): List<StringResource> {
        val templateContainerFinder = createTemplatesFinder(stringsTemplatesModel)
        return if (stringsTemplatesModel.templates.any { templateContainerFinder.containsTemplates(it.text) }) {
            // If there are recursive templates
            resolveRecursiveTemplates(
                stringsTemplatesModel.templates,
                stringsTemplatesModel.values,
                templateContainerFinder
            )
        } else {
            // If there are no recursive templates
            resolveSimpleTemplates(stringsTemplatesModel.templates, stringsTemplatesModel.values)
        }
    }

    private fun resolveRecursiveTemplates(
        templates: List<StringResource>,
        originalValues: Map<String, String>,
        templateContainerFinder: TemplateContainerFinder
    ): List<StringResource> {
        // Get metaList of recursive level per template
        val recursiveLevelMetaList =
            recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates, templateContainerFinder)

        // Make the values mutable:
        val mutableValues = HashMap(originalValues)

        // The result:
        val resolvedTemplateList = mutableListOf<StringResource>()

        for (lst in recursiveLevelMetaList) {
            // Resolve this lst templates:
            val templatesResolved = resolveSimpleTemplates(lst, mutableValues)

            // Add resolved templates to the result:
            resolvedTemplateList.addAll(templatesResolved)

            // Add resolved templates to the values so that recursive templates can find them
            for (it in templatesResolved) {
                mutableValues[it.name()] = it.text
            }
        }

        return resolvedTemplateList
    }

    private fun resolveSimpleTemplates(
        templateList: List<StringResource>,
        values: Map<String, String>
    ): List<StringResource> {
        val resolvedTemplates = mutableListOf<StringResource>()
        for (it in templateList) {
            resolvedTemplates.add(getResolvedStringResourceModel(it, values))
        }
        return resolvedTemplates
    }

    private fun getResolvedStringResourceModel(original: StringResource, values: Map<String, String>)
            : StringResource {
        return StringResource(
            resolve(original.text, values),
            original.attributes
        )
    }

    private fun resolve(template: String, values: Map<String, String>): String {
        var resolvedString = template
        val occurrences = configuration.placeholderRegex.findAll(template)
        for (it in occurrences) {
            resolvedString = resolvedString.replace(it.value, values.getValue(it.groupValues[1]))
        }
        return resolvedString
    }

    private fun createTemplatesFinder(stringsTemplatesModel: StringsTemplatesModel): TemplateContainerFinder {
        return TemplateContainerFinder(configuration, stringsTemplatesModel.templates.map { it.name() })
    }
}