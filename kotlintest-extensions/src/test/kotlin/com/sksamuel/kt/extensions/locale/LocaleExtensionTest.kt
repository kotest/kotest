package com.sksamuel.kt.extensions.locale

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.extensions.locale.LocaleExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.util.*

class LocaleExtensionTest : FunSpec() {

  override fun extensions() = listOf(LocaleExtension(Locale.FRANCE))

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