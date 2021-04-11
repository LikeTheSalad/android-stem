package com.likethesalad.placeholder.modules.common.helpers.resources.utils

import com.likethesalad.placeholder.modules.common.Constants.Companion.XML_STRING_TAG
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

class XmlUtils {

    companion object {
        private val RAW_VALUES_FILE_REGEX = Regex("^(?!resolved\\.xml)[A-Za-z0-9_]+\\.xml\$")

        fun stringResourceModelToElement(document: Document, stringResourceModel: StringResourceModel): Element {
            val strElement = document.createElement(XML_STRING_TAG)
            strElement.textContent = stringResourceModel.content
            for (it in stringResourceModel.attributes) {
                strElement.setAttribute(it.key, it.value)
            }
            return strElement
        }

        fun nodeToStringResourceModel(node: Node): StringResourceModel {
            val attributesMap = mutableMapOf<String, String>()
            val attributesNodes = node.attributes
            for (it in 0 until attributesNodes.length) {
                val attr = attributesNodes.item(it)
                attributesMap[attr.nodeName] = attr.textContent
            }
            return StringResourceModel(
                attributesMap,
                trimQuotes(
                    node.textContent
                )
            )
        }

        fun isValidRawXmlFileName(name: String): Boolean {
            return RAW_VALUES_FILE_REGEX.matches(name)
        }

        private fun trimQuotes(text: String): String {
            return text.replace(Regex("^\"|\"$"), "")
        }
    }
}