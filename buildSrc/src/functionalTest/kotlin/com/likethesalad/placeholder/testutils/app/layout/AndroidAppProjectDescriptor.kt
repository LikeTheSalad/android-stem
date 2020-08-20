package com.likethesalad.placeholder.testutils.app.layout

import com.likethesalad.placeholder.testutils.base.layout.ProjectDescriptor

class AndroidAppProjectDescriptor(private val name: String) : ProjectDescriptor() {

    override fun getBuildGradleContents(): String {
        return """
            apply plugin: 'com.android.application'
            apply plugin: 'placeholder-resolver'
            
            android {
                compileSdkVersion = 28
            }
        """.trimIndent()
    }

    override fun getProjectName(): String = name
}