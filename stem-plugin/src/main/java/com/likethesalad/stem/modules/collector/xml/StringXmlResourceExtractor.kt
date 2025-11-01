package com.likethesalad.stem.modules.collector.xml

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.common.helpers.files.AndroidXmlResDocument
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Node
import org.w3c.dom.NodeList

object StringXmlResourceExtractor {

    private const val STRING_RESOURCE_PATH = "/resources/string"
    private val OUTER_XML_TAGS_PATTERN = Regex("^<[^>]*>|<[^>]*>\$")
    private val contentExtractor by lazy {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer
    }

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
        return getContents(node)
    }

    private fun getStringNodeList(document: AndroidXmlResDocument): NodeList {
        return document.getElementsByXPath(STRING_RESOURCE_PATH)
    }

    private fun getContents(node: Node): String {
        val outText = StringWriter()
        val streamResult = StreamResult(outText)
        return try {
            contentExtractor.transform(DOMSource(node), streamResult)
            val text = outText.toString()
            return OUTER_XML_TAGS_PATTERN.replace(text, "")
        } catch (e: TransformerException) {
            node.textContent
        }
    }
}