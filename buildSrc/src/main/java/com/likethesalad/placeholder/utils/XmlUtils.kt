package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.data.Constants.Companion.XML_STRING_TAG
import com.likethesalad.placeholder.models.StringResourceModel
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

class XmlUtils {

    companion object {

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
            return StringResourceModel(attributesMap, node.textContent)
        }
    }
}