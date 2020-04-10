package com.likethesalad.placeholder.data.storage.libraries

import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

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

    }

//    @Suppress("UnstableApiUsage")
//    private fun createResolvableDependenciesWith(artifacts: Set<ResolvedArtifactResult>)
//            : ResolvableDependencies {
//        val resolvableDependencies = mockk<ResolvableDependencies>()
//        val viewConfig = mockk<ArtifactView.ViewConfiguration>()
//        val actionCaptor = slot<Double>()
//
//
//    }
}