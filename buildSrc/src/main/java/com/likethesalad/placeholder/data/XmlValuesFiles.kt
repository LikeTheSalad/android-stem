package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.utils.AndroidXmlResDocument
import java.io.File

class XmlValuesFiles(val filesSet: Set<File>) {

    fun getStringResources(): Set<StringResourceModel> {
        return filesSet.map {
            AndroidXmlResDocument.readFromFile(it).getStringResourceList()
        }.flatten().toSet()
    }
}
