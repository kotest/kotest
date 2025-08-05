package io.kotest.extensions.pitest

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

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
      KotestUnitFinder().findTestUnits(clazz, null)
         .stream()
         .forEach { testUnit -> testUnit.execute(resultCollector) }
      return resultCollector
   }
}

private class FunSpecs : FunSpec() {
   init {
      test("passing test") { 1 shouldBe 1 }
      test("failing test") { 1 shouldBe 2 }
   }
}

private class StringSpecs : StringSpec() {
   init {
      "passing test" { 1 shouldBe 1 }
      "failing test" { 1 shouldBe 2 }
   }
}

private class WordSpecs : WordSpec() {
   init {
      "should container" should {
         "passing test" { 1 shouldBe 1 }
         "failing test" { 1 shouldBe 2 }
      }
      "when container" `when` {
         "nested should container" should {
            "passing test" { 1 shouldBe 1 }
            "failing test" { 1 shouldBe 2 }
         }
      }
   }
}
