package io.kotlintest.extensions.junitxml

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.extensions.TestListener
import java.io.File
import java.io.FileWriter
import java.util.concurrent.ConcurrentHashMap

// for xml spec see http://help.catchsoftware.com/display/ET/JUnit+Format
object JUnitXmlListener : TestListener {

  private val results = ConcurrentHashMap<Description, TestResult>()

  private val outputDirectory = System.getProperty("kotlintest.junit.report.directory") ?: "build/junit-report"

  override fun specFinished(description: Description, spec: Spec) {
    val testResults = results.filterKeys { it.hasParent(description) }
    val failures = testResults.filter { it.value.status == TestStatus.Failed }
    val disabled = testResults.filter { it.value.status == TestStatus.Failed }
    val file = File("$outputDirectory/${description.fullName()}.xml")
    file.parentFile.mkdirs()
    val writer = FileWriter(file)
    writer.write("""<?xml version="1.0" encoding="UTF-8"?>""")
    writer.write("""<testsuite tests="${testResults.size}" failures="${failures.size}" disabled="${disabled.size}" errors="0" time="0" name="${spec.name()}">""")
    testResults.forEach {
      writer.write("""<testcase name="${it.key.fullName().drop(description.name.length).trim()}" status="run" time="0" classname="${spec.javaClass.canonicalName}">""")
      when (it.value.status) {
        TestStatus.Failed -> writer.write("""<failure message="${it.value.error?.message}"/>""")
        TestStatus.Ignored -> writer.write("""<skipped/>""")
        TestStatus.Passed -> {
        }
      }
      writer.write("""</testcase>""")
    }
    writer.write("""</testsuite>""")
    writer.close()
  }

  override fun testFinished(description: Description, result: TestResult) {
    results[description] = result
  }
}