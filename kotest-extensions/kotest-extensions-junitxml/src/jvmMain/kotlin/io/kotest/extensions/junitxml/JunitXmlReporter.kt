package io.kotest.extensions.junitxml

import io.kotest.common.testTimeSource
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.formatTestPath
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.extensions.junitxml.JunitXmlReporter.Companion.defaultOutputDir
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createParentDirectories
import kotlin.reflect.KClass
import kotlin.time.DurationUnit
import kotlin.time.TimeMark

/**
 * A JUnit XML legacy format writer.
 *
 * This implementation handles nesting, whereas the JUnit implementation will only output for leaf tests.
 *
 * Note: This site has a good set of schemas and examples of the format: https://github.com/testmoapp/junitxml
 * Another useful,but less details is https://llg.cubic.org/docs/junit
 *
 * @param includeContainers when `true`, all intermediate tests are included in the report as
 * tests in their own right.
 * Defaults to `false`.
 *
 * @param useTestPathAsName when `true`, the full test path will be used as the name.
 * In other words the name will include the name of any parent tests as a single string.
 *
 * @param outputDir The directory to write reports.
 */
class JunitXmlReporter(
   private val includeContainers: Boolean = false,
   private val useTestPathAsName: Boolean = true,
   private val outputDir: Path,
   private val clock: Clock = Clock.systemDefaultZone(),
) : PrepareSpecListener, FinalizeSpecListener {

   /**
    * @param outputDir Path of the output directory, relative to the build directory.
    */
   constructor(
      includeContainers: Boolean = false,
      useTestPathAsName: Boolean = true,
      outputDir: String = DEFAULT_TEST_RESULT_RELATIVE_DIR,
      clock: Clock = Clock.systemDefaultZone(),
   ) : this(
      includeContainers = includeContainers,
      useTestPathAsName = useTestPathAsName,
      clock = clock,
      outputDir = defaultOutputDir(outputDir),
   )

   companion object {
      private const val DEFAULT_BUILD_DIR = "./build"

      /**
       * System property that provides the project's build directory.
       *
       * @see defaultOutputDir
       */
      private const val BUILD_DIR_KEY = "gradle.build.dir"

      const val ELEMENT_FAILURE = "failure"
      const val ELEMENT_ERROR = "error"
      const val ATTRIBUTE_NAME = "name"
      const val ATTRIBUTE_TYPE = "type"
      const val ATTRIBUTE_MESSAGE = "message"

      private const val DEFAULT_TEST_RESULT_RELATIVE_DIR = "test-results/test"

      private fun defaultOutputDir(
         testResultsPath: String = DEFAULT_TEST_RESULT_RELATIVE_DIR,
      ): Path {
         val buildDir = Path(System.getProperty(BUILD_DIR_KEY) ?: DEFAULT_BUILD_DIR)
         return buildDir.resolve(testResultsPath).normalize().absolute()
      }

      private val hostname: String? by lazy {
         try {
            InetAddress.getLocalHost().hostName
         } catch (_: UnknownHostException) {
            null
         }
      }
   }

   private val formatter: FallbackDisplayNameFormatter =
      getFallbackDisplayNameFormatter(ProjectConfigResolver(), TestConfigResolver())

   /** Record the start of each spec, so the duration of each can be measured. */
   private val marks: ConcurrentHashMap<KClass<out Spec>, TimeMark> =
      ConcurrentHashMap()

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      marks[kclass] = testTimeSource().markNow()
   }

   private fun filterResults(results: Map<TestCase, TestResult>) =
      when (includeContainers) {
         true -> results
         false -> results.filter { it.key.type == TestType.Test }
      }

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      val start = marks[kclass] ?: testTimeSource().markNow()
      val duration = start.elapsedNow()

      val filtered = filterResults(results)

      val document = Document()
      val testSuite = Element("testsuite").apply {
         setAttribute("timestamp", getCurrentDateTimeIsoString())
         setAttribute("time", duration.toDouble(DurationUnit.SECONDS).toString())
         setAttribute("hostname", hostname)
         setAttribute("errors", filtered.count { it.value.isError }.toString())
         setAttribute("failures", filtered.count { it.value.isFailure }.toString())
         setAttribute("skipped", filtered.count { it.value.isIgnored }.toString())
         setAttribute("tests", filtered.size.toString())
         setAttribute(ATTRIBUTE_NAME, formatter.format(kclass))
      }
      document.addContent(testSuite)

      filtered.map { (testcase, result) ->

         val name = when (useTestPathAsName) {
            true -> formatter.formatTestPath(testcase, " -- ")
            false -> formatter.format(testcase)
         }

         val e = createTestCaseElement(name, result, kclass)
         testSuite.addContent(e)
      }

      write(kclass, document)
   }

   private fun write(kclass: KClass<*>, document: Document) {
      outputDir
         .resolve("TEST-" + formatter.format(kclass) + ".xml")
         .createParentDirectories()
         .bufferedWriter(options = arrayOf(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE))
         .use { writer ->
            val outputter = XMLOutputter(Format.getPrettyFormat())
            outputter.output(document, writer)
         }
   }

   private fun getCurrentDateTimeIsoString(): String =
      ISO_LOCAL_DATE_TIME.format(
         LocalDateTime.now(clock).withNano(0)
      )

   internal fun createTestCaseElement(name: String, result: TestResult, kclass: KClass<out Spec>): Element {
      val e = Element("testcase")
      e.setAttribute(ATTRIBUTE_NAME, name)
      e.setAttribute("classname", kclass.java.canonicalName)
      e.setAttribute("time", result.duration.toDouble(DurationUnit.SECONDS).toString())

      when (result) {
         is TestResult.Error -> {
            result.errorOrNull?.let { throwable ->
               val error = Element(ELEMENT_ERROR)
               error.setAttribute(ATTRIBUTE_TYPE, throwable.javaClass.name)
               error.setAttribute(ATTRIBUTE_MESSAGE, throwable.message ?: "No message")
               error.setText(throwable.stackTraceToString())
               e.addContent(error)
            }
         }

         is TestResult.Failure -> {
            result.errorOrNull?.let { throwable ->
               val failure = Element(ELEMENT_FAILURE)
               failure.setAttribute(ATTRIBUTE_TYPE, throwable.javaClass.name)
               failure.setAttribute(ATTRIBUTE_MESSAGE, throwable.message ?: "No message")
               failure.setText(throwable.stackTraceToString())
               e.addContent(failure)
            }
         }

         is TestResult.Ignored,
         is TestResult.Success -> {
            // Only report failures, not Ignored or Success
         }
      }
      return e
   }
}
