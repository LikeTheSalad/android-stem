package com.likethesalad.placeholder.data

class VariantDirsPathHandler(
    val variantDirsPathResolver: VariantDirsPathResolver
) {

    companion object {
        const val BASE_DIR_PATH = "main"
    }

    fun isBase(dirPath: String): Boolean {
        return dirPath == BASE_DIR_PATH
    }

    fun isVariantType(dirPath: String): Boolean {
        return dirPath == variantDirsPathResolver.variantType
    }

    fun getHighestFrom(dirPaths: Set<String>): String {
        var highest = ""

        for (dirPath in variantDirsPathResolver.pathList.reversed()) {
            if (dirPath in dirPaths) {
                highest = dirPath
                break
            }
        }

        return highest
    }

    fun getOutputFrom(offset: String): String {
        return if (!hasFlavors()) {
            offset
        } else {
            when {
                isBase(offset) -> variantDirsPathResolver.pathList[1]
                isVariantType(offset) -> variantDirsPathResolver.pathList.last()
                else -> offset
            }
        }
    }

    private fun hasFlavors(): Boolean {
        return variantDirsPathResolver.pathList.size > 2
    }
}
