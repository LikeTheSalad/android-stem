package com.likethesalad.android.templates.common.plugins.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class StemExtension @Inject constructor(private val objectFactory: ObjectFactory) {
    val includeLocalizedOnlyTemplates: Property<Boolean> = objectFactory.property(Boolean::class.java)

    init {
        includeLocalizedOnlyTemplates.convention(false)
    }
}