package com.likethesalad.placeholder.data.storage.libraries.helpers

import com.google.common.truth.Truth
import org.junit.Test

class LibrariesNameValidatorTest {

    @Test
    fun isNameValid() {
        verifyNameIsValid("something", true, "something")
        verifyNameIsValid("something", false, "something2")
        verifyNameIsValid("something", false, "someth")
        verifyNameIsValid("something2", true, "someth", "something2", "something")
        verifyNameIsValid("something:else", true, "something:*")
        verifyNameIsValid("something:", true, "something:*")
        verifyNameIsValid("something", false, "something:*")
        verifyNameIsValid("something", true, "*")
        verifyNameIsValid("something", false)
    }

    private fun verifyNameIsValid(name: String, shouldBeValid: Boolean, vararg allowedNames: String) {
        val allowedNamesProvider =
            AllowedNamesProvider(allowedNames.toList())
        val librariesNameValidator =
            LibrariesNameValidator(allowedNamesProvider)
        Truth.assertThat(librariesNameValidator.isNameValid(name)).isEqualTo(shouldBeValid)
    }
}