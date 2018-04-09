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

  private val outputDirectory = System.getProperty("kotlintest.junit.report.directory") ?: "build/test-results"

  override fun afterSpec(description: Description, spec: Spec) {
    val testResults = results.filterKeys { it.hasParent(description) }
    val errors = testResults.filter { it.value.status == TestStatus.Error }
    val failures = testResults.filter { it.value.status == TestStatus.Failure }
    val disabled = testResults.filter { it.value.status == TestStatus.Error }
    val file = File("$outputDirectory/${description.fullName()}.xml")
    file.parentFile.mkdirs()
    val writer = FileWriter(file)
    writer.write("""<?xml version="1.0" encoding="UTF-8"?>""")
    writer.write("""<testsuite tests="${testResults.size}" failures="${failures.size}" disabled="${disabled.size}" errors="${errors.size}" time="0" name="${spec.name()}">""")
    testResults.forEach {
      writer.write("""<testcase name="${it.key.fullName().drop(description.name.length).trim()}" status="run" time="0" classname="${spec.javaClass.canonicalName}">""")
      when (it.value.status) {
        TestStatus.Error -> writer.write("""<error message="${it.value.error?.message}"/>""")
        TestStatus.Failure -> writer.write("""<failure message="${it.value.error?.message}"/>""")
        TestStatus.Ignored -> writer.write("""<skipped/>""")
        TestStatus.Success -> {
        }
      }
      writer.write("""</testcase>""")
    }
    writer.write("""</testsuite>""")
    writer.close()
  }

  override fun afterTest(description: Description, result: TestResult) {
    results[description] = result
  }
}