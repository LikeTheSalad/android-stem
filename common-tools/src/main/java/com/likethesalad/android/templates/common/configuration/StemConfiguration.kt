package com.likethesalad.android.templates.common.configuration

import com.likethesalad.android.templates.common.plugins.extension.StemExtension

class StemConfiguration(private val extension: StemExtension) {

    fun searchForTemplatesInLanguages(): Boolean {
        return extension.includeLocalizedOnlyTemplates.get()
    }
}