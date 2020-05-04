package com.likethesalad.placeholder.utils

import com.google.common.truth.Truth
import org.junit.Assert.fail
import org.junit.Test

class DependenciesExtensionCleanerTest {

    @Test
    fun cleanUpDependencies_success() {
        verifyCleanedParam("something", listOf("something"))
        verifyCleanedParam("*", listOf("*"))
        verifyCleanedParam(listOf("one", "other"), listOf("one", "other"))
        verifyCleanedParam(listOf("one*", "other"), listOf("one*", "other"))
    }

    @Test
    fun cleanUpDependencies_fail() {
        verifyExceptionWhenParamHasWrongType(Any())
        verifyExceptionWhenParamHasWrongType(listOf(1, 2, 3))
        verifyExceptionWhenParamHasWrongType(emptyArray<String>())
    }

    private fun verifyCleanedParam(param: Any, expected: List<String>) {
        Truth.assertThat(DependenciesExtensionCleaner.cleanUpDependencies(param)).isEqualTo(expected)
    }

    private fun verifyExceptionWhenParamHasWrongType(param: Any) {
        try {
            DependenciesExtensionCleaner.cleanUpDependencies(param)
            fail()
        } catch (e: IllegalArgumentException) {
            Truth.assertThat(e.message).isEqualTo(
                "The type of the parameter 'useStringsFromDependencies'" +
                        " can only be either String or List of Strings"
            )
        }
    }
}