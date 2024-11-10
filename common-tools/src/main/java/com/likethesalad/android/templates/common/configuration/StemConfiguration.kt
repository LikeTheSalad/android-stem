package com.likethesalad.android.templates.common.configuration

import com.likethesalad.android.templates.common.plugins.extension.StemExtension
import java.util.function.Supplier
import java.util.regex.Pattern

class StemConfiguration(
    placeholderStart: Supplier<String>,
    placeholderEnd: Supplier<String>,
    includeLocalizedOnlyTemplates: Supplier<Boolean>
) {

    companion object {
        fun create(extension: StemExtension): StemConfiguration {
            return StemConfiguration(
                extension.placeholder.getStart()::get,
                extension.placeholder.getEnd()::get,
                extension.includeLocalizedOnlyTemplates::get
            )
        }
    }

    val placeholderStart by lazy { placeholderStart.get() }
    val placeholderEnd by lazy { placeholderEnd.get() }
    val placeholderRegex by lazy {
        Regex(
            "${Pattern.quote(this.placeholderStart)}([a-zA-Z0-9_]+)${
                Pattern.quote(
                    this.placeholderEnd
                )
            }"
        )
    }
    private val includeLocalizedOnlyTemplates by lazy { includeLocalizedOnlyTemplates.get() }

    fun searchForTemplatesInLanguages(): Boolean {
        return includeLocalizedOnlyTemplates
    }
}