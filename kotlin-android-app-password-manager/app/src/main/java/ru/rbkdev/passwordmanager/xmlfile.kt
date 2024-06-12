package ru.rbkdev.passwordmanager

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

data class DItem (
    var type: String,
    var title: String,
    var login: String,
    var password: String
)

object CXMLFile {

    fun read(path: String): ByteArray? {

        var result: ByteArray? = null

        val file: File = File(path)
        if(file.exists() == true)
            result = file.inputStream().readBytes()

        return result
    }

    fun write(path: String, data: ByteArray?): Boolean {

        val file: File = File(path)
        if(file.exists() == false) {

            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        file.outputStream().write(data)
        return true
    }

    fun parse(inData: ByteArray) : List<DItem> {

        val outData: MutableList<DItem> = mutableListOf()

        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder = factory.newDocumentBuilder()
        val doc: Document = builder.parse(inData.inputStream())

        val root: Element = doc.documentElement
        val listRoot: NodeList = root.childNodes
        val sizeRoot: Int = listRoot.length
        for(i in 0 until sizeRoot) {

            val nodeBlock: Node = listRoot.item(i)
            if(nodeBlock.nodeType == Node.ELEMENT_NODE) {

                val block: Element = nodeBlock as Element
                outData.add(DItem(
                    "block",
                    block.getAttribute("title"),
                    block.getAttribute("login"),
                    block.getAttribute("password")
                ))

                val listBlock: NodeList = block.childNodes
                val sizeBlock: Int = listBlock.length
                for(j in 0 until sizeBlock) {

                    val nodeItem = listBlock.item(j)
                    if(nodeItem.nodeType == Node.ELEMENT_NODE) {

                        val item = nodeItem as Element
                        outData.add(DItem(
                            "item",
                            item.getAttribute("title"),
                            item.getAttribute("login"),
                            item.getAttribute("password")
                        ))
                    }
                }
            }
        }

        return  outData
    }

    fun collect(inData: List<DItem>): ByteArray {

        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder = factory.newDocumentBuilder()
        val doc: Document = builder.newDocument()

        val root = doc.createElement("root")
        var block: Element? = null
        var item: Element? = null

        inData.forEach { it ->

            if(it.type == "block") {
                block = doc.createElement(it.type)
                block?.let { block ->
                    block.setAttribute("title", it.title)
                    block.setAttribute("login", it.login)
                    block.setAttribute("password", it.password)
                    root.appendChild(block)
                }
            }

            if(block != null && it.type == "item") {
                item = doc.createElement(it.type)
                item?.let {item ->
                    item.setAttribute("title", it.title)
                    item.setAttribute("login", it.login)
                    item.setAttribute("password", it.password)
                    block?.appendChild(item)
                }
            }
        }
        doc.appendChild(root)

        val outStream: ByteArrayOutputStream = ByteArrayOutputStream()
        val result: StreamResult = StreamResult(outStream)

        val transformerFactory: TransformerFactory = TransformerFactory.newInstance()
        val transformer: Transformer = transformerFactory.newTransformer()
        val source: DOMSource = DOMSource(doc)

        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.transform(source, result)

        return outStream.toByteArray()
    }
}