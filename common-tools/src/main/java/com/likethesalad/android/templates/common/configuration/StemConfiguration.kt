package com.likethesalad.android.templates.common.configuration

import com.likethesalad.android.templates.common.plugins.extension.StemExtension
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StemConfiguration @Inject constructor(private val extension: StemExtension) {

    fun searchForTemplatesInLanguages(): Boolean {
        return extension.includeLocalizedOnlyTemplates.get()
    }
}