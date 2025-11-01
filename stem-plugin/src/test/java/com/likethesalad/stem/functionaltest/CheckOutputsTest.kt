package com.likethesalad.stem.functionaltest

import com.google.common.truth.Truth
import com.likethesalad.stem.functionaltest.testtools.BasePluginTest
import com.likethesalad.stem.functionaltest.testtools.PlaceholderBlock
import com.likethesalad.stem.functionaltest.testtools.StemConfigBlock
import com.likethesalad.stem.tools.extensions.upperFirst
import com.likethesalad.tools.functional.testing.AndroidTestProject
import com.likethesalad.tools.functional.testing.android.blocks.AndroidBlockItem
import com.likethesalad.tools.functional.testing.android.blocks.DefaultConfigAndroidBlockItem
import com.likethesalad.tools.functional.testing.android.blocks.FlavorAndroidBlockItem
import com.likethesalad.tools.functional.testing.android.descriptor.AndroidAppProjectDescriptor
import com.likethesalad.tools.functional.testing.android.descriptor.AndroidLibProjectDescriptor
import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem
import com.likethesalad.tools.functional.testing.blocks.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import java.io.File
import junit.framework.TestCase.fail
import org.junit.Test
import org.xmlunit.builder.DiffBuilder.compare
import org.xmlunit.builder.Input

class CheckOutputsTest : BasePluginTest() {

    companion object {
        private const val RESOLVER_PLUGIN_ID = "com.likethesalad.stem"
    }

    private val inputAssetsProvider = TestAssetsProvider("inputs")
    private val outputAssetsProvider = TestAssetsProvider("outputs")

    @Test
    fun `verify basic app outputs`() {
        runInputOutputComparisonTest("basic", listOf("debug"))
    }

    @Test
    fun `verify basic app outputs with namespaces`() {
        runInputOutputComparisonTest("basic_with_namespace", listOf("debug"))
    }

    @Test
    fun `verify custom placeholder start`() {
        val projectName = "custom_placeholder_start"

        val placeholderBlock = PlaceholderBlock(start = "{{")

        val project = createProject(
            createAndroidAppProjectDescriptor(
                projectName,
                config = StemConfigBlock(placeholderBlock = placeholderBlock)
            )
        )

        runInputOutputComparisonTest(
            project,
            projectName,
            listOf("debug"),
            "custom_placeholder_result"
        )
    }

    @Test
    fun `verify custom placeholder end`() {
        val projectName = "custom_placeholder_end"

        val placeholderBlock = PlaceholderBlock(end = "}}")

        val project = createProject(
            createAndroidAppProjectDescriptor(
                projectName,
                config = StemConfigBlock(placeholderBlock = placeholderBlock)
            )
        )

        runInputOutputComparisonTest(
            project,
            projectName,
            listOf("debug"),
            "custom_placeholder_result"
        )
    }

    @Test
    fun `verify custom placeholder start and end`() {
        val projectName = "custom_placeholder_both"

        val placeholderBlock = PlaceholderBlock(start = "{{", end = "}}")

        val project = createProject(
            createAndroidAppProjectDescriptor(
                projectName,
                config = StemConfigBlock(placeholderBlock = placeholderBlock)
            )
        )

        runInputOutputComparisonTest(
            project,
            projectName,
            listOf("debug"),
            "custom_placeholder_result"
        )
    }

    @Test
    fun `verify nothing happens when there are no templates available`() {
        val variantNames = listOf("debug")
        val inOutDirName = "no_templates_available"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val commandList = variantNamesToResolveCommands(variantNames)
        val project = createProject(descriptor)

        val result = project.runGradle(inOutDirName, commandList)

        verifyResultContainsText(
            result, """
            > Task :$inOutDirName:debugGatherStringTemplates
            > Task :$inOutDirName:debugResolvePlaceholders NO-SOURCE
        """.trimIndent()
        )
        verifyEmptyOutput(inOutDirName, "debug")
    }

