package com.likethesalad.android.templates.common.plugins.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class StemExtension @Inject constructor(objectFactory: ObjectFactory) {
    val includeLocalizedOnlyTemplates: Property<Boolean> = objectFactory.property(Boolean::class.java)

    init {
        includeLocalizedOnlyTemplates.convention(false)
    }
}