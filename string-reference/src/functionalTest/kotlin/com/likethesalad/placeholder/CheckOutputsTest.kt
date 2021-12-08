package com.likethesalad.placeholder

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.ResolverPluginConfig
import com.likethesalad.placeholder.data.ResolverPluginConfigBlock
import com.likethesalad.tools.functional.testing.AndroidProjectTest
import com.likethesalad.tools.functional.testing.app.content.ValuesResFoldersPlacer
import com.likethesalad.tools.functional.testing.app.layout.AndroidAppProjectDescriptor
import com.likethesalad.tools.functional.testing.app.layout.AndroidBlockItem
import com.likethesalad.tools.functional.testing.app.layout.GradleBlockItem
import com.likethesalad.tools.functional.testing.app.layout.items.DefaultConfigAndroidBlockItem
import com.likethesalad.tools.functional.testing.app.layout.items.FlavorAndroidBlockItem
import com.likethesalad.tools.functional.testing.layout.AndroidLibProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.ProjectDescriptor
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import org.gradle.testkit.runner.BuildResult
import org.junit.Test
import java.io.File

class CheckOutputsTest : AndroidProjectTest() {

    companion object {
        private const val PLUGIN_ID = "placeholder-resolver"
    }

    private val inputAssetsProvider = TestAssetsProvider("functionalTest", "inputs")
    private val outputAssetsProvider = TestAssetsProvider("functionalTest", "outputs")

    @Test
    fun `verify basic app outputs`() {
        runInputOutputComparisonTest("basic", listOf("debug"))
    }

    @Test
    fun `verify basic app outputs are generated only once if the inputs don't change`() {
        val variantNames = listOf("debug")
        val inOutDirName = "basic-repeated"
        val descriptor = createAndroidAppProjectDescriptor(inOutDirName)
        val inputDir = getInputTestAsset(inOutDirName)
        descriptor.projectDirectoryBuilder.register(ValuesResFoldersPlacer(inputDir))
        val commandList = variantNamesToResolveCommands(variantNames)

        createProject(descriptor)
        val result1 = buildProject(commandList, inOutDirName)
        verifyResultContainsText(result1, """
            > Task :basic-repeated:gatherDebugStringTemplates
            > Task :basic-repeated:resolveDebugPlaceholders
        """.trimIndent())

        verifyVariantResults(variantNames, inOutDirName)

        // Second time
        val result2 = buildProject(commandList, inOutDirName)

        verifyVariantResults(variantNames, inOutDirName)
        verifyResultContainsText(result2, """
            > Task :basic-repeated:gatherDebugStringTemplates UP-TO-DATE
            > Task :basic-repeated:resolveDebugPlaceholders UP-TO-DATE
        """.trimIndent())
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
            listOf(DefaultConfigAndroidBlockItem(gradleStrings))
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
            listOf(FlavorAndroidBlockItem(flavors))
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
    fun `verify app that takes resources from libraries`() {
        // Create library
        val libName = "mylibrary"
        val libDescriptor = AndroidLibProjectDescriptor(libName)
        libDescriptor.projectDirectoryBuilder
            .register(ValuesResFoldersPlacer(getInputTestAsset(libName)))
        createProject(libDescriptor)

        // Set up app
        val appName = "with-library"
        val appConfig = ResolverPluginConfig(useDependenciesRes = true)
        val appDescriptor = createAndroidAppProjectDescriptor(
            appName,
            dependencies = listOf("implementation project(':$libName')"),
            resolverPluginConfig = appConfig
        )

        runInputOutputComparisonTest(appName, listOf("debug"), appDescriptor)
    }

    private fun runInputOutputComparisonTest(
        inOutDirName: String,
        variantNames: List<String>,
        descriptor: AndroidAppProjectDescriptor = createAndroidAppProjectDescriptor(inOutDirName)
    ) {
        val inputDir = getInputTestAsset(inOutDirName)
        descriptor.projectDirectoryBuilder.register(ValuesResFoldersPlacer(inputDir))

        createProjectAndRunStringResolver(descriptor, variantNames)

        verifyVariantResults(variantNames, inOutDirName)
    }

    private fun createAndroidAppProjectDescriptor(
        inOutDirName: String,
        androidBlockItems: List<AndroidBlockItem> = emptyList(),
        dependencies: List<String> = emptyList(),
        resolverPluginConfig: ResolverPluginConfig = ResolverPluginConfig()
    ): AndroidAppProjectDescriptor {
        val blockItems = mutableListOf<GradleBlockItem>(ResolverPluginConfigBlock(resolverPluginConfig))
        blockItems.addAll(androidBlockItems)
        return AndroidAppProjectDescriptor(inOutDirName, PLUGIN_ID, blockItems, dependencies)
    }

    private fun verifyVariantResults(variantNames: List<String>, inOutDirName: String) {
        variantNames.forEach {
            verifyExpectedOutput(inOutDirName, it)
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
        inOutDirName: String,
        variantName: String
    ) {
        val projectDir = getProjectDir(inOutDirName)
        val resultDir = File(projectDir, "build/generated/resolved/$variantName")
        Truth.assertThat(resultDir.exists()).isTrue()
        verifyDirsContentsAreEqual(getExpectedOutputDir(inOutDirName, variantName), resultDir)
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
        Truth.assertThat(dirFileNames1).containsExactlyElementsIn(dirFileNames2)
    }

    private fun checkIfFileIsInList(file: File, list: List<File>) {
        val fileWithSameName = list.first { it.name == file.name }
        Truth.assertThat(fileWithSameName.readText()).isEqualTo(file.readText())
    }

    override fun getGradleVersion(): String = "5.6.4"
}