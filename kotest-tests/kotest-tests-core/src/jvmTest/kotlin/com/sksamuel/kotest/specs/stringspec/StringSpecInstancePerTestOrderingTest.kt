package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.extensions.TestListener
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.shouldBe
import io.kotest.core.spec.style.StringSpec
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class StringSpecInstancePerTestOrderingTest : StringSpec() {

   companion object {
      var string = ""
   }

   override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest

   private var uniqueCount = AtomicInteger(0)

   init {

      listener(object : TestListener {
         override fun finalizeSpec(kclass: KClass<out SpecConfiguration>, results: Map<TestCase, TestResult>) {
            string shouldBe "a_z_b_y_c_"
         }
      })

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
