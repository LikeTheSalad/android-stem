package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class VariantDirsPathHandlerTest {

    private lateinit var variantDirsPathResolver: VariantDirsPathResolver
    private lateinit var variantDirsPathHandler: VariantDirsPathHandler
    private val variantType = "debug"
    private val variantDirsPaths = listOf(
        "main",
        "client",
        variantType,
        "clientDebug"
    )

    @Before
    fun setup() {
        variantDirsPathResolver = mockk()
        every { variantDirsPathResolver.pathList }.returns(variantDirsPaths)
        every { variantDirsPathResolver.variantType }.returns(variantType)
        variantDirsPathHandler = VariantDirsPathHandler(variantDirsPathResolver)
    }

    @Test
    fun `Get list of path dirs`() {
        Truth.assertThat(variantDirsPathHandler.variantDirsPathResolver.pathList)
            .isEqualTo(variantDirsPaths)
    }

    @Test
    fun `Get if dir path is base`() {
        Truth.assertThat(variantDirsPathHandler.isBase("main")).isTrue()
        Truth.assertThat(variantDirsPathHandler.isBase("client")).isFalse()
        Truth.assertThat(variantDirsPathHandler.isBase("debug")).isFalse()
        Truth.assertThat(variantDirsPathHandler.isBase("clientDebug")).isFalse()
    }

    @Test
    fun `Get if path is build type`() {
        Truth.assertThat(variantDirsPathHandler.isVariantType("main")).isFalse()
        Truth.assertThat(variantDirsPathHandler.isVariantType("client")).isFalse()
        Truth.assertThat(variantDirsPathHandler.isVariantType("debug")).isTrue()
        Truth.assertThat(variantDirsPathHandler.isVariantType("clientDebug")).isFalse()
    }

    @Test
    fun `Get highest dir path from set`() {
        assertHighestFromSet(setOf("main", "client", "debug", "clientDebug"), "clientDebug")
        assertHighestFromSet(setOf("main", "client"), "client")
        assertHighestFromSet(setOf("main", "debug"), "debug")
        assertHighestFromSet(setOf("main", "debug", "clientDebug"), "clientDebug")
        assertHighestFromSet(setOf("debug", "clientDebug"), "clientDebug")
        assertHighestFromSet(setOf("client", "debug"), "debug")
    }

    @Test
    fun `Get output dir path from offset without flavors`() {
        val pathDirs = listOf("main", "debug")
        val variantDirsPathResolver = mockk<VariantDirsPathResolver>()
        every { variantDirsPathResolver.pathList }.returns(pathDirs)
        every { variantDirsPathResolver.variantType }.returns(variantType)

        val variantDirsPathHandler = VariantDirsPathHandler(variantDirsPathResolver)

        Truth.assertThat(variantDirsPathHandler.getOutputFrom("main")).isEqualTo("main")
        Truth.assertThat(variantDirsPathHandler.getOutputFrom("debug")).isEqualTo("debug")
    }

    @Test
    fun `Get output dir path from offset with flavors`() {
        Truth.assertThat(variantDirsPathHandler.getOutputFrom("main"))
            .isEqualTo("client")
        Truth.assertThat(variantDirsPathHandler.getOutputFrom("client"))
            .isEqualTo("client")
        Truth.assertThat(variantDirsPathHandler.getOutputFrom("clientDebug"))
            .isEqualTo("clientDebug")
        Truth.assertThat(variantDirsPathHandler.getOutputFrom("debug"))
            .isEqualTo("clientDebug")
    }

    private fun assertHighestFromSet(dirPaths: Set<String>, expectedHighest: String) {
        Truth.assertThat(variantDirsPathHandler.getHighestFrom(dirPaths)).isEqualTo(expectedHighest)
    }
}