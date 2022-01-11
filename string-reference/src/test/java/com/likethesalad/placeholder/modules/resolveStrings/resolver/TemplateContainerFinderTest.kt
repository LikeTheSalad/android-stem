package com.likethesalad.placeholder.modules.resolveStrings.resolver

import com.google.common.truth.Truth
import org.junit.Test

class TemplateContainerFinderTest {

    @Test
    fun `Tell if string contains template in it`() {
        val template1 = "someTemplate1"
        val template2 = "someTemplate2"
        val instance = getInstance(template1, template2)

        checkIfContainsTemplate("Some string that has a \${$template2} template placeholder", instance, true)
        checkIfContainsTemplate("Some other text that has none", instance, false)
        checkIfContainsTemplate("Some text that has both \${$template1} and \${$template2} templates", instance, true)
        checkIfContainsTemplate("Some text that has a non-existing \${nope} template", instance, false)
    }

    private fun checkIfContainsTemplate(text: String, instance: TemplateContainerFinder, expected: Boolean) {
        Truth.assertThat(instance.containsTemplates(text)).isEqualTo(expected)
    }

    private fun getInstance(vararg templates: String): TemplateContainerFinder {
        return TemplateContainerFinder(templates.toList())
    }
}