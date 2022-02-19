package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.likethesalad.stem.modules.common.Constants.Companion.XML_STRING_TAG
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import org.w3c.dom.Document
import org.w3c.dom.Element

class XmlUtils {

    companion object {
        fun stringResourceModelToElement(document: Document, stringResourceModel: StringAndroidResource): Element {
            val strElement = document.createElement(XML_STRING_TAG)
            strElement.textContent = stringResourceModel.stringValue()
            for (it in stringResourceModel.attributes().asMap()) {
                strElement.setAttribute(it.key, it.value)
            }
            return strElement
        }
    }
}