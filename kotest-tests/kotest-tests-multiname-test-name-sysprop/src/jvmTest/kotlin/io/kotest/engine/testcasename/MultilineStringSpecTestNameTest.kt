package io.kotest.engine.testcasename

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MultilineStringSpecTestNameTest : StringSpec({

   val testNames = mutableListOf<String>()

   """1 first test line one
         first test line two
               first test line three
                              """ {
      // dummy test
      testNames.add(this.testCase.displayName)
   }

   """2         second test line one
         second test line two
               second test line three
                              """ {
      // dummy test
      testNames.add(this.testCase.displayName)
   }

"""
3
third
test
last
line
""" {
testNames.add(this.testCase.displayName)
}

   afterSpec {
      val firstTestCaseName = testNames.find { it.startsWith("1") }
      val secondTestCaseName = testNames.find { it.startsWith("2") }
      val thirdTestCaseName = testNames.find { it.startsWith("3") }

      assertSoftly {
         firstTestCaseName shouldBe "1 first test line one first test line two first test line three"
         secondTestCaseName shouldBe "2 second test line one second test line two second test line three"
         thirdTestCaseName shouldBe "3 third test last line"
      }
   }
})
