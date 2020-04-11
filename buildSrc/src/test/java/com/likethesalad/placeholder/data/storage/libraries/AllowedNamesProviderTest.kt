package com.likethesalad.placeholder.data.storage.libraries

import com.google.common.truth.Truth
import org.junit.Assert.fail
import org.junit.Test

class AllowedNamesProviderTest {

    @Test
    fun `initialization success`() {
        verifyGeneratedLists(
            setOf("something", "something2"),
            setOf("somethingelse:", "thisthing", "more-"),
            "something", " something2  ", "somethingelse:*", "thisthing*", "more-*"
        )
        verifyGeneratedLists(
            setOf(),
            setOf("starts:with:"),
            "starts:with:*",
            "",
            "    "
        )
        verifyGeneratedLists(
            setOf("the:full:name"),
            setOf(),
            "the:full:name"
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
        vararg allowedNames: String
    ) {
        val allowedNamesProvider = AllowedNamesProvider(allowedNames.toList())
        Truth.assertThat(allowedNamesProvider.fullNames).isEqualTo(expectedFullName)
        Truth.assertThat(allowedNamesProvider.startOfNames).isEqualTo(expectedStartsWithName)
    }
}