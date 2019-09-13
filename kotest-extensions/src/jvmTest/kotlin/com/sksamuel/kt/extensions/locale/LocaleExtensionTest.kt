package com.sksamuel.kt.extensions.locale

import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.TopLevelTest
import io.kotest.extensions.locale.LocaleTestListener
import io.kotest.extensions.locale.withDefaultLocale
import io.kotest.shouldBe
import io.kotest.shouldNotBe
import io.kotest.shouldThrowAny
import io.kotest.specs.DescribeSpec
import io.kotest.specs.FunSpec
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

class LocaleListenerTest : FunSpec() {

  override fun listeners() = listOf(LocaleTestListener(Locale.FRANCE))

  private var deflocale: Locale? = null

  override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
    deflocale = Locale.getDefault()
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    Locale.getDefault() shouldBe deflocale
  }

  init {
    test("locale default should be set, and then restored after") {
      Locale.getDefault() shouldBe Locale.FRANCE
    }
  }
}
