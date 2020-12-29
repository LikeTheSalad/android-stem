package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathResolver
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class VariantDirsPathResolverTest {

    @Test
    fun `Get path list from empty flavors`() {
        validatePathList("debug", emptyList(), "debug", "main", "debug")
        validatePathList("release", emptyList(), "release", "main", "release")
    }

    @Test
    fun `Get path list from raw string with one flavor`() {
        validatePathList(
            "fullDebug",
            listOf("full"), "debug",
            "main", "full", "debug", "fullDebug"
        )
    }

    @Test
    fun `Get path list from raw string with multiple flavors`() {
        validatePathList(
            "demoStableDebug",
            listOf("demo", "stable"), "debug",
            "main", "stable", "demo",
            "demoStable", "debug", "demoStableDebug"
        )

        validatePathList(
            "demoStablePaidDebug",
            listOf("demo", "stable", "paid"), "debug",
            "main", "paid", "stable", "demo",
            "demoStablePaid", "debug", "demoStablePaidDebug"
        )

        validatePathList(
            "demoStablePaidAnimeDebug",
            listOf("demo", "stable", "paid", "anime"), "debug",
            "main", "anime", "paid", "stable", "demo",
            "demoStablePaidAnime", "debug", "demoStablePaidAnimeDebug"
        )
    }

    private fun validatePathList(
        variantName: String,
        flavors: List<String>,
        suffix: String,
        vararg expectedPathItems: String
    ) {
        val variantHelper = mockk<AppVariantHelper>()
        every { variantHelper.getVariantName() }.returns(variantName)
        every { variantHelper.getVariantFlavors() }.returns(flavors)
        every { variantHelper.getVariantType() }.returns(suffix)
        val variantDirsPathResolver =
            VariantDirsPathResolver(
                variantHelper
            )

        Truth.assertThat(variantDirsPathResolver.pathList).containsExactlyElementsIn(expectedPathItems).inOrder()
    }
}