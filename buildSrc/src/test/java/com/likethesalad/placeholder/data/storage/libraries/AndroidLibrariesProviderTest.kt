package com.likethesalad.placeholder.data.storage.libraries

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import com.likethesalad.placeholder.models.AndroidLibrary
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.junit.Before
import org.junit.Test
import java.io.File

@Suppress("UnstableApiUsage")
class AndroidLibrariesProviderTest {
    private lateinit var androidConfigHelper: AndroidConfigHelper
    private lateinit var androidLibrariesProvider: AndroidLibrariesProvider

    @Before
    fun setUp() {
        androidConfigHelper = mockk()
        androidLibrariesProvider = AndroidLibrariesProvider(androidConfigHelper)
    }

    @Test
    fun getAndroidLibraries() {
        val name1 = "someLib"
        val file1 = mockk<File>()
        val name2 = "someLib2"
        val file2 = mockk<File>()
        val artifacts = createMockArtifactsFor(
            Pair(name1, file1),
            Pair(name2, file2)
        )
        setResolvableDependenciesWith(artifacts)

        val libraries = androidLibrariesProvider.getAndroidLibraries()

        Truth.assertThat(libraries).containsExactly(
            AndroidLibrary(name1, file1),
            AndroidLibrary(name2, file2)
        )
    }

    private fun setResolvableDependenciesWith(artifacts: Set<ResolvedArtifactResult>) {
        val artifactCollection = mockk<ArtifactCollection>()
        every { artifactCollection.artifacts }.returns(artifacts)
        every { androidConfigHelper.getResArtifactCollection() }.returns(artifactCollection)
    }

    private fun createMockArtifactsFor(vararg names: Pair<String, File>): Set<ResolvedArtifactResult> {
        return names.map { item ->
            mockk<ResolvedArtifactResult>().also {
                val id = mockk<ComponentArtifactIdentifier>()
                every { id.displayName }.returns(item.first)
                every { it.id }.returns(id)
                every { it.file }.returns(item.second)
            }
        }.toSet()
    }
}