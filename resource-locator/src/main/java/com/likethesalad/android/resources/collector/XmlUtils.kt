package com.likethesalad.android.resources.collector

import java.io.File
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Node

object XmlUtils {

    private val OUTER_XML_TAGS_PATTERN = Regex("^<[^>]*>|<[^>]*>\$")
    private val contentExtractor by lazy {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer
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
}