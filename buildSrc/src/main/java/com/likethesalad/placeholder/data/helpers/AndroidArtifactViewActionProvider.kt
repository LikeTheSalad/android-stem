package com.likethesalad.placeholder.data.helpers

import org.gradle.api.Action
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.attributes.Attribute

@Suppress("UnstableApiUsage")
class AndroidArtifactViewActionProvider {

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