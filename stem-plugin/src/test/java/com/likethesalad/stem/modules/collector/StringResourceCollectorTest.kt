package com.likethesalad.stem.modules.collector

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.extensions.get
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class StringResourceCollectorTest {
    companion object {
        private val ASSETS_DIR = File(Paths.get("src", "test", "assets", "collector").absolutePathString())
    }

    @Test
    fun `Verify basic res dir`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("basic/main/res"))

        val resources = StringResourceCollector.collectStringResourcesPerValueDir(listOf(resDirs))

        assertThat(resources.values).containsOnlyKeys("values")
        assertThat(resources.get("values")).containsExactly(
            createStringResource("app_name", "Strings playground"),
            createStringResource("welcome_message", "Welcome to the app")
        )
    }

    @Test
    fun `Verify namespaced resources`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("namespaced/main/res"))

        val resources = StringResourceCollector.collectStringResourcesPerValueDir(listOf(resDirs))

        assertThat(resources.values).containsOnlyKeys("values")
        assertThat(resources.get("values")).containsExactly(
            createStringResource("app_name", "Strings playground"),
            createStringResource(
                "welcome_message",
                "Welcome to the app",
                createAttribute("someNsKey", "someNsValue", "http://schemas.android.com/tools")
            )
        )
    }

    @Test
    fun `Verify multiple res dirs`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("multipleresdirs/main/res"))
        resDirs.add(getInputDir("multipleresdirs/main/res2"))

        val resources = StringResourceCollector.collectStringResourcesPerValueDir(listOf(resDirs))

        assertThat(resources.values).containsOnlyKeys("values")
        assertThat(resources.get("values")).containsExactly(
            createStringResource("app_name", "Strings playground"),
            createStringResource("app_name_res2", "Strings playground 2"),
            createStringResource("welcome_message", "Welcome to the app"),
            createStringResource("welcome_message_res2", "Welcome to the app 2")
        )
    }

    @Test
    fun `Verify multiple res dirs with repeated name`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("multipleresdirsconflict/main/res"))
        resDirs.add(getInputDir("multipleresdirsconflict/main/res2"))

        try {
            StringResourceCollector.collectStringResourcesPerValueDir(listOf(resDirs))
            fail("An exception is expected")
        } catch (e: IllegalStateException) {
            assertThat(e).hasMessage("Duplicate name 'app_name' in 'values'")
        }
    }

    @Test
    fun `Verify multiple layers dirs merging`() {
        val layers = mutableListOf<Collection<File>>()
        layers.add(listOf(getInputDir("multiplelayers/demoDebug/res")))
        layers.add(listOf(getInputDir("multiplelayers/debug/res")))
        layers.add(listOf(getInputDir("multiplelayers/main/res")))

        val resources = StringResourceCollector.collectStringResourcesPerValueDir(layers)

        assertThat(resources.values).containsOnlyKeys("values")
        assertThat(resources.get("values")).containsExactly(
            createStringResource("app_name", "Demo debug playground"),
            createStringResource("some_other", "Some main string"),
            createStringResource("welcome_message", "Welcome to the debug app")
        )
    }

    @Test
    fun `Verify multiple xml files in single values dir`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("multiplefiles/main/res"))

        val resources = StringResourceCollector.collectStringResourcesPerValueDir(listOf(resDirs))

        assertThat(resources.values).containsOnlyKeys("values")
        assertThat(resources.get("values")).containsExactly(
            createStringResource("app_config_item", "Config item"),
            createStringResource("app_name", "Strings playground"),
            createStringResource("some_other_config", "Some other config"),
            createStringResource("welcome_message", "Welcome to the app")
        )
    }

    @Test
    fun `Verify multiple languages in single res dir`() {
        val resDirs = mutableListOf<File>()
        resDirs.add(getInputDir("multiplelanguages/main/res"))

        val resources = StringResourceCollector.collectStringResourcesPerValueDir(listOf(resDirs))

        assertThat(resources.values).containsOnlyKeys("values", "values-es")
        assertThat(resources.get("values")).containsExactly(
            createStringResource("app_name", "Strings playground"),
            createStringResource("welcome_message", "Welcome to the app")
        )
        assertThat(resources.get("values-es")).containsExactly(
            createStringResource("app_name_es", "Nombre de app"),
            createStringResource("welcome_message", "Bienvenido a la app")
        )
    }

    private fun createStringResource(
        name: String,
        value: String,
        vararg extraAttributes: Attribute
    ): StringResource {
        val attributes = listOf(createAttribute("name", name)).plus(extraAttributes)
        return StringResource(value, attributes)
    }

    private fun createAttribute(name: String, value: String, namespace: String? = null): Attribute {
        return Attribute(name, value, namespace)
    }

    private fun getInputDir(path: String) = File(ASSETS_DIR, "inputs/$path")
}