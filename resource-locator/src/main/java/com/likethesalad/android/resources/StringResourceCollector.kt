package com.likethesalad.android.resources

import com.likethesalad.android.resources.collector.AndroidXmlResDocument
import com.likethesalad.android.resources.collector.StringXmlResourceExtractor
import com.likethesalad.android.resources.data.StringResource
import com.likethesalad.android.resources.data.ValueDir
import java.io.File

object StringResourceCollector {

    private val XML_PATTERN = Regex(".+\\.[xX][mM][lL]\$")
    private val VALUES_FOLDER_NAME_PATTERN = Regex("values(-.+)*")

    fun collectStringResources(resDirCollections: List<Collection<File>>): Map<String, Collection<StringResource>> {
        val stringResources = mutableMapOf<String, MutableMap<String, StringResource>>()

        resDirCollections.forEach { collection ->
            val collectionValues = mutableMapOf<String, MutableMap<String, StringResource>>()
            collection.forEach { resDir ->
                findValueDirs(resDir).forEach { valueDir ->
                    val valueResources = collectionValues.getOrPut(valueDir.name, ::mutableMapOf)
                    findXmlFiles(valueDir.dir).forEach { xmlFile ->
                        extractResources(xmlFile).forEach { stringResource ->
                            val stringName = stringResource.getName()
                            if (stringName in valueResources) {
                                throw IllegalStateException("Duplicate name '$stringName' in '${valueDir.name}'")
                            }
                            valueResources[stringName] = stringResource
                        }
                    }
                }
            }

            // Merge collection into global resources
            collectionValues.forEach { (valuesName, valueResource) ->
                val globalResources = stringResources.getOrPut(valuesName, ::mutableMapOf)
                valueResource.forEach { (stringName, stringResource) ->
                    globalResources[stringName] = stringResource
                }
            }
        }

        return stringResources.map { it.key to it.value.values }.toMap()
    }

    private fun findValueDirs(resDir: File): List<ValueDir> {
        val valueFolders = resDir.listFiles { _, name ->
            VALUES_FOLDER_NAME_PATTERN.matches(name)
        }?.filter { it.isDirectory } ?: emptyList<File>()

        return valueFolders.map {
            ValueDir(it.name, it)
        }
    }

    private fun findXmlFiles(valueDir: File): List<File> {
        return valueDir.listFiles { _, name ->
            XML_PATTERN.matches(name)
        }?.toList() ?: emptyList()
    }

    private fun extractResources(xmlFile: File): List<StringResource> {
        return StringXmlResourceExtractor.getResourcesFromAndroidDocument(AndroidXmlResDocument.fromFile(xmlFile))
    }
}