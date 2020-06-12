package io.kotest.extensions.junitxml

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import org.jdom2.Element
import org.jdom2.Document
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@AutoScan
@OptIn(ExperimentalTime::class)
class JunitXmlListener : TestListener {

   private var marks = ConcurrentHashMap<KClass<out Spec>, TimeMark>()

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      marks[kclass] = TimeSource.Monotonic.markNow()
   }

   @OptIn(ExperimentalTime::class)
   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      super.finalizeSpec(kclass, results)

      val mark = marks[kclass] ?: TimeSource.Monotonic.markNow()
      val time = mark.elapsedNow()

      val document = Document()
      val testSuite = Element("testsuite")
      testSuite.setAttribute("timestamp", ISO_LOCAL_DATE_TIME.format(getCurrentDateTime()))
      testSuite.setAttribute("time", time.inSeconds.toString())
      testSuite.setAttribute("hostname", hostname())
      testSuite.setAttribute("errors", results.filter { it.value.status == TestStatus.Error }.size.toString())
      testSuite.setAttribute("failures", results.filter { it.value.status == TestStatus.Failure }.size.toString())
      testSuite.setAttribute("skipped", results.filter { it.value.status == TestStatus.Ignored }.size.toString())
      testSuite.setAttribute("tests", results.size.toString())
      testSuite.setAttribute("name", kclass.description().fullName())
      document.addContent(testSuite)

      results.map { (testcase, result) ->
         val e = Element("testcase")
         e.setAttribute("name", testcase.description.fullName())
         e.setAttribute("classname", kclass.java.canonicalName)
         e.setAttribute("time", result.duration.inSeconds.toString())

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
      val path = Paths.get("target").resolve("TEST-" + kclass.description().name.name + ".xml")
      val outputter = XMLOutputter(Format.getPrettyFormat())
      val writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)
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
