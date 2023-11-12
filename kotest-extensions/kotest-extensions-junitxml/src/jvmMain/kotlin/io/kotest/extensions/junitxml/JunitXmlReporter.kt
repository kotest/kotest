package io.kotest.extensions.junitxml

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.names.formatTestPath
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import org.jdom2.Document
import org.jdom2.Element
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
import kotlin.io.path.absolute
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Deprecated("Now called JunitXmlReporter. Deprecated since 4.6.")
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
) : PrepareSpecListener, FinalizeSpecListener {

   companion object {
      const val DefaultBuildDir = "./build"

      // sets the build directory, to which test-results will be appended
      const val BuildDirKey = "gradle.build.dir"

      const val AttributeName = "name"
   }

   private val formatter = getFallbackDisplayNameFormatter(ProjectConfiguration().registry, ProjectConfiguration())
   private var marks = ConcurrentHashMap<KClass<out Spec>, Long>()

   private fun outputDir(): Path {
      val buildDir = System.getProperty(BuildDirKey)
      return if (buildDir != null)
         Paths.get(buildDir, outputDir).normalize().absolute()
      else
         Paths.get(DefaultBuildDir, outputDir).normalize().absolute()
   }

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      marks[kclass] = System.currentTimeMillis()
   }

   private fun filterResults(results: Map<TestCase, TestResult>) = when (includeContainers) {
      true -> results
      false -> results.filter { it.key.type == TestType.Test }
   }

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      val start = marks[kclass] ?: System.currentTimeMillis()
      val duration = System.currentTimeMillis() - start

      val filtered = filterResults(results)

      val document = Document()
      val testSuite = Element("testsuite")
      testSuite.setAttribute("timestamp", ISO_LOCAL_DATE_TIME.format(getCurrentDateTime()))
      testSuite.setAttribute("time", (duration.milliseconds.toDouble(DurationUnit.SECONDS)).toString())
      testSuite.setAttribute("hostname", hostname())
      testSuite.setAttribute("errors", filtered.filter { it.value.isError }.size.toString())
      testSuite.setAttribute("failures", filtered.filter { it.value.isFailure }.size.toString())
      testSuite.setAttribute("skipped", filtered.filter { it.value.isIgnored }.size.toString())
      testSuite.setAttribute("tests", filtered.size.toString())
      testSuite.setAttribute(AttributeName, formatter.format(kclass))
      document.addContent(testSuite)

      filtered.map { (testcase, result) ->

         val name = when (useTestPathAsName) {
            true -> formatter.formatTestPath(testcase, " -- ")
            false -> formatter.format(testcase)
         }

         val e = Element("testcase")
         e.setAttribute(AttributeName, name)
         e.setAttribute("classname", kclass.java.canonicalName)
         e.setAttribute("time", result.duration.toDouble(DurationUnit.SECONDS).toString())

         when (result) {
            is TestResult.Error -> {
               val err = Element("error")
               result.errorOrNull?.let {
                  err.setAttribute("type", it.javaClass.name)
                  err.setText(it.message)
               }
               e.addContent(err)
            }
            is TestResult.Failure -> {
               val failure = Element("failure")
               result.errorOrNull?.let {
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

   private fun write(kclass: KClass<*>, document: Document) {
      val path = outputDir().resolve("TEST-" + formatter.format(kclass) + ".xml")
      path.parent.toFile().mkdirs()
      val outputter = XMLOutputter(Format.getPrettyFormat())
      val writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
      outputter.output(document, writer)
      writer.flush()
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
