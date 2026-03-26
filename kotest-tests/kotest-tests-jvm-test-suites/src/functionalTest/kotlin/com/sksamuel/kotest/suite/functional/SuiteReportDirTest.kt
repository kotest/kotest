package com.sksamuel.kotest.suite.functional

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SuiteReportDirTest : FunSpec({
   test("gradle writes xml test results to directory named after the test suite") {
      // The 'functionalTest' JVM test suite task writes XML reports to build/test-results/functionalTest/
      java.io.File("build/test-results/functionalTest").name shouldBe "functionalTest"
   }
})
