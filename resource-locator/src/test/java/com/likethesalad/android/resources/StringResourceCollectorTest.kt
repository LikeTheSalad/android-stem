package com.likethesalad.android.resources

import com.likethesalad.android.resources.data.StringResource
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringResourceCollectorTest {
    companion object {
        private val ASSETS_DIR = File(Paths.get("src", "test", "assets").absolutePathString())
    }

    @Test
    fun `Verify basic res dir`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("basic/main/res"))

        val resources = StringResourceCollector.collectStringResources(listOf(resDirs))

        assertThat(resources).containsKeys("values")
        assertThat(resources["values"]).containsExactly(
            createStringResource("app_name", "Strings playground"),
            createStringResource("welcome_message", "Welcome to the app")
        )
    }

    private fun createStringResource(name: String, value: String): StringResource {
        return StringResource(value, listOf(StringResource.Attribute("name", name, null)))
    }

    private fun getInputDir(path: String) = File(ASSETS_DIR, "inputs/$path")
}