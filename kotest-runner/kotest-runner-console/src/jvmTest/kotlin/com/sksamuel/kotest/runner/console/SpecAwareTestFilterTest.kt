package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.core.TestFilterResult
import io.kotest.core.fromSpecClass
import io.kotest.core.specs.AbstractFeatureSpec
import io.kotest.runner.console.SpecAwareTestFilter
import io.kotest.shouldBe
import io.kotest.specs.BehaviorSpec
import io.kotest.specs.FeatureSpec
import io.kotest.specs.FreeSpec
import io.kotest.specs.FunSpec
import io.kotest.specs.ShouldSpec
import io.kotest.specs.StringSpec
import io.kotest.specs.WordSpec

class SpecAwareTestFilterTest : FunSpec() {

  init {

    test("should detect style for spec with intermediate parents") {
      val r = Description.fromSpecClass(FeatureSpecs2::class)
      SpecAwareTestFilter("Feature: a", FeatureSpecs2::class)
          .filter(r.append("Feature: a").append("Scenario: c")) shouldBe TestFilterResult.Include
    }

    test("should filter for fun specs") {
      val r = Description.fromSpecClass(FunSpecs::class)
      SpecAwareTestFilter("test a", FunSpecs::class).filter(r.append("test")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("test", FunSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("test a", FunSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("test a", FunSpecs::class).filter(r.append("test a b")) shouldBe TestFilterResult.Exclude
    }

    test("should filter for strings specs") {
      val r = Description.fromSpecClass(StringSpecs::class)

      SpecAwareTestFilter("test a", StringSpecs::class).filter(r.append("test")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("test", StringSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("test a", StringSpecs::class).filter(r.append("test a")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("test a", StringSpecs::class).filter(r.append("test a b")) shouldBe TestFilterResult.Exclude
    }

    test("should filter for should specs") {
      val r = Description.fromSpecClass(ShouldSpecs::class)

      SpecAwareTestFilter("should test a", ShouldSpecs::class)
          .filter(r.append("should test")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("should test", ShouldSpecs::class)
          .filter(r.append("should test a")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("should test a", ShouldSpecs::class)
          .filter(r.append("should test a")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("should test a", ShouldSpecs::class)
          .filter(r.append("should test a b")) shouldBe TestFilterResult.Exclude
    }

    test("should filter for word specs") {
      val r = Description.fromSpecClass(WordSpecs::class)

      SpecAwareTestFilter("a should b", WordSpecs::class)
          .filter(r.append("a should")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a should b", WordSpecs::class)
          .filter(r.append("a should").append("b")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a should b", WordSpecs::class)
          .filter(r.append("a should").append("c")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a should b", WordSpecs::class)
          .filter(r.append("b should")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a should b", WordSpecs::class)
          .filter(r.append("b should").append("b")) shouldBe TestFilterResult.Exclude
    }

    test("should filter for free specs") {
      val r = Description.fromSpecClass(FreeSpecs::class)

      SpecAwareTestFilter("a", FreeSpecs::class)
          .filter(r.append("a")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a", FreeSpecs::class)
          .filter(r.append("b").append("b")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a -- b", FreeSpecs::class)
          .filter(r.append("a")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a -- b", FreeSpecs::class)
          .filter(r.append("a").append("b")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a -- b", FreeSpecs::class)
          .filter(r.append("c")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a -- b", FreeSpecs::class)
          .filter(r.append("a").append("c")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a -- b", FreeSpecs::class)
          .filter(r.append("b").append("b")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a -- b -- c", FreeSpecs::class)
          .filter(r.append("a")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a -- b -- c", FreeSpecs::class)
          .filter(r.append("a").append("b")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a -- b -- c", FreeSpecs::class)
          .filter(r.append("a").append("b").append("c")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("a -- b -- c", FreeSpecs::class)
          .filter(r.append("b").append("b").append("c")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a -- b -- c", FreeSpecs::class)
          .filter(r.append("b").append("c")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("a -- b -- c", FreeSpecs::class)
          .filter(r.append("a").append("b").append("c").append("d")) shouldBe TestFilterResult.Include
    }

    test("should filter for behavior specs") {
      val r = Description.fromSpecClass(BehaviorSpecs::class)

      SpecAwareTestFilter("Given: a", BehaviorSpecs::class)
          .filter(r.append("Given: a")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Given: a", BehaviorSpecs::class)
          .filter(r.append("Given: aa")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Given: a", BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: b")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Given: a", BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: b").append("Then: c")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("Given: a When: b", BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: b")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Given: a When: b", BehaviorSpecs::class)
          .filter(r.append("Given: aa")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Given: a When: b", BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: bb")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Given: a When: b", BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: b").append("Then: c")) shouldBe TestFilterResult.Include

      SpecAwareTestFilter("Given: a When: b Then: c",
          BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: b").append("Then: c")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Given: a When: b Then: c",
          BehaviorSpecs::class)
          .filter(r.append("Given: aa").append("When: b").append("Then: c")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Given: a When: b Then: c",
          BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: bb").append("Then: c")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Given: a When: b Then: c",
          BehaviorSpecs::class)
          .filter(r.append("Given: a").append("When: b").append("Then: cc")) shouldBe TestFilterResult.Exclude
    }

    test("should filter for feature specs") {
      val r = Description.fromSpecClass(FeatureSpecs::class)

      SpecAwareTestFilter("Feature: a", FeatureSpecs::class)
          .filter(r.append("Feature: a")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Feature: a", FeatureSpecs::class)
          .filter(r.append("Feature: aa")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Feature: a", FeatureSpecs::class)
          .filter(r.append("Feature: a").append("Scenario: b")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Feature: a", FeatureSpecs::class)
          .filter(r.append("Feature: a").append("Scenario: c")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Feature: b", FeatureSpecs::class)
          .filter(r.append("Feature: a").append("Scenario: b")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Feature: b", FeatureSpecs::class)
          .filter(r.append("Feature: a").append("Scenario: c")) shouldBe TestFilterResult.Exclude

      SpecAwareTestFilter("Feature: a Scenario: b",
          FeatureSpecs::class)
          .filter(r.append("Feature: a").append("Scenario: b")) shouldBe TestFilterResult.Include
      SpecAwareTestFilter("Feature: a Scenario: b",
          FeatureSpecs::class)
          .filter(r.append("Feature: aa")) shouldBe TestFilterResult.Exclude
      SpecAwareTestFilter("Feature: a Scenario: b",
          FeatureSpecs::class)
          .filter(r.append("Feature: a").append("Scenario: bb")) shouldBe TestFilterResult.Exclude
    }
  }
}

abstract class AbstractFeature(body: AbstractFeatureSpec.() -> Unit = {}) : FeatureSpec(body = body)

class BehaviorSpecs : BehaviorSpec()
class FeatureSpecs : FeatureSpec()
class FeatureSpecs2 : AbstractFeature()
class FreeSpecs : FreeSpec()
class FunSpecs : FunSpec()
class ShouldSpecs : ShouldSpec()
class StringSpecs : StringSpec()
class WordSpecs : WordSpec()
