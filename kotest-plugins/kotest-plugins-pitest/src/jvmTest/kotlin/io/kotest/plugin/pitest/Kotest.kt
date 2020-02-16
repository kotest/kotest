package io.kotest.plugin.pitest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.plugin.pitest.specs.FunSpecs
import io.kotest.plugin.pitest.specs.StringSpecs
import io.kotest.plugin.pitest.specs.WordSpecs

class Kotest : FunSpec() {

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
    KotestUnitFinder().findTestUnits(clazz)
        .stream()
        .forEach { testUnit -> testUnit.execute(resultCollector) }
    return resultCollector
  }
}
