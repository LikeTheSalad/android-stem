package com.likethesalad.android.resources

import com.android.build.api.variant.Variant
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class VariantResTest {

    @Test
    fun `Get variants from empty flavors`() {
        validateNameOrder("debug", "debug", emptyList(), "main", "debug")
        validateNameOrder("release", "release", emptyList(), "main", "release")
    }

    @Test
    fun `Get variants from raw string with one flavor`() {
        validateNameOrder(
            "fullDebug",
            "debug", listOf("full"),
            "main", "full", "debug", "fullDebug"
        )
    }

    @Test
    fun `Get variants from raw string with multiple flavors`() {
        validateNameOrder(
            "demoStableDebug",
            "debug", listOf("demo", "stable"),
            "main", "stable", "demo",
            "demoStable", "debug", "demoStableDebug"
        )

        validateNameOrder(
            "demoStablePaidDebug",
            "debug", listOf("demo", "stable", "paid"),
            "main", "paid", "stable", "demo",
            "demoStablePaid", "debug", "demoStablePaidDebug"
        )

        validateNameOrder(
            "demoStablePaidAnimeDebug",
            "debug", listOf("demo", "stable", "paid", "anime"),
            "main", "anime", "paid", "stable", "demo",
            "demoStablePaidAnime", "debug", "demoStablePaidAnimeDebug"
        )
    }

    private fun validateNameOrder(
        variantName: String,
        buildType: String,
        flavors: List<String>,
        vararg expectedNameInOrder: String
    ) {
        val variant = mockk<Variant>()
        every { variant.name }.returns(variantName)
        every { variant.buildType }.returns(buildType)
        every { variant.productFlavors }.returns(flavors.map { "" to it })

        val variantRes = VariantRes.forVariant(mockk(), variant)

        Assertions.assertThat(variantRes.layers).containsExactlyElementsOf(expectedNameInOrder.toList())
    }
}