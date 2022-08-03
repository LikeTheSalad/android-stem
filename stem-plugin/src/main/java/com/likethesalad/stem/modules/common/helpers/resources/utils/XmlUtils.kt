package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.stem.modules.common.Constants.Companion.XML_STRING_TAG
import com.likethesalad.tools.resource.api.android.attributes.AndroidAttributeKey
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import org.w3c.dom.Document
import org.w3c.dom.Element

object XmlUtils {

    fun stringResourceModelToElement(
        document: Document, stringResourceModel: StringAndroidResource,
        namespaceNameProvider: NamespaceNameProvider
    ): Element {
        val strElement = document.createElement(XML_STRING_TAG)
        strElement.textContent = stringResourceModel.stringValue()
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