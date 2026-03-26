package com.skamuel.kotest.runner.junit4.com.sksamuel.kotest.runner.junit4t

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

// junit 4 will show an error if the test count for started / finished doesn't match up
@Description("Test that KotestTestRunner runner handles nested test reporting")
@RunWith(KotestTestRunner::class)
class HelloWorldTest : FreeSpec() {

   init {
      "some context" - {
         "more context" - {
            "First Test" {
               1.shouldBeLessThan(2)
            }
         }
      }

      "String tests #@!*!$" - {
         "substring" {
            "helloworld".shouldContain("world")
         }

         "startwith" {
            "hello".shouldStartWith("he")
         }
      }
   }

}
