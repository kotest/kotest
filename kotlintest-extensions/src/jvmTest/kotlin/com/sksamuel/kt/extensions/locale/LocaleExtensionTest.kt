package com.sksamuel.kt.extensions.locale

import io.kotlintest.*
import io.kotlintest.extensions.locale.LocaleTestListener
import io.kotlintest.extensions.locale.withDefaultLocale
import io.kotlintest.listener.TopLevelTest
import io.kotlintest.specs.DescribeSpec
import io.kotlintest.specs.FunSpec
import java.util.*

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