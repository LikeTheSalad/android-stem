package com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs

import com.google.auto.factory.AutoFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.utils.ValuesNameUtils
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import java.io.File

@AutoFactory
class VariantValuesFolders(variantResPaths: VariantResPaths) {

    val variantName = variantResPaths.variantName
    val valuesFolders: List<File> by lazy { getValuesFolders(variantResPaths.paths) }

    private fun getValuesFolders(resDirs: Set<File>): List<File> {
        return resDirs.map { resDir ->
            resDir.listFiles { _, name ->
                ValuesNameUtils.isValueName(name)
            }?.toList() ?: emptyList()
        }.flatten()
    }

}
