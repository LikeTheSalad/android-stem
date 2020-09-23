package com.likethesalad.placeholder.data.helpers

import org.gradle.api.Action
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.attributes.Attribute
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UnstableApiUsage")
@Singleton
class AndroidArtifactViewActionProvider @Inject constructor() {

    companion object {
        private val artifactTypeAttr = Attribute.of("artifactType", String::class.java)
    }

    fun getResArtifactViewAction(): Action<ArtifactView.ViewConfiguration> {
        return Action { config ->
            config.isLenient = false
            config.attributes {
                it.attribute(artifactTypeAttr, "android-res")
            }
        }
    }
}