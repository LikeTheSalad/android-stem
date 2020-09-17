package com.likethesalad.placeholder.data.helpers

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.junit.Before
import org.junit.Test

@Suppress("UnstableApiUsage")
class AndroidArtifactViewActionProviderTest {

    private lateinit var androidArtifactViewActionProvider: AndroidArtifactViewActionProvider

    @Before
    fun setUp() {
        androidArtifactViewActionProvider = AndroidArtifactViewActionProvider()
    }

    @Test
    fun getResArtifactViewAction() {
        val viewConfig = mockk<ArtifactView.ViewConfiguration>()
        val attrContainer = mockk<AttributeContainer>(relaxed = true)
        val attrContainerActionCaptor = slot<Action<AttributeContainer>>()
        every { viewConfig.isLenient = any() }.returns(Unit)
        every { viewConfig.attributes(any()) }.returns(viewConfig)

        val action = androidArtifactViewActionProvider.getResArtifactViewAction()
        action.execute(viewConfig)

        verify { viewConfig.isLenient = false }
        verify { viewConfig.attributes(capture(attrContainerActionCaptor)) }

        attrContainerActionCaptor.captured.execute(attrContainer)
        verify {
            attrContainer.attribute(
                Attribute.of("artifactType", String::class.java), "android-res"
            )
        }
    }
}