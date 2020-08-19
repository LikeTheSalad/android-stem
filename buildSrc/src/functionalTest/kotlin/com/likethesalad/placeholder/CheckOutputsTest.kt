package com.likethesalad.placeholder

import com.likethesalad.placeholder.testutils.AndroidAppProjectDefinition
import com.likethesalad.placeholder.testutils.base.BaseAndroidProjectTest
import org.junit.Test

class CheckOutputsTest : BaseAndroidProjectTest() {

    @Test
    fun checkIntegration() {
        createAppAndRun(AndroidAppProjectDefinition("app"), listOf("build"))
    }

    override fun getAndroidBuildPluginVersion(): String = "3.3.3"
}