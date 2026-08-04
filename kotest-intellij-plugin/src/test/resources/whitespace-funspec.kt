package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec

class WhitespaceFunSpecExampleTest : FunSpec({

   context("outer context") {

      test("a  nested   test") {
      }
   }
})
