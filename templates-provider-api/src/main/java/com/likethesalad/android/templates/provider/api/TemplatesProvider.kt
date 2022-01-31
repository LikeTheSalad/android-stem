package com.likethesalad.android.templates.provider.api

import com.likethesalad.android.templates.provider.api.versioning.TemplatesProviderHost

interface TemplatesProvider : TemplatesProviderHost {
    fun getId(): String
    fun getTemplates(): String
}