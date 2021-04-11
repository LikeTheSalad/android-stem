package com.likethesalad.placeholder.testutils.lib.layout

import com.likethesalad.placeholder.testutils.base.layout.ProjectDescriptor


class AndroidLibProjectDescriptor(private val name: String) : ProjectDescriptor() {

    override fun getBuildGradleContents(): String {
        return """
            apply plugin: 'com.android.library'
            
            android {
                compileSdkVersion = 28
            }
        """.trimIndent()
    }

    override fun getProjectName(): String = name
}