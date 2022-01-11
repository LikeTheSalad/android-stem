package com.likethesalad.placeholder.modules.resolveStrings.resolver

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
}