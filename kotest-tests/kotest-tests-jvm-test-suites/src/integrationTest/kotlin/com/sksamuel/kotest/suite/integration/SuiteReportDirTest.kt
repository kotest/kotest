package com.sksamuel.kotest.suite.integration

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SuiteReportDirTest : FunSpec({
   test("gradle writes xml test results to directory named after the test suite") {
      // The 'integrationTest' JVM test suite task writes XML reports to build/test-results/integrationTest/
      java.io.File("build/test-results/integrationTest").name shouldBe "integrationTest"
   }
})
