package com.sksamuel.kt.extensions.locale

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.locale.withDefaultLocale
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.Locale

class LocaleExtensionFunctionTest : DescribeSpec() {

   init {
      describe("The Locale extension function") {

         Locale.getDefault() shouldNotBe Locale.FRANCE  // Guaranteeing pre-condition, as we'll use Locale.FRANCE in all tests

         it("Should change the Locale to the expected one") {
            withDefaultLocale(Locale.FRANCE) {
               Locale.getDefault() shouldBe Locale.FRANCE
            }
         }

         it("Should reset the Locale to the previous one after the execution") {
            val previousLocale = Locale.getDefault()

            withDefaultLocale(Locale.FRANCE) { }

            Locale.getDefault() shouldBe previousLocale
         }

         it("Should reset the Locale to the previous one even if code throws an exception") {
            val previousLocale = Locale.getDefault()

            shouldThrowAny { withDefaultLocale<Unit>(Locale.FRANCE) { throw RuntimeException() } }

            Locale.getDefault() shouldBe previousLocale
         }

         it("Should return the result of block") {

            val v = withDefaultLocale(Locale.FRANCE) { "Foo!" }
            v shouldBe "Foo!"
         }
      }
   }
}
