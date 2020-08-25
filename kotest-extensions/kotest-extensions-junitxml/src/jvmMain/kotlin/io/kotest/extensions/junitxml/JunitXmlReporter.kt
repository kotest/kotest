package io.kotest.extensions.junitxml

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.core.spec.toDescription
import org.jdom2.Element
import org.jdom2.Document
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Deprecated("Now called JunitXmlReporter")
typealias JunitXmlListener = JunitXmlReporter

/**
 * A JUnit xml legacy format writer.
 *
 * This implementation handles nesting, whereas the junit implementation will only output for leaf tests.
 *
 * @param includeContainers when true, all intermediate tests are included in the report as
 * tests in their own right. Defaults to false.
 *
 * @param useTestPathAsName when true, the full test path will be used as the name. In other
 * words the name will include the name of any parent tests as a single string.
 */
class JunitXmlReporter(
   private val includeContainers: Boolean = false,
   private val useTestPathAsName: Boolean = true,
   private val outputDir: String = "test-results/test"
) : TestListener {

   companion object {
      const val DefaultLocation = "./build/test-results/test/"

      // sets the build directory, to which test-results will be appended
      const val BuildDirKey = "gradle.build.dir"

      const val AttributeName = "name"
   }

   private var marks = ConcurrentHashMap<KClass<out Spec>, Long>()

   private fun outputDir(): Path {
      val buildDir = System.getProperty(BuildDirKey)
      return if (buildDir != null)
         Paths.get(buildDir).resolve(outputDir)
      else
         Paths.get(DefaultLocation)
   }

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      marks[kclass] = System.currentTimeMillis()
   }

   private fun filterResults(results: Map<TestCase, TestResult>) = when (includeContainers) {
      true -> results
      false -> results.filter { it.key.type == TestType.Test }
   }

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      super.finalizeSpec(kclass, results)

      val start = marks[kclass] ?: System.currentTimeMillis()
      val duration = System.currentTimeMillis() - start

      val filtered = filterResults(results)

      val document = Document()
      val testSuite = Element("testsuite")
      testSuite.setAttribute("timestamp", ISO_LOCAL_DATE_TIME.format(getCurrentDateTime()))
      testSuite.setAttribute("time", (duration / 1000).toString())
      testSuite.setAttribute("hostname", hostname())
      testSuite.setAttribute("errors", filtered.filter { it.value.status == TestStatus.Error }.size.toString())
      testSuite.setAttribute("failures", filtered.filter { it.value.status == TestStatus.Failure }.size.toString())
      testSuite.setAttribute("skipped", filtered.filter { it.value.status == TestStatus.Ignored }.size.toString())
      testSuite.setAttribute("tests", filtered.size.toString())
      testSuite.setAttribute(AttributeName, kclass.toDescription().displayName())
      document.addContent(testSuite)

      filtered.map { (testcase, result) ->

         val name = when (useTestPathAsName) {
            true -> testcase.description.testDisplayPath().value
            false -> testcase.description.name.displayName
         }

         val e = Element("testcase")
         e.setAttribute(AttributeName, name)
         e.setAttribute("classname", kclass.java.canonicalName)
         e.setAttribute("time", (result.duration / 1000).toString())

         when (result.status) {
            TestStatus.Error -> {
               val error = Element("error")
               result.error?.let {
                  error.setAttribute("type", it.javaClass.name)
                  error.setText(it.message)
               }
               e.addContent(error)
            }
            TestStatus.Failure -> {
               val failure = Element("failure")
               result.error?.let {
                  failure.setAttribute("type", it.javaClass.name)
                  failure.setText(it.message)
               }
               e.addContent(failure)
            }
            else -> Unit
         }

         testSuite.addContent(e)
      }

      write(kclass, document)
   }

   private fun write(kclass: KClass<out Spec>, document: Document) {
      val path = outputDir().resolve("TEST-" + kclass.toDescription().name.displayName + ".xml")
      path.parent.toFile().mkdirs()
      val outputter = XMLOutputter(Format.getPrettyFormat())
      val writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
      outputter.output(document, writer)
      writer.close()
   }

   private fun hostname(): String? {
      return try {
         InetAddress.getLocalHost().hostName
      } catch (e: UnknownHostException) {
         null
      }
   }

   private fun getCurrentDateTime(): LocalDateTime {
      return LocalDateTime.now(Clock.systemDefaultZone()).withNano(0)
   }
}
