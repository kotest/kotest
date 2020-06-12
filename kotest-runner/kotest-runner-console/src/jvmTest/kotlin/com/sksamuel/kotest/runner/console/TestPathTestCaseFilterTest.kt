package com.sksamuel.kotest.runner.console

import io.kotest.core.filters.TestFilterResult
import io.kotest.core.spec.style.*
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe
import io.kotest.runner.console.TestPathTestCaseFilter

class TestPathTestCaseFilterTest : FunSpec() {

   init {

      test("should detect style for spec with intermediate parents") {
         val r = Description.spec(FeatureSpecs2::class)
         TestPathTestCaseFilter("Feature: a", FeatureSpecs2::class)
            .filter(r.append("Feature: a").append("Scenario: c")) shouldBe TestFilterResult.Include
      }

      test("should filter for fun specs") {
         val r = Description.spec(FunSpecs::class)
         TestPathTestCaseFilter("test a", FunSpecs::class).filter(r.append("test")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("test", FunSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("test a", FunSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("test a", FunSpecs::class).filter(r.append("test a b")) shouldBe TestFilterResult.Exclude
      }

      test("should filter for strings specs") {
         val r = Description.spec(StringSpecs::class)

         TestPathTestCaseFilter("test a", StringSpecs::class).filter(r.append("test")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("test", StringSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("test a", StringSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter(
            "test a",
            StringSpecs::class
         ).filter(r.append("test a b")) shouldBe TestFilterResult.Exclude
      }

      test("should filter for should specs") {
         val r = Description.spec(ShouldSpecs::class)

         TestPathTestCaseFilter("should test a", ShouldSpecs::class)
            .filter(r.append("should test")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("should test", ShouldSpecs::class)
            .filter(r.append("should test a")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("should test a", ShouldSpecs::class)
            .filter(r.append("should test a")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("should test a", ShouldSpecs::class)
            .filter(r.append("should test a b")) shouldBe TestFilterResult.Exclude
      }

      test("should filter for word specs") {
         val r = Description.spec(WordSpecs::class)

         TestPathTestCaseFilter("a should -- b", WordSpecs::class)
            .filter(r.append("a should")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a should -- b", WordSpecs::class)
            .filter(r.append("a should").append("b")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a should -- b", WordSpecs::class)
            .filter(r.append("a should").append("c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a should -- b", WordSpecs::class)
            .filter(r.append("b should")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a should -- b", WordSpecs::class)
            .filter(r.append("b should").append("b")) shouldBe TestFilterResult.Exclude
      }

      test("should filter for free specs") {
         val r = Description.spec(FreeSpecs::class)

         TestPathTestCaseFilter("a", FreeSpecs::class)
            .filter(r.append("a")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a", FreeSpecs::class)
            .filter(r.append("b").append("c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a -- b", FreeSpecs::class)
            .filter(r.append("a")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a -- b", FreeSpecs::class)
            .filter(r.append("a").append("b")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a -- b", FreeSpecs::class)
            .filter(r.append("c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a -- b", FreeSpecs::class)
            .filter(r.append("a").append("c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a -- b", FreeSpecs::class)
            .filter(r.append("b").append("b")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a -- b -- c", FreeSpecs::class)
            .filter(r.append("a")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a -- b -- c", FreeSpecs::class)
            .filter(r.append("a").append("b")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a -- b -- c", FreeSpecs::class)
            .filter(r.append("a").append("b").append("c")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("a -- b -- c", FreeSpecs::class)
            .filter(r.append("b").append("b").append("c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a -- b -- c", FreeSpecs::class)
            .filter(r.append("b").append("c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter("a -- b -- c", FreeSpecs::class)
            .filter(r.append("a").append("b").append("c").append("d")) shouldBe TestFilterResult.Include
      }

      test("should filter for behavior specs") {
         val r = Description.spec(BehaviorSpecs::class)

         TestPathTestCaseFilter("Given: a", BehaviorSpecs::class)
            .filter(r.append("Given: a")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("Given: a", BehaviorSpecs::class)
            .filter(r.append("Given: aa")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("Given: a", BehaviorSpecs::class)
            .filter(r.append("Given: a").append("When: b")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("Given: a", BehaviorSpecs::class)
            .filter(r.append("Given: a").append("When: b").append("Then: c")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter("Given: a -- When: b", BehaviorSpecs::class)
            .filter(r.append("Given: a").append("When: b")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("Given: a -- When: b", BehaviorSpecs::class)
            .filter(r.append("Given: aa")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("Given: a -- When: b", BehaviorSpecs::class)
            .filter(r.append("Given: a").append("When: bb")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("Given: a -- When: b", BehaviorSpecs::class)
            .filter(r.append("Given: a").append("When: b").append("Then: c")) shouldBe TestFilterResult.Include

         TestPathTestCaseFilter(
            "Given: a -- When: b -- Then: c",
            BehaviorSpecs::class
         )
            .filter(r.append("Given: a").append("When: b").append("Then: c")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter(
            "Given: a -- When: b -- Then: c",
            BehaviorSpecs::class
         )
            .filter(r.append("Given: aa").append("When: b").append("Then: c")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter(
            "Given: a -- When: b -- Then: c",
            BehaviorSpecs::class
         )
            .filter(r.append("Given: a").append("When: bb").append("Then: c")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter(
            "Given: a -- When: b -- Then: c",
            BehaviorSpecs::class
         )
            .filter(r.append("Given: a").append("When: b").append("Then: cc")) shouldBe TestFilterResult.Exclude
      }

      test("should filter for feature specs") {
         val r = Description.spec(FeatureSpecs::class)

         TestPathTestCaseFilter("Feature: a", FeatureSpecs::class)
            .filter(r.append("Feature: a")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("Feature: a", FeatureSpecs::class)
            .filter(r.append("Feature: aa")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("Feature: a", FeatureSpecs::class)
            .filter(r.append("Feature: a").append("Scenario: b")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("Feature: a", FeatureSpecs::class)
            .filter(r.append("Feature: a").append("Scenario: c")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter("Feature: b", FeatureSpecs::class)
            .filter(r.append("Feature: a").append("Scenario: b")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter("Feature: b", FeatureSpecs::class)
            .filter(r.append("Feature: a").append("Scenario: c")) shouldBe TestFilterResult.Exclude

         TestPathTestCaseFilter(
            "Feature: a -- Scenario: b",
            FeatureSpecs::class
         )
            .filter(r.append("Feature: a").append("Scenario: b")) shouldBe TestFilterResult.Include
         TestPathTestCaseFilter(
            "Feature: a -- Scenario: b",
            FeatureSpecs::class
         )
            .filter(r.append("Feature: aa")) shouldBe TestFilterResult.Exclude
         TestPathTestCaseFilter(
            "Feature: a -- Scenario: b",
            FeatureSpecs::class
         )
            .filter(r.append("Feature: a").append("Scenario: bb")) shouldBe TestFilterResult.Exclude
      }
   }
}

abstract class AbstractFeatureSpec : FeatureSpec()

class BehaviorSpecs : BehaviorSpec()
class FeatureSpecs : FeatureSpec()
class FeatureSpecs2 : AbstractFeatureSpec()
class FreeSpecs : FreeSpec()
class FunSpecs : FunSpec()
class ShouldSpecs : ShouldSpec()
class StringSpecs : StringSpec()
class WordSpecs : WordSpec()
