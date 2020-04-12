package com.likethesalad.placeholder.data.storage.libraries.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.storage.libraries.helpers.AllowedNamesProvider
import org.junit.Assert.fail
import org.junit.Test

class AllowedNamesProviderTest {

    @Test
    fun `initialization success`() {
        verifyGeneratedLists(
            setOf("something", "something2"),
            setOf("somethingelse:", "thisthing", "more-"),
            false,
            false,
            listOf("something", " something2  ", "somethingelse:*", "thisthing*", "more-*")
        )
        verifyGeneratedLists(
            setOf(),
            setOf("starts:with:"),
            false,
            false,
            listOf(
                "starts:with:*",
                "",
                "    "
            )
        )
        verifyGeneratedLists(
            setOf("the:full:name"),
            setOf(),
            false,
            false,
            listOf("the:full:name")
        )

        verifyGeneratedLists(
            setOf(),
            setOf(),
            false,
            true,
            emptyList()
        )
        verifyGeneratedLists(
            setOf(),
            setOf(),
            true,
            false,
            listOf("*")
        )
    }

    @Test
    fun `Initialization error due to bad wildcard format`() {
        val input = listOf("something", "else:*", "oops:*:haha")
        try {
            AllowedNamesProvider(input)
            fail("This test should've gone into the catch block")
        } catch (e: IllegalArgumentException) {
            Truth.assertThat(e.message)
                .isEqualTo(
                    "Misuse of wildcard in: 'oops:*:haha' - " +
                            "Asterisk wildcard should be the last thing on a String"
                )
        }
    }

    @Test
    fun `Initialization error due to multiple wildcards in one item`() {
        val input = listOf("something", "else:*", "oops:*:haha*")
        try {
            AllowedNamesProvider(input)
            fail("This test should've gone into the catch block")
        } catch (e: IllegalArgumentException) {
            Truth.assertThat(e.message)
                .isEqualTo(
                    "Misuse of wildcard in: 'oops:*:haha*' - " +
                            "There can only be one asterisk wildcard per item"
                )
        }
    }

    private fun verifyGeneratedLists(
        expectedFullName: Set<String>,
        expectedStartsWithName: Set<String>,
        expectedAllowAll: Boolean,
        expectedAllowNone: Boolean,
        allowedNames: List<String>
    ) {
        val allowedNamesProvider =
            AllowedNamesProvider(allowedNames)
        Truth.assertThat(allowedNamesProvider.fullNames).isEqualTo(expectedFullName)
        Truth.assertThat(allowedNamesProvider.startOfNames).isEqualTo(expectedStartsWithName)
        Truth.assertThat(allowedNamesProvider.allowAll).isEqualTo(expectedAllowAll)
        Truth.assertThat(allowedNamesProvider.allowNone).isEqualTo(expectedAllowNone)
    }
}