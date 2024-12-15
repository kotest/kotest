package io.kotest.extensions.allure

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import kotlin.reflect.full.findAnnotation

fun TestCase.maxSeverity(): SeverityLevel? {
   val classSeverity = this.spec::class.findAnnotation<Severity>()?.value?.toTestCaseSeverity()
   val max = if (classSeverity != null) {
      maxOf(classSeverity, config.severity, compareBy { it.level })
   } else {
      config.severity
   }

   return max.toAllureSeverity()
}


fun TestCaseSeverityLevel.toAllureSeverity(): SeverityLevel? = when (this) {
   TestCaseSeverityLevel.BLOCKER -> SeverityLevel.BLOCKER
   TestCaseSeverityLevel.CRITICAL -> SeverityLevel.CRITICAL
   TestCaseSeverityLevel.NORMAL -> SeverityLevel.NORMAL
   TestCaseSeverityLevel.MINOR -> SeverityLevel.MINOR
   TestCaseSeverityLevel.TRIVIAL -> SeverityLevel.TRIVIAL
   else -> null
}

fun SeverityLevel.toTestCaseSeverity(): TestCaseSeverityLevel? = when (this) {
   SeverityLevel.BLOCKER -> TestCaseSeverityLevel.BLOCKER
   SeverityLevel.CRITICAL -> TestCaseSeverityLevel.CRITICAL
   SeverityLevel.NORMAL -> TestCaseSeverityLevel.NORMAL
   SeverityLevel.MINOR -> TestCaseSeverityLevel.MINOR
   SeverityLevel.TRIVIAL -> TestCaseSeverityLevel.TRIVIAL
   else -> null
}
