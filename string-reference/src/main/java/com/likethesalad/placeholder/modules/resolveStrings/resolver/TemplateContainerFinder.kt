package com.likethesalad.placeholder.modules.resolveStrings.resolver

import com.likethesalad.placeholder.modules.common.Constants

class TemplateContainerFinder(private val templateNames: List<String>) {

    private val templatesPattern: Regex by lazy {
        Regex(templateNames.fold("") { accumulated, current ->
            val placeholder = toPlaceholderFormat(current)
            if (accumulated.isEmpty()) placeholder else "$accumulated|$placeholder"
        })
    }

    fun containsTemplates(text: String): Boolean {
        return templatesPattern.containsMatchIn(text)
    }

    private fun toPlaceholderFormat(text: String): String {
        return "\\\$\\{$text}"
    }

    fun getTemplateNamesFrom(text: String): List<String> {
        val matches = templatesPattern.findAll(text).toList().map { it.value }

        return matches.toSet().map { Constants.PLACEHOLDER_REGEX.matchEntire(it)!!.groupValues[1] }
    }
}