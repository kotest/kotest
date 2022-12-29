package io.kotest.extensions.htmlreporter

import io.kotest.core.listeners.ProjectListener
import org.jdom2.Document
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
      const val DefaultResultsLocation = "./build/test-results/test"
      const val BuildDirKey = "gradle.build.dir"
   }

   override suspend fun afterProject() {
      super.afterProject()

      val writer = HtmlWriter(outputDir())
      val testResults = getTestResults()
      val testClasses: MutableList<TestClassInfo> = mutableListOf()

      testResults.forEach {
         val builder = SAXBuilder()
         val doc = builder.build(it.path)

         with(doc.rootElement) {
            testClasses.add(
               TestClassInfo(
                  this.getAttributeValue("name"),
                  SummaryInfo(
                     this.getAttributeValue("tests"),
                     this.getAttributeValue("errors"),
                     this.getAttributeValue("failures"),
                     this.getAttributeValue("skipped")
                  ),
                  this.getChildren("testcase").map { elem ->
                     TestCase(
                        elem.getAttributeValue("name"),
                        elem.getAttributeValue("time"),
                        if(elem.getChild("failure") != null) "Failed" else "Passed"
                     )
                  }
               )
            )
         }
      }

      write(writer.buildSummaryDocument(testClasses), "index.html")
      testClasses.forEach { testClass ->
         write(writer.buildClassDocument(testClass, "../index.html"), "classes/${testClass.name}.html")
      }

      write({}.javaClass.getResource("/style.css").readText(), "./css/style.css")
   }

   private fun getTestResults(): Sequence<File> {
      return File(DefaultResultsLocation)
         .walk()
         .filter { Files.isRegularFile(it.toPath())}
         .filter { it.toString().endsWith(".xml") }
   }

   private fun outputDir(): Path {
      val buildDir = System.getProperty(BuildDirKey)
      return if (buildDir != null)
         Paths.get(buildDir).resolve(outputDir)
      else
         Paths.get("./$outputDir")
   }

   private fun write(text: String, path: String) {
      val absolutePath = outputDir().resolve(path)
      absolutePath.parent.toFile().mkdirs()
      val writer = Files.newBufferedWriter(absolutePath, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
      writer.use { out ->
         out.write(text)
      }
   }

   private fun write(document: Document, path: String) {
      with(XMLOutputter(Format.getPrettyFormat().setOmitDeclaration(true))) {
         write(this.outputString(document), path)
      }
   }
}

data class TestClassInfo(
   val name: String,
   val summary: SummaryInfo,
   val testcases: List<TestCase>
)

data class SummaryInfo(
   val tests: String,
   val errors: String,
   val failures: String,
   val skipped: String
)

data class TestCase(
   val name: String,
   val duration: String,
   val result: String
)

