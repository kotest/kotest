package com.sksamuel.kt.extensions.time

import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.time.ConstantNowTestListener
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.time.LocalDateTime
import java.time.chrono.HijrahDate

class ConstantNowExtensionsListenerTest : StringSpec() {

   private val myNow = HijrahDate.now()
   private val myNow2 = LocalDateTime.now()

   init {
      listeners(ConstantNowTestListener(myNow), ConstantNowTestListener(myNow2))

      finalizeSpec {
         HijrahDate.now() shouldNotBeSameInstanceAs myNow
         LocalDateTime.now() shouldNotBeSameInstanceAs myNow2
      }

      "Should use my now" {
         HijrahDate.now() shouldBeSameInstanceAs myNow
         LocalDateTime.now() shouldBeSameInstanceAs myNow2
      }
   }
}
