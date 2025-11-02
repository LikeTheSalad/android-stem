package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.testutils.named
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class XmlUtilsTest {

    @Test
    fun checkStringResourceModelToElement() {
        // Given:
        val stringResourceModel =
            StringResource.named(
                "some_name",
                "some content",
                listOf(
                    Attribute("extra", "some extra attr", null)
                )
            )
        val noOpNsProvider = object : XmlUtils.NamespaceNameProvider {
            override fun getNameFor(namespaceValue: String): String {
                throw UnsupportedOperationException()
            }
        }

        // When:
        val result = XmlUtils.stringResourceModelToElement(
            stringResourceModel,
            noOpNsProvider
        )

        // Then:
        assertThat(result.textContent).isEqualTo("some content")
        assertThat(result.attributes.length).isEqualTo(2)
        assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
    }

    @Test
    fun checkStringResourceModelWithTagsToElement() {
        // Given:
        val stringResourceModel =
            StringResource.named(
                "some_name",
                "some content <b>something bold</b>",
                listOf(
                    Attribute("extra", "some extra attr", null)
                )
            )
        val noOpNsProvider = object : XmlUtils.NamespaceNameProvider {
            override fun getNameFor(namespaceValue: String): String {
                throw UnsupportedOperationException()
            }
        }

        // When:
        val result = XmlUtils.stringResourceModelToElement(
            stringResourceModel,
            noOpNsProvider
        )

        // Then:
        assertThat(XmlUtils.getContents(result))
            .isEqualTo("some content <b>something bold</b>")
        assertThat(result.attributes.length).isEqualTo(2)
        assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
    }

    @Test
    fun checkNamespacedStringResourceModelToElement() {
        // Given:
        val namespaceValue = "http://some.namespace"
        val stringResourceModel =
            StringResource.named(
                "some_name",
                "some content",
                listOf(
                    Attribute("extra", "some extra attr", null),
                    Attribute("someNsKey", "Some namespaced value", namespaceValue)
                )
            )
        val nsNameProvider = mockk<XmlUtils.NamespaceNameProvider>()
        every { nsNameProvider.getNameFor(namespaceValue) }.returns("ns1")

        // When:
        val result = XmlUtils.stringResourceModelToElement(stringResourceModel, nsNameProvider)

        // Then:
        assertThat(result.textContent).isEqualTo("some content")
        assertThat(result.attributes.length).isEqualTo(3)
        assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
        assertThat(result.attributes.getNamedItem("ns1:someNsKey").textContent).isEqualTo("Some namespaced value")
    }
}