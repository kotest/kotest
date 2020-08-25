package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class StringSpecInstancePerTestOrderingTest : StringSpec() {

   companion object {
      var string = ""
   }

   override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest

   private var uniqueCount = AtomicInteger(0)

   init {

      finalizeSpec {
         string shouldBe "a_z_b_y_c_"
      }

      "a" {
         string += "a_"
      }

      "z" {
         string += "z_"
      }

      "b" {
         string += "b_"
      }

      "y" {
         string += "y_"
      }

      "c" {
         string += "c_"
      }
   }
}
