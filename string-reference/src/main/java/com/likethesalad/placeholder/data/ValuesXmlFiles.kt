package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.models.StringResourceModel
import com.likethesalad.placeholder.utils.AndroidXmlResDocument
import java.io.File

data class ValuesXmlFiles(val filesSet: Set<File>) {

    val stringResources: Set<StringResourceModel> by lazy {
        filesSet.map {
            AndroidXmlResDocument.readFromFile(it).getStringResourceList()
        }.flatten().toSet()
    }
}
