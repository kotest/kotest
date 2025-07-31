package io.kotest.engine.launcher

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.reports.JUnitXmlWriter
import io.kotest.engine.test.TestResult
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.time.Clock

class JunitXmlTestEngineListener(private val testReportsDir: String, hostname: String) : TestEngineListener {

   private val writer = JUnitXmlWriter(
      clock = Clock.System,
      includeStackTraces = true,
      hostname = hostname
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
      File(testReportsDir).mkdirs()
      val xml = writer.writeXml(ref.kclass, results)
      val fileName = ref.kclass.qualifiedName ?: ref.kclass.simpleName
      Files.writeString(Paths.get(testReportsDir).resolve("TEST-${fileName}.xml"), xml)
      results.clear()
   }

   override suspend fun testStarted(testCase: TestCase) {
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      results[testCase] = TestResult.Ignored(reason)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      results[testCase] = result
   }

}
