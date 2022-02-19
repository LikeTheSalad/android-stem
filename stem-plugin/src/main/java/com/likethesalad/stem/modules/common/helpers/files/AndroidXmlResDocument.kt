package com.likethesalad.stem.modules.common.helpers.files

import com.likethesalad.stem.modules.common.Constants.Companion.XML_RESOURCES_TAG
import com.likethesalad.stem.modules.common.Constants.Companion.XML_STRING_TAG
import com.likethesalad.stem.modules.common.helpers.resources.utils.XmlUtils
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class AndroidXmlResDocument(
    val document: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().apply {
        xmlStandalone = true
    }
) {

    val resources: Element = getOrCreateResources()

    fun saveToFile(file: File, indentSpaces: Int = 4) {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indentSpaces.toString())
        val domSource = DOMSource(document)
        val streamResult = StreamResult(file)

        transformer.transform(domSource, streamResult)
    }

    fun append(item: Node) {
        try {
            resources.appendChild(item)
        } catch (e: DOMException) {
            resources.appendChild(document.importNode(item, true))
        }
    }

    fun appendStringResource(stringResourceModel: StringAndroidResource) {
        append(
            XmlUtils.stringResourceModelToElement(
                document,
                stringResourceModel
            )
        )
    }

    fun appendAll(nodeList: NodeList) {
        for (it in 0 until nodeList.length) {
            append(nodeList.item(it))
        }
    }

    fun appendAllStringResources(list: Collection<StringAndroidResource>) {
        for (it in list) {
            appendStringResource(it)
        }
    }

    fun getStringList(): NodeList {
        return resources.getElementsByTagName(XML_STRING_TAG)
    }

    private fun getOrCreateResources(): Element {
        val resourcesList = document.getElementsByTagName(XML_RESOURCES_TAG)
        if (resourcesList.length > 0) {
            return resourcesList.item(0) as Element
        }
        val resources = document.createElement(XML_RESOURCES_TAG)
        document.appendChild(resources)
        return resources
    }
}