package com.likethesalad.placeholder.modules.resolveStrings.resolver

import com.likethesalad.placeholder.modules.common.Constants
import com.likethesalad.placeholder.modules.templateStrings.models.StringsTemplatesModel
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateResolver @Inject constructor(private val recursiveLevelDetector: RecursiveLevelDetector) {

    fun resolveTemplates(stringsTemplatesModel: StringsTemplatesModel): List<StringAndroidResource> {
        return if (stringsTemplatesModel.templates.any { containsTemplateAsPlaceholder(it.stringValue()) }) {
            // If there's recursive templates
            resolveRecursiveTemplates(stringsTemplatesModel.templates, stringsTemplatesModel.values)
        } else {
            // If there's no recursive templates
            resolveSimpleTemplates(stringsTemplatesModel.templates, stringsTemplatesModel.values)
        }
    }

    private fun resolveRecursiveTemplates(
        templates: List<StringAndroidResource>,
        originalValues: Map<String, String>
    ): List<StringAndroidResource> {
        // Get metaList of recursive level per template
        val recursiveLevelMetaList = recursiveLevelDetector.orderTemplatesByRecursiveLevel(templates)

        // Make the values mutable:
        val mutableValues = HashMap(originalValues)

        // The result:
        val resolvedTemplateList = mutableListOf<StringAndroidResource>()

        for (lst in recursiveLevelMetaList) {
            // Resolve this lst templates:
            val templatesResolved = resolveSimpleTemplates(lst, mutableValues)

            // Add resolved templates to the result:
            resolvedTemplateList.addAll(templatesResolved)

            // Add resolved templates to the values so that recursive templates can find them
            for (it in templatesResolved) {
                // Have to add the prefix as it's removed when resolved
                mutableValues[Constants.TEMPLATE_STRING_PREFIX + it.name()] = it.stringValue()
            }
        }

        return resolvedTemplateList
    }

    private fun resolveSimpleTemplates(
        templateList: List<StringAndroidResource>,
        values: Map<String, String>
    ): List<StringAndroidResource> {
        val resolvedTemplates = mutableListOf<StringAndroidResource>()
        for (it in templateList) {
            resolvedTemplates.add(getResolvedStringResourceModel(it, values))
        }
        return resolvedTemplates
    }

    private fun getResolvedStringResourceModel(original: StringAndroidResource, values: Map<String, String>)
            : StringAndroidResource {
        val attrs = original.attributes().asMap().toMutableMap()
        attrs["name"] = stripTemplatePrefix(original.name())
        return StringAndroidResource(
            attrs,
            resolve(original.stringValue(), values),
            original.getAndroidScope()
        )
    }

    private fun stripTemplatePrefix(text: String): String {
        return text.replace(Constants.TEMPLATE_STRING_PREFIX_REGEX, "")
    }

    private fun containsTemplateAsPlaceholder(content: String): Boolean {
        return Constants.TEMPLATE_AS_PLACEHOLDER_REGEX.containsMatchIn(content)
    }

    private fun resolve(template: String, values: Map<String, String>): String {
        var resolvedString = template
        val occurrences = Constants.PLACEHOLDER_REGEX.findAll(template).toList().map { it.groupValues[1] }.toSet()
        for (it in occurrences) {
            resolvedString = resolvedString.replace("\${$it}", values.getValue(it))
        }
        return resolvedString
    }
}