    @Test
    fun `verify previous outputs cleared when templates are deleted afterwards`() {
        val variantNames = listOf("debug")
        val projectName = "no_templates_available_afterwards"
        val withTemplatesDir = "basic"
        val descriptor = createAndroidAppProjectDescriptor(projectName, withTemplatesDir)
        val commandList = variantNamesToResolveCommands(variantNames)
        val project = createProject(descriptor)

        val result = project.runGradle(projectName, commandList)

        verifyResultContainsText(
            result, """
            > Task :$projectName:debugGatherStringTemplates
            > Task :$projectName:debugResolvePlaceholders
        """.trimIndent()
        )
        verifyVariantResults(variantNames, projectName, withTemplatesDir)

        // After removing templates:
        val withoutTemplatesDir = "no_templates_available"
        val descriptor2 = createAndroidAppProjectDescriptor(projectName, withoutTemplatesDir)
        val projectDir = getTempFile(projectName)
        descriptor.projectDirBuilder.clearFilesCreated()
        descriptor2.projectDirBuilder.buildDirectory(projectDir)

        project.runGradle(projectName, commandList)

        verifyEmptyOutput(projectName, "debug")
    }

    @Test
    fun `verify basic app outputs are generated only once if the inputs don't change`() {
        val variantNames = listOf("debug")
        val inOutDirName = "basic_repeated"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val commandList = variantNamesToResolveCommands(variantNames)
        val project = createProject(descriptor)

        val result1 = project.runGradle(inOutDirName, commandList)
        verifyResultContainsText(
            result1, """
            > Task :$inOutDirName:debugGatherStringTemplates
            > Task :$inOutDirName:debugResolvePlaceholders
        """.trimIndent()
        )

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)

