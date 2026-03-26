package com.sksamuel.kotest.suite.functional

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SuiteReportDirTest : FunSpec({
   test("gradle writes xml test results to directory named after the test suite") {
      val suiteXmlDir = System.getProperty("suiteXmlDir")
         ?: error("Missing system property 'suiteXmlDir'")
      java.io.File(suiteXmlDir).name shouldBe "functionalTest"
   }
})
