package com.likethesalad.android.templates.provider.api

import com.likethesalad.android.templates.provider.api.versioning.TemplatesProviderVersion

interface TemplatesProvider : TemplatesProviderVersion {
    fun getId(): String
    fun getTemplates(): String
}