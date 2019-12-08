package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import org.junit.Test

class ResStringsPathResolverTest {

    @Test
    fun `Get path list from empty flavors`() {
        validatePathList(emptyList(), "debug", "main", "debug")
        validatePathList(emptyList(), "release", "main", "release")
    }

    @Test
    fun `Get path list from raw string with one flavor`() {
        validatePathList(
            listOf("full"), "debug",
            "main", "full", "debug", "fullDebug"
        )
    }

    @Test
    fun `Get path list from raw string with multiple flavors`() {
        validatePathList(
            listOf("demo", "stable"), "debug",
            "main", "stable", "demo",
            "demoStable", "debug", "demoStableDebug"
        )

        validatePathList(
            listOf("demo", "stable", "paid"), "debug",
            "main", "paid", "stable", "demo",
            "demoStablePaid", "debug", "demoStablePaidDebug"
        )
    }

    private fun validatePathList(flavors: List<String>, suffix: String, vararg pathItems: String) {
        val resStringsPathResolver = ResStringsPathResolver(flavors, suffix)

        Truth.assertThat(resStringsPathResolver.getPath()).containsExactlyElementsIn(pathItems).inOrder()
    }
}