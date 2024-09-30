package io.kotest.extensions.junitxml

import io.kotest.common.testTimeSource
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.formatTestPath
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
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
      outputDir: String = DefaultTestResultRelativeDir,
      clock: Clock = Clock.systemDefaultZone(),
   ) : this(
      includeContainers = includeContainers,
      useTestPathAsName = useTestPathAsName,
      clock = clock,
      outputDir = defaultOutputDir(outputDir),
   )

   companion object {
      const val DefaultBuildDir = "./build"

      /**
       * System property that provides the project's build directory.
       *
       * @see defaultOutputDir
       */
      const val BuildDirKey = "gradle.build.dir"

      const val AttributeName = "name"

      private const val DefaultTestResultRelativeDir = "test-results/test"

      private fun defaultOutputDir(
         testResultsPath: String = DefaultTestResultRelativeDir,
      ): Path {
         val buildDir = Path(System.getProperty(BuildDirKey) ?: DefaultBuildDir)
         return buildDir.resolve(testResultsPath).normalize().absolute()
      }

      private val hostname: String? by lazy {
         try {
            InetAddress.getLocalHost().hostName
         } catch (e: UnknownHostException) {
            null
         }
      }
   }

   private val formatter: FallbackDisplayNameFormatter =
      getFallbackDisplayNameFormatter(ProjectConfiguration().registry, ProjectConfiguration())

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
         setAttribute(AttributeName, formatter.format(kclass))
      }
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
               e.addContent(
                  Element("error").apply {
                     result.errorOrNull?.let { throwable ->
                        setAttribute("type", throwable.javaClass.name)
                        setText(throwable.message)
                     }
                  }
               )
            }

            is TestResult.Failure -> {
               val failure = Element("failure")
               result.errorOrNull?.let { throwable ->
                  failure.setAttribute("type", throwable.javaClass.name)
                  failure.setText(throwable.message)
               }
               e.addContent(failure)
            }

            is TestResult.Ignored,
            is TestResult.Success -> {
               // Only report failures, not Ignored or Success
            }
         }

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
}
