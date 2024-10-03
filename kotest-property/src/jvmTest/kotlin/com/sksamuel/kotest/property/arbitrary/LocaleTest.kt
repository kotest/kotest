package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.locale
import io.kotest.property.checkAll

@EnabledIf(LinuxCondition::class)
class LocaleTest : FunSpec({

   test("locale happy path") {
      checkAll(Arb.locale()) { locale ->
         locale.shouldHaveMinLength(2)
      }
   }

   test("locales edgecases should contain a local without country") {
      Arb.locale().edgecases().shouldContain("en")
   }

   test("locales edgecases should contain a locale with a variant") {
      Arb.locale().edgecases().shouldContain("ca_ES_VALENCIA")
   }
})
