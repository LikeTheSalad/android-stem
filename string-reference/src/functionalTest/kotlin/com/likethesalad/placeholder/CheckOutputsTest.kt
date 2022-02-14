package com.likethesalad.placeholder

import com.google.common.truth.Truth
import com.likethesalad.tools.functional.testing.AndroidProjectTest
import com.likethesalad.tools.functional.testing.app.layout.AndroidAppProjectDescriptor
import com.likethesalad.tools.functional.testing.app.layout.AndroidBlockItem
import com.likethesalad.tools.functional.testing.app.layout.items.DefaultConfigAndroidBlockItem
import com.likethesalad.tools.functional.testing.app.layout.items.FlavorAndroidBlockItem
import com.likethesalad.tools.functional.testing.layout.AndroidLibProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.ProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.items.GradleBlockItem
import com.likethesalad.tools.functional.testing.layout.items.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import org.gradle.testkit.runner.BuildResult
import org.junit.Test
import java.io.File

class CheckOutputsTest : AndroidProjectTest() {

    companion object {
        private const val RESOLVER_PLUGIN_ID = "placeholder-resolver"
        private const val PROVIDER_PLUGIN_ID = "resource.templates.provider"
        private const val ANDROID_PLUGIN_VERSION = "7.1.0"
        private const val GRADLE_VERSION = "7.2"
    }

    private val inputAssetsProvider = TestAssetsProvider("functionalTest", "inputs")
    private val outputAssetsProvider = TestAssetsProvider("functionalTest", "outputs")

    @Test
    fun `verify basic app outputs`() {
        runInputOutputComparisonTest("basic", listOf("debug"))
    }

    @Test
    fun `verify nothing happens when there are no templates available`() {
        val variantNames = listOf("debug")
        val inOutDirName = "no-templates-available"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val commandList = variantNamesToResolveCommands(variantNames)
        createProject(descriptor)

        val result = buildProject(commandList, inOutDirName)

        verifyResultContainsText(
            result, """
            > Task :$inOutDirName:templatesDebugIdentifier
            > Task :$inOutDirName:gatherDebugStringTemplates
            > Task :$inOutDirName:resolveDebugPlaceholders NO-SOURCE
        """.trimIndent()
        )
        verifyEmptyOutput(inOutDirName, "debug")
    }

    @Test
    fun `verify prevous outputs cleared when templates are deleted afterwards`() {
        val variantNames = listOf("debug")
        val projectName = "no-templates-available-afterwards"
        val withTemplatesDir = "basic"
        val descriptor = createAndroidAppProjectDescriptor(projectName, withTemplatesDir)
        val commandList = variantNamesToResolveCommands(variantNames)
        createProject(descriptor)

        val result = buildProject(commandList, projectName)

        verifyResultContainsText(
            result, """
            > Task :$projectName:templatesDebugIdentifier
            > Task :$projectName:gatherDebugStringTemplates
            > Task :$projectName:resolveDebugPlaceholders
        """.trimIndent()
        )
        verifyVariantResults(variantNames, projectName, withTemplatesDir)

        // After removing templates:
        val withoutTemplatesDir = "no-templates-available"
        val descriptor2 = createAndroidAppProjectDescriptor(projectName, withoutTemplatesDir)
        val projectDir = getProjectDir(projectName)
        descriptor.projectDirBuilder.clearFilesCreated()
        descriptor2.projectDirBuilder.buildDirectory(projectDir)

        buildProject(commandList, projectName)

        verifyEmptyOutput(projectName, "debug")
    }

    @Test
    fun `verify basic app outputs are generated only once if the inputs don't change`() {
        val variantNames = listOf("debug")
        val inOutDirName = "basic-repeated"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val commandList = variantNamesToResolveCommands(variantNames)

        createProject(descriptor)
        val result1 = buildProject(commandList, inOutDirName)
        verifyResultContainsText(
            result1, """
            > Task :basic-repeated:templatesDebugIdentifier
            > Task :basic-repeated:gatherDebugStringTemplates
            > Task :basic-repeated:resolveDebugPlaceholders
        """.trimIndent()
        )

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)

