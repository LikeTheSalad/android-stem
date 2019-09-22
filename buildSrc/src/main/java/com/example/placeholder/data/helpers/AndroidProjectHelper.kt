package com.example.placeholder.data.helpers

import com.example.placeholder.data.helpers.android.AndroidExtensionWrapper
import org.gradle.api.Project

class AndroidProjectHelper(val project: Project) {
    val androidExtension: AndroidExtensionWrapper by lazy {
        AndroidExtensionWrapper(project.extensions.getByName("android"))
    }
}