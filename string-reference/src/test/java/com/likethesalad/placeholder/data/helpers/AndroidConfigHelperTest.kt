package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.file.FileCollection
import org.junit.Before
import org.junit.Test

class AndroidConfigHelperTest {

    private lateinit var androidConfigHelper: AndroidConfigHelper
    private lateinit var configuration: Configuration
    private lateinit var androidArtifactViewActionProvider: AndroidArtifactViewActionProvider

    @Before
    fun setUp() {
        configuration = mockk()
        androidArtifactViewActionProvider = mockk()
        androidConfigHelper = AndroidConfigHelper(configuration, androidArtifactViewActionProvider)
    }

    @Suppress("UnstableApiUsage")
    @Test
    fun getResArtifactCollection() {
        val resolvableDependencies = mockk<ResolvableDependencies>()
        val artifactCollection = mockk<ArtifactCollection>()
        val fileCollection = mockk<FileCollection>()
        val artifactView = mockk<ArtifactView>()
        val viewConfigAction = mockk<Action<ArtifactView.ViewConfiguration>>()
        every { androidArtifactViewActionProvider.getResArtifactViewAction() }.returns(viewConfigAction)
        every { resolvableDependencies.artifactView(viewConfigAction) }.returns(artifactView)
        every { artifactView.artifacts }.returns(artifactCollection)
        every { artifactCollection.artifactFiles }.returns(fileCollection)
        every { configuration.incoming }.returns(resolvableDependencies)

        val collection = androidConfigHelper.librariesResDirs

        Truth.assertThat(collection).isEqualTo(fileCollection)
        verify { androidArtifactViewActionProvider.getResArtifactViewAction() }
    }
}