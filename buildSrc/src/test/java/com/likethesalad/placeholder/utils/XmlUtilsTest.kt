package com.likethesalad.placeholder.utils

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.StringResourceModel
import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory

class XmlUtilsTest {

    @Test
    fun checkStringResourceModelToElement() {
        // Given:
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val stringResourceModel =
            StringResourceModel(mapOf("name" to "some_name", "extra" to "some extra attr"), "some content")

        // When:
        val result = XmlUtils.stringResourceModelToElement(document, stringResourceModel)

        // Then:
        Truth.assertThat(result.textContent).isEqualTo("some content")
        Truth.assertThat(result.attributes.length).isEqualTo(2)
        Truth.assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        Truth.assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
    }

    @Test
    fun checkNodeToStringResourceModel() {
        // Given:
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val node = document.createElement("something")
        node.setAttribute("name", "the_name")
        node.setAttribute("other_attr", "other attr value")
        node.setAttribute("third_attr", "third value")
        node.textContent = "The content"

        // When:
        val result = XmlUtils.nodeToStringResourceModel(node)

        // Then:
        Truth.assertThat(result.content).isEqualTo("The content")
        Truth.assertThat(result.name).isEqualTo("the_name")
        Truth.assertThat(result.attributes).containsExactly(
            "name", "the_name",
            "other_attr", "other attr value",
            "third_attr", "third value"
        )
    }

    @Test
    fun `Check is valid xml file name to gather raw strings`() {
        assertValidXmlRawFileName("strings.xml", true)
        assertValidXmlRawFileName("something.xml", true)
        assertValidXmlRawFileName("resolved.xml", false)
        assertValidXmlRawFileName("strings.xml2", false)
        assertValidXmlRawFileName("strings", false)
        assertValidXmlRawFileName("strings.cxml", false)
    }

    private fun assertValidXmlRawFileName(name: String, shouldBeValid: Boolean) {
        Truth.assertThat(XmlUtils.isValidRawXmlFileName(name)).isEqualTo(shouldBeValid)
    }
}