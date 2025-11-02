package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.android.protos.StringResource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource

object XmlUtils {
    private val docBuilder by lazy {
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
    }
    private val contentExtractor by lazy {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer
    }
    private val OUTER_XML_TAGS_PATTERN = Regex("^<[^>]*>|<[^>]*>\$")

    fun stringResourceModelToElement(
        stringResourceModel: StringResource,
        namespaceNameProvider: NamespaceNameProvider
    ): Element {
        val reader = StringReader("<string>${stringResourceModel.text}</string>")
        val strElement = docBuilder.parse(InputSource(reader)).documentElement
        for (it in stringResourceModel.attributes) {
            it.namespace?.let { namespace ->
                val namespaceName = namespaceNameProvider.getNameFor(namespace)
                strElement.setAttribute("$namespaceName:${it.name}", it.text)
            } ?: strElement.setAttribute(it.name, it.text)
        }
        return strElement
    }

    fun getContents(node: Node): String {
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

    interface NamespaceNameProvider {
        fun getNameFor(namespaceValue: String): String
    }
}