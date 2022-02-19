package com.likethesalad.placeholder.utils

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.resources.utils.XmlUtils
import com.likethesalad.tools.resource.api.android.AndroidResourceScope
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory

class XmlUtilsTest {

    private val scope = AndroidResourceScope(Variant.Default, Language.Default)

    @Test
    fun checkStringResourceModelToElement() {
        // Given:
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val stringResourceModel =
            StringAndroidResource(
                mapOf(
                    "name" to "some_name",
                    "extra" to "some extra attr"
                ), "some content", scope
            )

        // When:
        val result = XmlUtils.stringResourceModelToElement(document, stringResourceModel)

        // Then:
        Truth.assertThat(result.textContent).isEqualTo("some content")
        Truth.assertThat(result.attributes.length).isEqualTo(2)
        Truth.assertThat(result.attributes.getNamedItem("name").textContent).isEqualTo("some_name")
        Truth.assertThat(result.attributes.getNamedItem("extra").textContent).isEqualTo("some extra attr")
    }
}