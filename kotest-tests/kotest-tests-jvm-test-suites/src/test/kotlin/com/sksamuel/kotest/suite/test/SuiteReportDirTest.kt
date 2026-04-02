package com.sksamuel.kotest.suite.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SuiteReportDirTest : FunSpec({
   test("gradle writes xml test results to directory named after the test suite") {
      // The 'test' JVM test suite task writes XML reports to build/test-results/test/
      java.io.File("build/test-results/test").name shouldBe "test"
   }
})
