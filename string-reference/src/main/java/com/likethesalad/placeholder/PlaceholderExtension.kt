package com.likethesalad.placeholder

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

@Suppress("UnstableApiUsage")
open class PlaceholderExtension(objectFactory: ObjectFactory) {
    val resolveOnBuild: Property<Boolean> = objectFactory.property(Boolean::class.java)
    var keepResolvedFiles: Boolean? = null
    var useDependenciesRes: Boolean? = null

    init {
        resolveOnBuild.convention(true)
    }
}