        // Second time
        val result2 = project.runGradle(inOutDirName, commandList)

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)
        verifyResultContainsText(
            result2, """
            > Task :$inOutDirName:debugGatherStringTemplates UP-TO-DATE
            > Task :$inOutDirName:debugResolvePlaceholders UP-TO-DATE
        """.trimIndent()
        )
    }

    @Test
    fun `verify multi languages clean up after changes`() {
        val variantNames = listOf("debug")
        val inOutDirName = "multi_languages_changed_before"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val commandList = variantNamesToResolveCommands(variantNames)
        val project = createProject(descriptor)

        val result1 = project.runGradle(inOutDirName, commandList)
        verifyResultContainsText(
            result1, """
            > Task :$inOutDirName:debugGatherStringTemplates
            > Task :$inOutDirName:debugResolvePlaceholders
        """.trimIndent()
        )

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)

        // Second time
        val dirName2 = "multi_languages_changed_after"
        val descriptor2 = createAndroidAppProjectDescriptor(inOutDirName, dirName2)
        val projectDir = getTempFile(inOutDirName)
        descriptor.projectDirBuilder.clearFilesCreated()
        descriptor2.projectDirBuilder.buildDirectory(projectDir)

        val result2 = project.runGradle(inOutDirName, commandList)

        verifyVariantResults(variantNames, inOutDirName, dirName2)
        verifyResultContainsText(
            result2, """
            > Task :$inOutDirName:debugGatherStringTemplates
            > Task :$inOutDirName:debugResolvePlaceholders
        """.trimIndent()
        )
    }

    @Test
    fun `verify multi-languages app outputs`() {
        runInputOutputComparisonTest(
            "multi_languages", listOf("debug")
        )
    }

    @Test
    fun `verify multi-languages app outputs with localized-only templates`() {
        val inOutDirName = "multi_languages_localized_templates"
        val androidProjectDescriptor = createAndroidAppProjectDescriptor(
            inOutDirName,
            config = StemConfigBlock(true)
        )
        runInputOutputComparisonTest(
            createProject(androidProjectDescriptor), inOutDirName, listOf("debug")
        )
    }

    @Test
    fun `verify app with gradle-generated strings outputs`() {
        val appName = "with_gradle_strings"
        val gradleStrings = mapOf("my_app_id" to "APP ID")
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            androidBlockItems = listOf(DefaultConfigAndroidBlockItem(gradleStrings))
        )

        runInputOutputComparisonTest(createProject(appDescriptor), appName, listOf("debug"))
    }

    @Test
    fun `verify flavored app outputs`() {
        val inOutDirName = "flavored_app"
        val flavors = mutableListOf<FlavorAndroidBlockItem.FlavorDescriptor>()
        val modeFlavors = listOf("demo", "full")
        val environmentFlavors = listOf("stable", "prod")
        flavors.add(FlavorAndroidBlockItem.FlavorDescriptor("mode", modeFlavors))
        flavors.add(FlavorAndroidBlockItem.FlavorDescriptor("environment", environmentFlavors))
        val flavoredDescriptor = createAndroidAppProjectDescriptor(
            inOutDirName,
            androidBlockItems = listOf(FlavorAndroidBlockItem(flavors))
        )

        runInputOutputComparisonTest(
            createProject(flavoredDescriptor),
            inOutDirName,
            listOf(
                "fullStableDebug",
                "demoStableDebug",
                "fullProdDebug",
                "demoProdDebug"
            )
        )
    }

    @Test
    fun `verify app that takes resources from a library`() {
        // Create library
        val libName = "mylibrary"
        val libDescriptor = createAndroidLibProjectDescriptor(libName)
        libDescriptor.dependenciesBlock.addDependency("implementation 'com.android.support:recyclerview-v7:28.0.0'")

        // Set up app
        val appName = "with_library"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf("stemProvider project(':$libName')")
        )
        val project = createProject(libDescriptor, appDescriptor)

        runInputOutputComparisonTest(project, appName, listOf("debug"))
    }

    @Test
    fun `verify app that takes resources from a library with namespaces`() {
        // Create library
        val libName = "mylibrary_with_namespaces"
        val libDescriptor = createAndroidLibProjectDescriptor(libName)
        libDescriptor.dependenciesBlock.addDependency("implementation 'com.android.support:recyclerview-v7:28.0.0'")

        // Set up app
        val appName = "with_library_with_namespaces"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf("stemProvider project(':$libName')")
        )
        val project = createProject(libDescriptor, appDescriptor)

        runInputOutputComparisonTest(project, appName, listOf("debug"))
    }

    @Test
    fun `verify app that takes resources from an aar file`() {
        val appName = "with_aar_file_library"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf("stemProvider fileTree(dir: 'src/libs', include: ['*.aar'])")
        )

        runInputOutputComparisonTest(createProject(appDescriptor), appName, listOf("debug"))
    }

    @Test
    fun `verify app that takes resources from both local and external libraries`() {
        // Create library
        val libName = "my_local_library"
        val libDescriptor = createAndroidLibProjectDescriptor(libName)

        // Set up app
        val appName = "with_aar_and_local_libraries"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf(
                "stemProvider fileTree(dir: 'src/libs', include: ['*.aar'])",
                "stemProvider project(':$libName')"
            )
        )
        val project = createProject(libDescriptor, appDescriptor)

        runInputOutputComparisonTest(project, appName, listOf("debug"))
    }

    @Test
    fun `verify app that takes resources from multiple libraries`() {
        // Create library
        val libName1 = "my_first_library"
        val libName2 = "my_other_library"
        val libDescriptor1 = createAndroidLibProjectDescriptor(libName1)
        val libDescriptor2 = createAndroidLibProjectDescriptor(libName2)

        // Set up app
        val appName = "with_multiple_libraries"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf(
                "stemProvider project(':$libName1')",
                "stemProvider project(':$libName2')"
            )
        )
        val project = createProject(libDescriptor1, libDescriptor2, appDescriptor)

        runInputOutputComparisonTest(project, appName, listOf("debug"))
    }

    private fun runInputOutputComparisonTest(
        projectName: String,
        variantNames: List<String>,
        outputDirName: String = projectName
    ) {
        runInputOutputComparisonTest(
            createProject(createAndroidAppProjectDescriptor(projectName)),
            projectName,
            variantNames,
            outputDirName
        )
    }

    private fun runInputOutputComparisonTest(
        project: AndroidTestProject,
        projectName: String,
        variantNames: List<String>,
        outputDirName: String = projectName
    ) {
        val commandList = variantNamesToResolveCommands(variantNames)

        project.runGradle(projectName, commandList)

        verifyVariantResults(variantNames, projectName, outputDirName)
    }

    private fun createAndroidLibProjectDescriptor(
        libName: String,
        inputDirName: String = libName
    ): AndroidLibProjectDescriptor {
        val inputDir = getInputTestAsset(inputDirName)
        return AndroidLibProjectDescriptor(libName, inputDir)
    }

    private fun createAndroidAppProjectDescriptor(
        projectName: String,
        inputDirName: String = projectName,
        androidBlockItems: List<AndroidBlockItem> = emptyList(),
        dependencies: List<String> = emptyList(),
        config: StemConfigBlock? = null
    ): AndroidAppProjectDescriptor {
        val inputDir = getInputTestAsset(inputDirName)
        val blockItems = mutableListOf<GradleBlockItem>()
        blockItems.addAll(androidBlockItems)
        if (config != null) {
            blockItems.add(config)
        }
        val descriptor =
            AndroidAppProjectDescriptor(projectName, inputDir, blockItems)
        dependencies.forEach { dependency ->
            descriptor.dependenciesBlock.addDependency(dependency)
        }
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration(RESOLVER_PLUGIN_ID))
        return descriptor
    }

    private fun verifyVariantResults(variantNames: List<String>, projectName: String, outputDirName: String) {
        variantNames.forEach {
            verifyExpectedOutput(projectName, outputDirName, it)
        }
    }

    private fun variantNamesToResolveCommands(variantNames: List<String>) =
        variantNames.map { "merge${it.upperFirst()}Resources" }

    private fun getInputTestAsset(inputDirName: String): File {
        return inputAssetsProvider.getFile(inputDirName)
    }

    private fun getOutputTestAsset(outputDirName: String): File {
        return outputAssetsProvider.getFile(outputDirName)
    }

    private fun verifyExpectedOutput(
        projectName: String,
        outputDirName: String,
        variantName: String
    ) {
        val projectDir = getTempFile(projectName)
        val resultDir = File(projectDir, "build/generated/resolved/$variantName")
        Truth.assertThat(resultDir.exists()).isTrue()
        verifyDirsContentsAreEqual(getExpectedOutputDir(outputDirName, variantName), resultDir)
    }

    private fun verifyEmptyOutput(projectName: String, variantName: String) {
        val projectDir = getTempFile(projectName)
        val resultDir = File(projectDir, "build/generated/resolved/$variantName")
        Truth.assertThat(resultDir.exists()).isFalse()
    }

    private fun getExpectedOutputDir(inOutDirName: String, variantName: String): File {
        // Return specific variant's outputs dir if any, else fallback to "main".
        val expectedOutputRootDir = getOutputTestAsset(inOutDirName)
        val variantOutputDir = File(expectedOutputRootDir, variantName)
        if (variantOutputDir.exists()) {
            return variantOutputDir
        }

        return File(expectedOutputRootDir, "main")
    }

    private fun verifyDirsContentsAreEqual(dir1: File, dir2: File) {
        val dir1Files = dir1.listFiles()?.asList() ?: emptyList()
        val dir2Files = dir2.listFiles()?.asList() ?: emptyList()
        if (dir1Files.isEmpty() && dir2Files.isEmpty()) {
            return
        }
        checkRootContentFileNames(dir1Files, dir2Files)
        dir1Files.forEach { dir1File ->
            if (dir1File.isFile) {
                checkIfFileIsInList(dir1File, dir2Files)
            } else {
                verifyDirsContentsAreEqual(dir1File, dir2Files.first { it.name == dir1File.name })
            }
        }
    }

    private fun checkRootContentFileNames(dirFiles1: List<File>, dirFiles2: List<File>) {
        val dirFileNames1 = dirFiles1.map { it.name }
        val dirFileNames2 = dirFiles2.map { it.name }
        Truth.assertThat(dirFileNames2).containsExactlyElementsIn(dirFileNames1)
    }

    private fun checkIfFileIsInList(file: File, list: List<File>) {
        val fileWithSameName = list.first { it.name == file.name }
        val generated = Input.fromFile(fileWithSameName)
        val control = Input.fromFile(file)
        val diff = compare(generated).withTest(control).ignoreWhitespace().build()
        if (diff.hasDifferences()) {
            println("Control:\n" + file.readText())
            println("Generated:\n" + fileWithSameName.readText())
            fail(diff.fullDescription())
        }
    }
}