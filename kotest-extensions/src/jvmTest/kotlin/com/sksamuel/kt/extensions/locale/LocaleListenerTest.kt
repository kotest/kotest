package com.sksamuel.kt.extensions.locale

import io.kotest.core.annotation.Isolate
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.locale.LocaleTestListener
import io.kotest.matchers.shouldBe
import java.util.Locale

@Isolate
class LocaleListenerTest : FunSpec() {

   private val default = Locale.getDefault()
   private val ltl = LocaleTestListener(Locale.FRANCE)

   override val extensions: List<Extension> = listOf(ltl)

   init {
      test("LocaleTestListener should set locale") {
         Locale.getDefault() shouldBe Locale.FRANCE
      }

      afterSpec {
         // should be restored after test
         Locale.getDefault() shouldBe default
      }
   }
}
