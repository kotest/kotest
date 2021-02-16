package io.kotest.extensions.htmlreporter

import io.kotest.core.listeners.ProjectListener
import org.jdom2.DocType
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Text
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class HtmlReporter(
   private val outputDir: String = "reports/tests/test"
) : ProjectListener {

   companion object {
      const val DefaultLocation = "./reports/tests/test/"

      const val DefaultResultsLocation = "./build/test-results/test"

      const val BuildDirKey = "gradle.build.dir"
   }

   override suspend fun afterProject() {
      super.afterProject()

      val document = Document()
      document.setDocType(DocType("html"))

      val html = Element("html")
      val body = Element("body")
      val table = Element("table")

      val headerRow = Element("tr")
      addHeaderColumn(headerRow, "Class")
      addHeaderColumn(headerRow, "Tests")
      addHeaderColumn(headerRow, "Errors")
      addHeaderColumn(headerRow, "Failures")
      addHeaderColumn(headerRow, "Skipped")

      table.addContent(headerRow)

      File(DefaultResultsLocation)
         .walk()
         .filter { Files.isRegularFile(it.toPath())}
         .filter { it.toString().endsWith(".xml") }
         .forEach {
         val builder = SAXBuilder()
         val doc = builder.build(it.path)
         val row = Element("tr")

         addHeaderColumn(row, doc, "name")
         addHeaderColumn(row, doc, "tests")
         addHeaderColumn(row, doc, "errors")
         addHeaderColumn(row, doc, "failures")
         addHeaderColumn(row, doc, "skipped")

         table.addContent(row)
      }

      body.addContent(Element("h1").setContent(Text("Test Summary")))

      body.addContent(table)
      html.addContent(body)
      document.addContent(html)

      write(document)
   }

   private fun outputDir(): Path {
      val buildDir = System.getProperty(BuildDirKey)
      return if (buildDir != null)
         Paths.get(buildDir).resolve(outputDir)
      else
         Paths.get(DefaultLocation)
   }

   private fun write(document: Document) {
      val path = outputDir().resolve("index.thml")
      path.parent.toFile().mkdirs()
      val outputter = XMLOutputter(Format.getPrettyFormat().setOmitDeclaration(true))
      val writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
      outputter.output(document, writer)
      writer.close()
   }

   private fun addHeaderColumn(row: Element, doc: Document, attributeName: String) = row.addContent(Element("td").setContent(Text(doc.rootElement.getAttributeValue(attributeName))))

   private fun addHeaderColumn(row: Element, value: String) = row.addContent(Element("th").setContent(Text(value)))

}

