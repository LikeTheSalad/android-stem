package com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs

import com.likethesalad.placeholder.modules.common.helpers.dirs.utils.ValuesNameUtils
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class VariantValuesFolders @AssistedInject constructor(@Assisted variantResPaths: VariantResPaths) {

    @AssistedFactory
    interface Factory {
        fun create(variantResPaths: VariantResPaths): VariantValuesFolders
    }

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
