package io.kotest.extensions.allure

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.config.TestConfigResolver
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import kotlin.reflect.full.findAnnotation

fun TestCase.maxSeverity(): SeverityLevel? {
   val annotation = this.spec::class.findAnnotation<Severity>()?.value?.toTestCaseSeverity()
   val test = TestConfigResolver().severity(this)
   val max = if (annotation != null) {
      maxOf(annotation, test, compareBy { it.level })
   } else {
      test
   }
   return max.toAllureSeverity()
}


fun TestCaseSeverityLevel.toAllureSeverity(): SeverityLevel? = when (this) {
   TestCaseSeverityLevel.BLOCKER -> SeverityLevel.BLOCKER
   TestCaseSeverityLevel.CRITICAL -> SeverityLevel.CRITICAL
   TestCaseSeverityLevel.NORMAL -> SeverityLevel.NORMAL
   TestCaseSeverityLevel.MINOR -> SeverityLevel.MINOR
   TestCaseSeverityLevel.TRIVIAL -> SeverityLevel.TRIVIAL
}

fun SeverityLevel.toTestCaseSeverity(): TestCaseSeverityLevel? = when (this) {
   SeverityLevel.BLOCKER -> TestCaseSeverityLevel.BLOCKER
   SeverityLevel.CRITICAL -> TestCaseSeverityLevel.CRITICAL
   SeverityLevel.NORMAL -> TestCaseSeverityLevel.NORMAL
   SeverityLevel.MINOR -> TestCaseSeverityLevel.MINOR
   SeverityLevel.TRIVIAL -> TestCaseSeverityLevel.TRIVIAL
}
