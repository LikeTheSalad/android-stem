package com.likethesalad.placeholder.modules.common.helpers.files

import com.likethesalad.placeholder.modules.common.Constants.Companion.XML_RESOURCES_TAG
import com.likethesalad.placeholder.modules.common.Constants.Companion.XML_STRING_TAG
import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.common.helpers.resources.utils.XmlUtils
import org.w3c.dom.*
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
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

    companion object {

        fun readFromFile(xmlFile: File): AndroidXmlResDocument {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xmlFile.readText()))
            return AndroidXmlResDocument(
                dBuilder.parse(xmlInput)
            )
        }
    }

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

    fun appendStringResource(stringResourceModel: StringResourceModel) {
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

    fun appendAllStringResources(list: Collection<StringResourceModel>) {
        for (it in list) {
            appendStringResource(it)
        }
    }

    fun getStringList(): NodeList {
        return resources.getElementsByTagName(XML_STRING_TAG)
    }

    fun getStringResourceList(): List<StringResourceModel> {
        val strList = mutableListOf<StringResourceModel>()
        val strings = getStringList()

        for (it in 0 until strings.length) {
            strList.add(
                XmlUtils.nodeToStringResourceModel(
                    strings.item(it)
                )
            )
        }
        return strList
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