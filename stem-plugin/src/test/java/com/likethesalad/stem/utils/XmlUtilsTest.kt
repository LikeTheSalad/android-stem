package com.likethesalad.stem.utils

import com.google.common.truth.Truth
import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.common.helpers.resources.utils.XmlUtils
import com.likethesalad.stem.testutils.named
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.impl.AndroidResourceScope
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class XmlUtilsTest {

    private val scope = AndroidResourceScope(Variant.Default, Language.Default)

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
        Truth.assertThat(result.textContent).isEqualTo("some content")
        Truth.assertThat(result.attributes.length).isEqualTo(2)
        Truth.assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        Truth.assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
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
        Truth.assertThat(com.likethesalad.tools.resource.collector.android.data.xml.XmlUtils.getContents(result))
            .isEqualTo("some content <b>something bold</b>")
        Truth.assertThat(result.attributes.length).isEqualTo(2)
        Truth.assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        Truth.assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
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
                    Attribute("someNsKey", "some extra attr", namespaceValue)
                )
            )
        val nsNameProvider = mockk<XmlUtils.NamespaceNameProvider>()
        every { nsNameProvider.getNameFor(namespaceValue) }.returns("ns1")

        // When:
        val result = XmlUtils.stringResourceModelToElement(stringResourceModel, nsNameProvider)

        // Then:
        Truth.assertThat(result.textContent).isEqualTo("some content")
        Truth.assertThat(result.attributes.length).isEqualTo(3)
        Truth.assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        Truth.assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
        Truth.assertThat(result.attributes.getNamedItem("ns1:someNsKey").textContent).isEqualTo("Some namespaced value")
    }
}