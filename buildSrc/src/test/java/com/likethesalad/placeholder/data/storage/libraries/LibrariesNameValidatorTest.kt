package com.likethesalad.placeholder.data.storage.libraries

import com.google.common.truth.Truth
import org.junit.Test

class LibrariesNameValidatorTest {

    @Test
    fun initialization() {

    }

    @Test
    fun isNameValid() {
        verifyNameIsValid("something", true, "something")
        verifyNameIsValid("something", false, "something2")
        verifyNameIsValid("something", false, "someth")
        verifyNameIsValid("something2", true, "someth", "something2", "something")
        verifyNameIsValid("something:else", true, "something:*")
        verifyNameIsValid("something:", true, "something:*")
        verifyNameIsValid("something", false, "something:*")
    }

    private fun verifyNameIsValid(name: String, shouldBeValid: Boolean, vararg allowedNames: String) {
        val librariesNameValidator = LibrariesNameValidator(allowedNames.toSet())
        Truth.assertThat(librariesNameValidator.isNameValid(name)).isEqualTo(shouldBeValid)
    }
}