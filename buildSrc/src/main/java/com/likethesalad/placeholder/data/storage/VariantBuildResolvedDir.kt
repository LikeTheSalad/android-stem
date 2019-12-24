package com.likethesalad.placeholder.data.storage

import java.io.File

class VariantBuildResolvedDir(variantName: String, buildDir: File) {

    val resolvedDir: File by lazy {
        File(buildDir, "generated/resolved/$variantName")
    }
}
