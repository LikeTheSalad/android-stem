package com.likethesalad.placeholder.modules.rawStrings.data.helpers.files

import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.likethesalad.placeholder.modules.common.helpers.files.AndroidXmlResDocument
import java.io.File

data class ValuesXmlFiles(val filesSet: Set<File>) {

    val stringResources: Set<StringResourceModel> by lazy {
        filesSet.map {
            AndroidXmlResDocument.readFromFile(it).getStringResourceList()
        }.flatten().toSet()
    }
}
