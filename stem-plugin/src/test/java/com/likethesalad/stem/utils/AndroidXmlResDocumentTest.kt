package com.likethesalad.stem.utils

import com.google.common.truth.Truth
import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.modules.common.helpers.files.AndroidXmlResDocument
import com.likethesalad.stem.testutils.named
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class AndroidXmlResDocumentTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun checkEmptyConstructor_should_create_resources() {
        // When
        val androidXmlResDocument = AndroidXmlResDocument()

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
            StringResource.named(
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
        val stringResources = listOf<StringResource>(mockk(), mockk(), mockk(), mockk(), mockk())
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
    fun checkSaveToFile() {
        // Given
        val androidXmlResDocument =
            AndroidXmlResDocument()
        val file = temporaryFolder.newFile()
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name1",
                "some content1"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name2",
                "some content2"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name3",
                "some content3"
            )
        )

        // When
        androidXmlResDocument.saveToFile(file)

        // Then
        Truth.assertThat(file.readText()).isEqualTo(
            """
            <resources><string name="some_name1">some content1</string><string name="some_name2">some content2</string><string name="some_name3">some content3</string></resources>
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
            StringResource.named(
                "some_name1",
                "some content1"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name2",
                "some content2"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name3",
                "some content3"
            )
        )

        // When
        androidXmlResDocument.saveToFile(file)

        // Then
        Truth.assertThat(file.readText()).isEqualTo(
            """
            <resources><string name="some_name1">some content1</string><string name="some_name2">some content2</string><string name="some_name3">some content3</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun `verify custom prefixes`() {
        val document = AndroidXmlResDocument()
        val outputFile = temporaryFolder.newFile()
        val namespace = "http://some.namespace.com/"
        val resource = StringResource.named(
            "someName", "someValue",
            listOf(Attribute("someAttr", "someAttrValue", namespace))
        )
        document.appendStringResource(resource)

        document.saveToFile(outputFile)

        Truth.assertThat(outputFile.readText()).isEqualTo(
            """
                <resources xmlns:ns1="$namespace"><string name="someName" ns1:someAttr="someAttrValue">someValue</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun `verify custom prefixes reused`() {
        val document = AndroidXmlResDocument()
        val outputFile = temporaryFolder.newFile()
        val namespace = "http://some.namespace.com/"
        val resource = StringResource.named(
            "someName", "someValue", listOf(
                Attribute("someAttr", "someAttrValue", namespace),
                Attribute("someAttr2", "someOtherAttrValue", namespace)
            )
        )
        val resource2 = StringResource.named(
            "someOtherName", "someOtherValue", listOf(
                Attribute("someAttr3", "Some attr3 value", namespace)
            )
        )
        document.appendStringResource(resource)
        document.appendStringResource(resource2)

        document.saveToFile(outputFile)

        Truth.assertThat(outputFile.readText()).isEqualTo(
            """
                <resources xmlns:ns1="$namespace"><string name="someName" ns1:someAttr="someAttrValue" ns1:someAttr2="someOtherAttrValue">someValue</string><string name="someOtherName" ns1:someAttr3="Some attr3 value">someOtherValue</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun `verify multipl prefixes`() {
        val document = AndroidXmlResDocument()
        val outputFile = temporaryFolder.newFile()
        val namespace = "http://some.namespace.com/"
        val namespace2 = "http://someother.namespace.com/"
        val resource = StringResource.named(
            "someName", "someValue", listOf(
                Attribute("someAttr", "someAttrValue", namespace),
                Attribute("someAttr2", "someOtherAttrValue", namespace)
            )
        )
        val resource2 = StringResource.named(
            "someOtherName", "someOtherValue", listOf(
                Attribute("someAttr3", "Some attr3 value", namespace),
                Attribute("someAttr3", "Some attr3 namespace2 value", namespace2)
            )
        )
        document.appendStringResource(resource)
        document.appendStringResource(resource2)

        document.saveToFile(outputFile)

        Truth.assertThat(outputFile.readText()).isEqualTo(
            """
                <resources xmlns:ns1="$namespace" xmlns:ns2="$namespace2"><string name="someName" ns1:someAttr="someAttrValue" ns1:someAttr2="someOtherAttrValue">someValue</string><string name="someOtherName" ns1:someAttr3="Some attr3 value" ns2:someAttr3="Some attr3 namespace2 value">someOtherValue</string></resources>
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