package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.utils.AppVariantHelper
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
    private lateinit var runtimeConfiguration: Configuration
    private lateinit var androidArtifactViewActionProvider: AndroidArtifactViewActionProvider

    @Before
    fun setUp() {
        runtimeConfiguration = mockk()
        androidArtifactViewActionProvider = mockk()
        val appVariantHelper = mockk<AppVariantHelper>()
        every { appVariantHelper.getRuntimeConfiguration() }.returns(runtimeConfiguration)
        androidConfigHelper = AndroidConfigHelper(appVariantHelper, androidArtifactViewActionProvider)
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
        every { runtimeConfiguration.incoming }.returns(resolvableDependencies)

        val collection = androidConfigHelper.librariesResDirs

        Truth.assertThat(collection).isEqualTo(fileCollection)
        verify { androidArtifactViewActionProvider.getResArtifactViewAction() }
    }
}