        // Second time
        val result2 = buildProject(commandList, inOutDirName)

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)
        verifyResultContainsText(
            result2, """
            > Task :basic-repeated:templatesDebugIdentifier UP-TO-DATE
            > Task :basic-repeated:gatherDebugStringTemplates UP-TO-DATE
            > Task :basic-repeated:resolveDebugPlaceholders UP-TO-DATE
        """.trimIndent()
        )
    }

    @Test
    fun `verify multi languages clean up after changes`() {
        val variantNames = listOf("debug")
        val inOutDirName = "multi-languages-changed-before"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val commandList = variantNamesToResolveCommands(variantNames)

        createProject(descriptor)
        val result1 = buildProject(commandList, inOutDirName)
        verifyResultContainsText(
            result1, """
            > Task :$inOutDirName:templatesDebugIdentifier
            > Task :$inOutDirName:gatherDebugStringTemplates
            > Task :$inOutDirName:resolveDebugPlaceholders
        """.trimIndent()
        )

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)

        // Second time
        val dirName2 = "multi-languages-changed-after"
        val descriptor2 = createAndroidAppProjectDescriptor(inOutDirName, dirName2)
        val projectDir = getProjectDir(inOutDirName)
        descriptor.projectDirBuilder.clearFilesCreated()
        descriptor2.projectDirBuilder.buildDirectory(projectDir)

        val result2 = buildProject(commandList, inOutDirName)

        verifyVariantResults(variantNames, inOutDirName, dirName2)
        verifyResultContainsText(
            result2, """
            > Task :$inOutDirName:templatesDebugIdentifier
            > Task :$inOutDirName:gatherDebugStringTemplates
            > Task :$inOutDirName:resolveDebugPlaceholders
        """.trimIndent()
        )
    }

    @Test
    fun `verify multi-languages app outputs`() {
        runInputOutputComparisonTest(
            "multi-languages", listOf("debug")
        )
    }

    @Test
    fun `verify app with gradle-generated strings outputs`() {
        val appName = "with-gradle-strings"
        val gradleStrings = mapOf("my_app_id" to "APP ID")
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            androidBlockItems = listOf(DefaultConfigAndroidBlockItem(gradleStrings))
        )

        runInputOutputComparisonTest(appName, listOf("debug"), appDescriptor)
    }

    @Test
    fun `verify flavored app outputs`() {
        val inOutDirName = "flavored-app"
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
            inOutDirName,
            listOf(
                "fullStableDebug",
                "demoStableDebug",
                "fullProdDebug",
                "demoProdDebug"
            ),
            flavoredDescriptor
        )
    }

    @Test
    fun `verify app that takes resources from a library`() {
        // Create library
        val libName = "mylibrary"
        val libDescriptor = createAndroidLibProjectDescriptor(libName)
        libDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration(PROVIDER_PLUGIN_ID))
        libDescriptor.dependenciesBlock.addDependency("implementation 'com.android.support:recyclerview-v7:28.0.0'")
        createProject(libDescriptor)

        // Set up app
        val appName = "with-library"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf("implementation project(':$libName')")
        )

        runInputOutputComparisonTest(appName, listOf("debug"), appDescriptor)
    }

    @Test
    fun `verify app that takes resources from an aar file`() {
        val appName = "with-aar-file-library"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf("implementation fileTree(dir: 'src/libs', include: ['*.aar'])")
        )

        runInputOutputComparisonTest(appName, listOf("debug"), appDescriptor)
    }

    @Test
    fun `verify app that takes resources from both local and external libraries`() {
        // Create library
        val libName = "my_local_library"
        val libDescriptor = createAndroidLibProjectDescriptor(libName)
        libDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration(PROVIDER_PLUGIN_ID))
        createProject(libDescriptor)

        // Set up app
        val appName = "with-aar-and-local-libraries"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf(
                "implementation fileTree(dir: 'src/libs', include: ['*.aar'])",
                "implementation project(':$libName')"
            )
        )

        runInputOutputComparisonTest(appName, listOf("debug"), appDescriptor)
    }

    @Test
    fun `verify app that takes resources from multiple libraries`() {
        // Create library
        val libName1 = "my_first_library"
        val libName2 = "my_other_library"
        setUpLibraryModule(libName1)
        setUpLibraryModule(libName2)

        // Set up app
        val appName = "with-multiple-libraries"
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf(
                "implementation project(':$libName1')",
                "implementation project(':$libName2')"
            )
        )

        runInputOutputComparisonTest(appName, listOf("debug"), appDescriptor)
    }

    private fun setUpLibraryModule(libName: String) {
        val libDescriptor = createAndroidLibProjectDescriptor(libName)
        libDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration(PROVIDER_PLUGIN_ID))
        createProject(libDescriptor)
    }

    private fun runInputOutputComparisonTest(
        inOutDirName: String,
        variantNames: List<String>,
        descriptor: AndroidAppProjectDescriptor = createAndroidAppProjectDescriptor(inOutDirName)
    ) {
        createProjectAndRunStringResolver(descriptor, variantNames)

        verifyVariantResults(variantNames, inOutDirName, inOutDirName)
    }

    private fun createAndroidLibProjectDescriptor(
        libName: String,
        inputDirName: String = libName
    ): AndroidLibProjectDescriptor {
        val inputDir = getInputTestAsset(inputDirName)
        return AndroidLibProjectDescriptor(libName, inputDir, ANDROID_PLUGIN_VERSION)
    }

    private fun createAndroidAppProjectDescriptor(
        projectName: String,
        inputDirName: String = projectName,
        androidBlockItems: List<AndroidBlockItem> = emptyList(),
        dependencies: List<String> = emptyList()
    ): AndroidAppProjectDescriptor {
        val inputDir = getInputTestAsset(inputDirName)
        val blockItems = mutableListOf<GradleBlockItem>()
        blockItems.addAll(androidBlockItems)
        val descriptor =
            AndroidAppProjectDescriptor(projectName, inputDir, ANDROID_PLUGIN_VERSION, blockItems)
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

    private fun createProjectAndRunStringResolver(
        projectDescriptor: ProjectDescriptor,
        variantNames: List<String>
    ): BuildResult {
        val commandList = variantNamesToResolveCommands(variantNames)
        return createProjectAndBuild(projectDescriptor, commandList)
    }

    private fun variantNamesToResolveCommands(variantNames: List<String>) =
        variantNames.map { "resolve${it.capitalize()}Placeholders" }

    private fun getInputTestAsset(inputDirName: String): File {
        return inputAssetsProvider.getAssetFile(inputDirName)
    }

    private fun getOutputTestAsset(outputDirName: String): File {
        return outputAssetsProvider.getAssetFile(outputDirName)
    }

    private fun verifyExpectedOutput(
        projectName: String,
        outputDirName: String,
        variantName: String
    ) {
        val projectDir = getProjectDir(projectName)
        val resultDir = File(projectDir, "build/generated/resolved/$variantName")
        Truth.assertThat(resultDir.exists()).isTrue()
        verifyDirsContentsAreEqual(getExpectedOutputDir(outputDirName, variantName), resultDir)
    }

    private fun verifyEmptyOutput(projectName: String, variantName: String) {
        val projectDir = getProjectDir(projectName)
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
        Truth.assertThat(fileWithSameName.readText()).isEqualTo(file.readText())
    }

    override fun getGradleVersion(): String = GRADLE_VERSION
}