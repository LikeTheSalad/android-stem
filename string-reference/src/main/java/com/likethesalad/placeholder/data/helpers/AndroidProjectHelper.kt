package com.likethesalad.placeholder.data.helpers

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import org.gradle.api.Project

class AndroidProjectHelper(val project: Project) {
    val androidExtension: AndroidExtensionWrapper by lazy {
        AndroidExtensionWrapper(project.extensions.getByName("android"))
    }
}