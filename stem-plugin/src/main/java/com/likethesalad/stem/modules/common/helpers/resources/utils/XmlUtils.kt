package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.android.resources.data.StringResource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.xml.sax.InputSource

object XmlUtils {
    private val docBuilder by lazy {
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
    }

    fun stringResourceModelToElement(
        stringResourceModel: StringResource,
        namespaceNameProvider: NamespaceNameProvider
    ): Element {
        val reader = StringReader("<string>${stringResourceModel.value}</string>")
        val strElement = docBuilder.parse(InputSource(reader)).documentElement
        for (it in stringResourceModel.attributes) {
            it.namespace?.let { namespace ->
                val namespaceName = namespaceNameProvider.getNameFor(namespace)
                strElement.setAttribute("$namespaceName:${it.name}", it.value)
            } ?: strElement.setAttribute(it.name, it.value)
        }
        return strElement
    }

    interface NamespaceNameProvider {
        fun getNameFor(namespaceValue: String): String
    }
}