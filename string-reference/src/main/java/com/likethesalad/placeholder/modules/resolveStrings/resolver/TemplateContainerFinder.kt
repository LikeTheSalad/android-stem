package com.likethesalad.placeholder.modules.resolveStrings.resolver

class TemplateContainerFinder(private val templateNames: List<String>) {

    private val templatesPattern: Regex by lazy {
        Regex(templateNames.fold("") { accumulated, current ->
            val placeholder = toPlaceholderFormat(current)
            if (accumulated.isEmpty()) placeholder else "$accumulated|$placeholder"
        })
    }
    private val placeholderFormat: Regex by lazy {
        Regex("^\\\$\\{([^}]+)}")
    }

    fun containsTemplates(text: String): Boolean {
        return templatesPattern.containsMatchIn(text)
    }

    private fun toPlaceholderFormat(text: String): String {
        return "\\\$\\{$text}"
    }

    fun getTemplateNamesFrom(text: String): List<String> {
        val matches = templatesPattern.findAll(text).toList().map { it.value }

        return matches.toSet().map { placeholderFormat.matchEntire(it)!!.groupValues[1] }
    }
}