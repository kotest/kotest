package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll

class ShrinkingTest : FunSpec() {
   init {
      test("shrinking should show the exception raised by the shrunk values") {

         val stdout = captureStandardOut {
            shouldThrowAny {
               checkAll<String, String>(PropTestConfig(seed = 12312313)) { a, b ->
                  (a + b).length shouldBeLessThan 4
               }
            }
         }
         println(stdout)

         stdout.shouldContain("Property test failed for inputs")
         stdout.shouldContain("0) \"\"aaVTT\$H<<3&rYlvW==(>]8Sga\"")
         stdout.shouldContain("1) \"Z7XHs_\\&5YUAua4^Eq>m\$w:-\\EtY\\3b<\$<[WnDk,e'T9I3?1+Rvx;\")63cwl!`\"")
         stdout.shouldContain("Caused by java.lang.AssertionError: 88 should be < 4 at")
         stdout.shouldContain("Caused by java.lang.AssertionError: 62 should be < 4 at")
         stdout.shouldContain("Caused by java.lang.AssertionError: 4 should be < 4 at")
         stdout.shouldContain("Attempting to shrink arg \"\"aaVTT\$H<<3&rYlvW==(>]8Sga\"")
         stdout.shouldContain("Attempting to shrink arg \"Z7XHs_\\&5YUAua4^Eq>m\$w:-\\EtY\\3b<\$<[WnDk,e'T9I3?1+Rvx;\")63cwl!`\"")
         stdout.shouldContain("Shrink result (after 6 shrinks) => <empty string>")
         stdout.shouldContain("Shrink result (after 10 shrinks) => \"aaaa\"")
      }
   }
}
