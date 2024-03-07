package io.kotest.extensions.htmlreporter

import org.jdom2.DocType
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Text
import java.nio.file.Path

class HtmlWriter(
   private val outputDir: Path
) {
   companion object {
      const val summaryTitle = "Test Summary"
      const val classTitle = "Class"
      const val homeAnchorHref = "Home"
      const val defaultStylesPath = "css/style.css"
   }

   fun buildSummaryDocument(summaryList: List<TestClassInfo>, stylesPath: String = defaultStylesPath): Document {
      return withDocument(stylesPath) { body ->
         body.addContent(Element("h1").setContent(Text(summaryTitle)))
         body.addContent(
            generateTestSummaryTable(
               listOf("Class", "Tests", "Errors", "Failures", "Skipped"),
               summaryList
            )
         )
      }
   }

   fun buildClassDocument(
      testClass: TestClassInfo,
      homepage: String,
      stylesPath: String = defaultStylesPath
   ): Document {
      return withDocument(stylesPath) { body ->
         body.addContent(Element("h1").setContent(Text("$classTitle ${testClass.name}")))
         body.addContent(Element("a").setText(homeAnchorHref).setAttribute("href", homepage))
         body.addContent(generateTestClassTable(testClass.testcases))
      }
   }

   private fun withDocument(stylesPath: String, block: (Element) -> Unit): Document {
      val document = Document()
      document.docType = DocType("html")

      val html = Element("html")
      val head = Element("head")
      val body = Element("body")

      head.addContent(
         Element("link")
            .setAttribute("rel", "stylesheet")
            .setAttribute("href", outputDir.resolve(stylesPath).toString())
      )

      block(body)

      html.addContent(head)
      html.addContent(body)
      document.addContent(html)

      return document
   }

   private fun addHeaderColumn(row: Element, value: String) = row.addContent(Element("th").setContent(Text(value)))

   private fun generateTestSummaryTable(headers: List<String>, testClasses: List<TestClassInfo>): Element {
      val table = Element("table")
      val headerRow = Element("tr")


      headers.forEach {
         addHeaderColumn(headerRow, it)
      }

      table.addContent(headerRow)

      testClasses.forEach { testClass ->
         val row = Element("tr")
         val anchor = Element("a")
            .setContent(Text(testClass.name))
            .setAttribute("href", "./classes/${testClass.name}.html")

         val tests = testClass.summary.tests.toIntOrNull() ?: 0
         val errors = testClass.summary.errors.toIntOrNull() ?: 0
         val failures = testClass.summary.failures.toIntOrNull() ?: 0
         val skipped = testClass.summary.skipped.toIntOrNull() ?: 0

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

   private fun generateTestClassTable(testcases: List<TestCase>): Element {
      val table = Element("table")
      val headerRow = Element("tr")

      listOf("Test", "Duration", "Result").forEach { addHeaderColumn(headerRow, it) }

      table.addContent(headerRow)

      testcases.forEach { testCase ->
         val row = Element("tr")

         row.addContent(Element("td").setContent(Text(testCase.name)))
         row.addContent(Element("td").setContent(Text(testCase.duration)))
         row.addContent(Element("td").setContent(Text(testCase.result)))

         if (testCase.result == "Passed") {
            row.setAttribute("class", "success")
         } else {
            row.setAttribute("class", "failure")
         }

         table.addContent(row)
      }

      return table
   }
}
