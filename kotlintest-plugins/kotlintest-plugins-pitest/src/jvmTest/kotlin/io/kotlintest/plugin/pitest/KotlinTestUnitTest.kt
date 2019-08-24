package io.kotlintest.plugin.pitest

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.plugin.pitest.specs.FunSpecs
import io.kotlintest.plugin.pitest.specs.StringSpecs
import io.kotlintest.plugin.pitest.specs.WordSpecs
import io.kotlintest.specs.FunSpec

class KotlinTestUnitTest : FunSpec() {

  init {

    test("StringSpecs") {
      val resultCollector = findTestsIn(StringSpecs::class.java)
      resultCollector.skipped.shouldBeEmpty()
      resultCollector.started.shouldHaveSize(2)
      resultCollector.ended.shouldHaveSize(2)
      resultCollector.failures.shouldHaveSize(1)
    }

    test("FunSpecs") {
      val resultCollector = findTestsIn(FunSpecs::class.java)
      resultCollector.skipped.shouldBeEmpty()
      resultCollector.started.shouldHaveSize(2)
      resultCollector.ended.shouldHaveSize(2)
      resultCollector.failures.shouldHaveSize(1)
    }

    test("WordSpecs") {
      val resultCollector = findTestsIn(WordSpecs::class.java)
      resultCollector.skipped.shouldBeEmpty()
      resultCollector.started.shouldHaveSize(7)
      resultCollector.ended.shouldHaveSize(7)
      resultCollector.failures.shouldHaveSize(2)
    }
  }

  private fun findTestsIn(clazz: Class<*>): TestResultCollector {
    val resultCollector = TestResultCollector()
    KotlinTestUnitFinder().findTestUnits(clazz)
        .stream()
        .forEach { testUnit -> testUnit.execute(resultCollector) }
    return resultCollector
  }
}