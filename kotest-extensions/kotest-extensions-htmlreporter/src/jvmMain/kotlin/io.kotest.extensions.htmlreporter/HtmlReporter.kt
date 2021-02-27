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
import kotlin.io.path.writeText

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

      val summaryList: MutableList<List<String>> = mutableListOf()

      File(DefaultResultsLocation)
         .walk()
         .filter { Files.isRegularFile(it.toPath())}
         .filter { it.toString().endsWith(".xml") }
         .forEach {
         val builder = SAXBuilder()
         val doc = builder.build(it.path)
         val root = doc.rootElement

         summaryList.add(
            listOf(
               root.getAttributeValue("name"),
               root.getAttributeValue("tests"),
               root.getAttributeValue("errors"),
               root.getAttributeValue("failures"),
               root.getAttributeValue("skipped")
            )
         )

         write(
            buildClassDocument(root.getAttributeValue("name"), root.getChildren("testcase")),
            "classes/${root.getAttributeValue("name")}.html"
         )
      }

      write(buildSummaryDocument(summaryList), "index.html")
      write({}.javaClass.getResource("/style.css").readText(), "css/style.css")
   }

   private fun withDocument(block: (Element) -> Unit): Document {
      val document = Document()
      document.docType = DocType("html")

      val html = Element("html")
      val head = Element("head")
      val body = Element("body")

      head.addContent(
         Element("link")
            .setAttribute("rel", "stylesheet")
            .setAttribute("href", outputDir().resolve("css/style.css").toString())
      )

      block(body)

      html.addContent(head)
      html.addContent(body)
      document.addContent(html)

      return document
   }

   private fun buildSummaryDocument(summaryList: MutableList<List<String>>): Document {
      return withDocument { body ->
         body.addContent(Element("h1").setContent(Text("Test Summary")))
         body.addContent(
            buildSummaryTable(
               listOf("Class", "Tests", "Errors", "Failures", "Skipped"),
               summaryList
            )
         )
      }
   }

   private fun buildClassDocument(name: String, testcases: List<Element>): Document {
      return withDocument { body ->
         body.addContent(Element("h1").setContent(Text("Class $name")))
         body.addContent(Element("a").setText("Home").setAttribute("href", "../index.html"))
         body.addContent(buildTestsTable(testcases))
      }
   }

   private fun addColumn(row: Element, doc: Document, attributeName: String) = row.addContent(Element("td").setContent(Text(doc.rootElement.getAttributeValue(attributeName))))

   private fun addHeaderColumn(row: Element, value: String) = row.addContent(Element("th").setContent(Text(value)))

   private fun buildSummaryTable(headers: List<String>, content: List<List<String>>): Element {
      val table = Element("table")
      val headerRow = Element("tr")


      headers.forEach {
         addHeaderColumn(headerRow, it)
      }

      table.addContent(headerRow)

      content.forEach {
         val row = Element("tr")
         val anchor = Element("a").setContent(Text(it[0])).setAttribute("href", "./classes/${it[0]}.html")

         val tests = it[1].toIntOrNull() ?: 0
         val errors = it[2].toIntOrNull() ?: 0
         val failures = it[3].toIntOrNull() ?: 0
         val skipped = it[4].toIntOrNull() ?: 0

         row.addContent(Element("td").setContent(anchor))
         row.addContent(Element("td").setContent(Text(tests.toString())))
         row.addContent(Element("td").setContent(Text(errors.toString())))
         row.addContent(Element("td").setContent(Text(failures.toString())))
         row.addContent(Element("td").setContent(Text(skipped.toString())))

         if((errors + failures + skipped) == 0) row.setAttribute("class", "success")
         if (failures > 0) row.setAttribute("class", "failure")

         table.addContent(row)
      }

      return table
   }

   private fun buildTestsTable(testcases: List<Element>): Element {
      val table = Element("table")
      val headerRow = Element("tr")

      listOf("Test", "Duration", "Result").forEach {
         addHeaderColumn(headerRow, it)
      }

      table.addContent(headerRow)

      testcases.forEach {
         val row = Element("tr")
         val result = if(it.getChild("failure") != null) "Failed" else "Passed"

         row.addContent(Element("td").setContent(Text(it.getAttributeValue("name"))))
         row.addContent(Element("td").setContent(Text(it.getAttributeValue("time"))))
         row.addContent(Element("td").setContent(Text(result)))

         if (result == "Passed") {
            row.setAttribute("class", "success")
         } else {
            row.setAttribute("class", "failure")
         }

         table.addContent(row)
      }

      return table
   }

   private fun outputDir(): Path {
      val buildDir = System.getProperty(BuildDirKey)
      return if (buildDir != null)
         Paths.get(buildDir).resolve(outputDir)
      else
         Paths.get(DefaultLocation)
   }

   private fun write(document: Document, path: String) {
      val path = outputDir().resolve(path)
      path.parent.toFile().mkdirs()
      val outputter = XMLOutputter(Format.getPrettyFormat().setOmitDeclaration(true))
      val writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
      outputter.output(document, writer)
      writer.close()
   }

   private fun write(text: String, path: String) {
      val path = outputDir().resolve(path)
      path.parent.toFile().mkdirs()
      File(path.toUri()).writeText(text)
   }
}

