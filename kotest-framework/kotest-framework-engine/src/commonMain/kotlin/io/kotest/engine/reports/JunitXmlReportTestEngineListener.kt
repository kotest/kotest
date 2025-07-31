package io.kotest.engine.reports

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestResult
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.reflect.KClass
import kotlin.time.Clock

class JunitXmlReportTestEngineListener(private val testReportsDir: String, hostname: String?) : TestEngineListener {

   private val generator = JUnitXmlReportGenerator(
      clock = Clock.System,
      includeStackTraces = true,
      hostname = hostname,
   )

   private val results = mutableMapOf<TestCase, TestResult>()

   override suspend fun engineStarted() {
   }

   override suspend fun engineInitialized(context: EngineContext) {
   }

   override suspend fun engineFinished(t: List<Throwable>) {
   }

   override suspend fun specStarted(ref: SpecRef) {
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      val specName = ref.kclass.qualifiedName ?: ref.kclass.simpleName
      val testFile = "TEST-${specName}.xml"
      val xml = generator.xml(ref.kclass, results)
      writeFile(testReportsDir, testFile, xml)
   }

   override suspend fun testStarted(testCase: TestCase) {
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      results[testCase] = TestResult.Ignored(reason)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      results[testCase] = result
   }

   fun writeFile(baseDir: String, filename: String, contents: String) {
      val path = Path(baseDir, filename)
      SystemFileSystem.createDirectories(path)
      println(" >> Test report will be written to $path")
      val sink = SystemFileSystem.sink(path, append = false).buffered()
      sink.writeString(contents)
      sink.close()
   }
}
