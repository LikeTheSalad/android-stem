package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.tools.resource.api.android.attributes.AndroidAttributeKey
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object XmlUtils {
    private val docBuilder by lazy {
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
    }

    fun stringResourceModelToElement(
        stringResourceModel: StringAndroidResource,
        namespaceNameProvider: NamespaceNameProvider
    ): Element {
        val reader = StringReader("<string>${stringResourceModel.stringValue()}</string>")
        val strElement = docBuilder.parse(InputSource(reader)).documentElement
        for (it in stringResourceModel.attributes().asMap()) {
            when (it.key) {
                is AndroidAttributeKey.Plain -> strElement.setAttribute(it.key.value(), it.value)
                is AndroidAttributeKey.Namespaced -> strElement.setAttribute(
                    getKeyName(it.key as AndroidAttributeKey.Namespaced, namespaceNameProvider),
                    it.value
                )
            }
        }
        return strElement
    }

    private fun getKeyName(namespacedKey: AndroidAttributeKey.Namespaced, provider: NamespaceNameProvider): String {
        return "${provider.getNameFor(namespacedKey.namespaceValue)}:${namespacedKey.value()}"
    }

    interface NamespaceNameProvider {
        fun getNameFor(namespaceValue: String): String
    }
}