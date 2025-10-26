package com.likethesalad.android.resources.collector

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import org.w3c.dom.Node
import org.w3c.dom.NodeList

object StringXmlResourceExtractor {

    private const val STRING_RESOURCE_PATH = "/resources/string"

    fun getResourcesFromAndroidDocument(document: AndroidXmlResDocument): List<StringResource> {
        val stringList = mutableListOf<StringResource>()
        val nodeList = getStringNodeList(document)

        for (index in 0 until nodeList.length) {
            stringList.add(parseNodeToStringAndroidResource(nodeList.item(index)))
        }

        return stringList
    }

    private fun parseNodeToStringAndroidResource(node: Node): StringResource {
        val attributes = mutableListOf<Attribute>()
        val value = getNodeText(node)
        val attributesNodes = node.attributes
        for (index in 0 until attributesNodes.length) {
            val attr = attributesNodes.item(index)
            val attrName = attr.localName
            attributes.add(Attribute(attrName, attr.textContent, attr.namespaceURI))
        }
        return StringResource(value, attributes)
    }

    private fun getNodeText(node: Node): String {
        return XmlUtils.getContents(node)
    }

    private fun getStringNodeList(document: AndroidXmlResDocument): NodeList {
        return document.getElementsByXPath(STRING_RESOURCE_PATH)
    }
}