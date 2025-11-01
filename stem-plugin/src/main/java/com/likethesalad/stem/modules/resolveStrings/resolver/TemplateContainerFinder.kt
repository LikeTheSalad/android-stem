package com.likethesalad.stem.modules.resolveStrings.resolver

import com.likethesalad.stem.configuration.StemConfiguration
import java.util.regex.Pattern

class TemplateContainerFinder(
    private val configuration: StemConfiguration,
    private val templateNames: List<String>
) {

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
        return "${Pattern.quote(configuration.placeholderStart)}$text${Pattern.quote(configuration.placeholderEnd)}"
    }

    fun getTemplateNamesFrom(text: String): List<String> {
        val matches = templatesPattern.findAll(text).toList().map { it.value }

        return matches.toSet().map { configuration.placeholderRegex.matchEntire(it)!!.groupValues[1] }
    }
}