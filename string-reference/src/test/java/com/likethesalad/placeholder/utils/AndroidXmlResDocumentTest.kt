package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.modules.common.models.StringResourceModel
import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.resources.utils.XmlUtils
import com.likethesalad.placeholder.modules.common.helpers.files.AndroidXmlResDocument
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.w3c.dom.*
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class AndroidXmlResDocumentTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun checkEmptyConstructor_should_create_resources() {
        // When
        val androidXmlResDocument =
            AndroidXmlResDocument()

        // Then
        val resourcesList = androidXmlResDocument.document.getElementsByTagName("resources")
        Truth.assertThat(resourcesList.length).isEqualTo(1)
        Truth.assertThat(androidXmlResDocument.document.xmlStandalone).isTrue()
    }

    @Test
    fun checkDocumentConstructor_should_get_resources() {
        // Given
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val resources = document.createElement("resources")
        document.appendChild(resources)

        // When
        val androidXmlResDocument =
            AndroidXmlResDocument(document)

        // Then
        Truth.assertThat(androidXmlResDocument.resources).isEqualTo(resources)
    }

    @Test
    fun checkReadFromFile() {
        // Given
        val file: File = temporaryFolder.newFile()
        file.writeText(
            """
            <resources>
                <string name="application_name">Some name</string>
                <string name="application_welcome">Welcome to the app</string>
            </resources>
        """
        )

        // When
        val result = AndroidXmlResDocument.readFromFile(file)

        // Then
        val xmlStrings = result.resources.getElementsByTagName("string")
        val stringList = mutableListOf<StringResourceModel>()
        for (it in 0 until xmlStrings.length) {
            stringList.add(XmlUtils.nodeToStringResourceModel(xmlStrings.item(it)))
        }
        Truth.assertThat(stringList.size).isEqualTo(2)
        Truth.assertThat(stringList.map { it.name }).containsExactly("application_name", "application_welcome")
    }

    @Test
    fun checkAppend() {
        // Given
        val itemMock: Element = mockk()
        val resourcesMock: Element = mockk(relaxed = true)
        val documentMock: Document = mockk()
        val nodeListResourcesMock: NodeList = mockk()
        every { nodeListResourcesMock.length }.returns(1)
        every { nodeListResourcesMock.item(0) }.returns(resourcesMock)
        every { documentMock.getElementsByTagName("resources") }.returns(nodeListResourcesMock)

        val androidXmlResDocument =
            AndroidXmlResDocument(
                documentMock
            )

        // When
        androidXmlResDocument.append(itemMock)

        // Then
        verify { resourcesMock.appendChild(itemMock) }
        verify(exactly = 0) { documentMock.importNode(any(), any()) }
    }

    @Test
    fun checkAppend_importNode() {
        // Given
        val itemMock: Element = mockk()
        val importedItemMock: Element = mockk()
        val resourcesMock: Element = mockk(relaxed = true)
        val documentMock: Document = mockk()
        val nodeListResourcesMock: NodeList = mockk()
        every { documentMock.importNode(itemMock, true) }.returns(importedItemMock)
        every { nodeListResourcesMock.length }.returns(1)
        every { nodeListResourcesMock.item(0) }.returns(resourcesMock)
        every { documentMock.getElementsByTagName("resources") }.returns(nodeListResourcesMock)
        every { resourcesMock.appendChild(itemMock) } throws DOMException(0, "")

        val androidXmlResDocument =
            AndroidXmlResDocument(
                documentMock
            )

        // When
        androidXmlResDocument.append(itemMock)

        // Then
        verify { resourcesMock.appendChild(importedItemMock) }
        verify(exactly = 1) { documentMock.importNode(itemMock, true) }
    }

    @Test
    fun checkAppendStringResource() {
        // Given
        val stringResourceModel =
            StringResourceModel(
                "some_name",
                "some content"
            )
        val androidXmlResDocumentSpy: AndroidXmlResDocument = spyk(
            AndroidXmlResDocument()
        )
        every { androidXmlResDocumentSpy.append(any()) } just Runs

        // When
        androidXmlResDocumentSpy.appendStringResource(stringResourceModel)

        // Then
        verify { androidXmlResDocumentSpy.append(any()) }
    }

    @Test
    fun checkAppendAll() {
        // Given
        val androidXmlResDocumentSpy: AndroidXmlResDocument = spyk(
            AndroidXmlResDocument()
        )
        val nodeList: NodeList = mockk()
        every { nodeList.length }.returns(4)
        every { nodeList.item(any()) }.returns(mockk())
        every { androidXmlResDocumentSpy.append(any()) } just Runs

        // When
        androidXmlResDocumentSpy.appendAll(nodeList)

        // Then
        verify(exactly = 4) { androidXmlResDocumentSpy.append(any()) }
    }

    @Test
    fun checkAppendAllStringResources() {
        // Given
        val androidXmlResDocumentSpy: AndroidXmlResDocument = spyk(
            AndroidXmlResDocument()
        )
        val stringResources = listOf<StringResourceModel>(mockk(), mockk(), mockk(), mockk(), mockk())
        every { androidXmlResDocumentSpy.appendStringResource(any()) } just Runs

        // When
        androidXmlResDocumentSpy.appendAllStringResources(stringResources)

        // Then
        verify(exactly = 5) { androidXmlResDocumentSpy.appendStringResource(any()) }
    }

    @Test
    fun checkGetStringList() {
        // Given
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val resources: Element = document.createElement("resources")
        document.appendChild(resources)
        resources.run {
            appendChild(createStringNode("some_name", "some content", document))
            appendChild(createStringNode("some_name2", "some content2", document))
            appendChild(createStringNode("some_name3", "some content3", document))
        }
        val androidXmlResDocument =
            AndroidXmlResDocument(document)

        // When
        val result = androidXmlResDocument.getStringList()

        // Then
        Truth.assertThat(result.length).isEqualTo(3)
        Truth.assertThat(result.item(0).attributes.getNamedItem("name").textContent)
            .isEqualTo("some_name")
        Truth.assertThat(result.item(1).attributes.getNamedItem("name").textContent)
            .isEqualTo("some_name2")
        Truth.assertThat(result.item(2).attributes.getNamedItem("name").textContent)
            .isEqualTo("some_name3")
    }

    @Test
    fun checkGetStringResourceList() {
        // Given
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val androidXmlResDocumentSpy: AndroidXmlResDocument = spyk(
            AndroidXmlResDocument(document)
        )
        val nodeListMock: NodeList = mockk()
        every { nodeListMock.length }.returns(5)
        every { nodeListMock.item(any()) }.returns(createStringNode("some_name", "some content", document))
        every { androidXmlResDocumentSpy.getStringList() }.returns(nodeListMock)

        // When
        val result = androidXmlResDocumentSpy.getStringResourceList()

        // Then
        Truth.assertThat(result.size).isEqualTo(5)
        Truth.assertThat(result.map { it.name })
            .containsExactly("some_name", "some_name", "some_name", "some_name", "some_name")
    }

    @Test
    fun checkSaveToFile() {
        // Given
        val androidXmlResDocument =
            AndroidXmlResDocument()
        val file = temporaryFolder.newFile()
        androidXmlResDocument.appendStringResource(
            StringResourceModel(
                "some_name1",
                "some content1"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResourceModel(
                "some_name2",
                "some content2"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResourceModel(
                "some_name3",
                "some content3"
            )
        )

        // When
        androidXmlResDocument.saveToFile(file)

        // Then
        Truth.assertThat(file.readText()).isEqualTo(
            """
            <resources>
                <string name="some_name1">some content1</string>
                <string name="some_name2">some content2</string>
                <string name="some_name3">some content3</string>
            </resources>
            
            """.trimIndent()
        )
    }

    @Test
    fun checkSaveToFile_indent() {
        // Given
        val androidXmlResDocument =
            AndroidXmlResDocument()
        val file = temporaryFolder.newFile()
        androidXmlResDocument.appendStringResource(
            StringResourceModel(
                "some_name1",
                "some content1"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResourceModel(
                "some_name2",
                "some content2"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResourceModel(
                "some_name3",
                "some content3"
            )
        )

        // When
        androidXmlResDocument.saveToFile(file, 2)

        // Then
        Truth.assertThat(file.readText()).isEqualTo(
            """
            <resources>
              <string name="some_name1">some content1</string>
              <string name="some_name2">some content2</string>
              <string name="some_name3">some content3</string>
            </resources>
            
            """.trimIndent()
        )
    }

    private fun createStringNode(name: String, content: String, document: Document): Node {
        val node = document.createElement("string")
        node.setAttribute("name", name)
        node.textContent = content
        return node
    }
}