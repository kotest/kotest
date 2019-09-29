package io.kotest.specs

import io.kotest.Matcher
import io.kotest.core.specs.BehaviorSpec
import io.kotest.core.specs.AbstractDescribeSpec
import io.kotest.core.specs.AbstractExpectSpec
import io.kotest.core.specs.AbstractFeatureSpec
import io.kotest.core.specs.AbstractFreeSpec
import io.kotest.core.specs.FunSpec
import io.kotest.core.specs.AbstractShouldSpec
import io.kotest.core.specs.AbstractStringSpec
import io.kotest.core.specs.AbstractWordSpec
import io.kotest.runner.junit4.KotestRunner
import org.junit.runner.RunWith
import io.kotest.should as shouldMatch

@RunWith(KotestRunner::class)
abstract class AnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractAnnotationSpec(body)

@RunWith(KotestRunner::class)
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(body)

@RunWith(KotestRunner::class)
abstract class DescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractDescribeSpec(body)

@RunWith(KotestRunner::class)
abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body)

@RunWith(KotestRunner::class)
abstract class FeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractFeatureSpec(body)

@RunWith(KotestRunner::class)
abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body)

@RunWith(KotestRunner::class)
abstract class FunSpec(body: FunSpec.() -> Unit = {}) : FunSpec(body) {
  @org.junit.Test
  @org.junit.Ignore
  fun primer() {}
}

@RunWith(KotestRunner::class)
abstract class ShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractShouldSpec(body) {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}

@RunWith(KotestRunner::class)
abstract class StringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractStringSpec(body) {
  @org.junit.Test
  @org.junit.Ignore
  fun primer() {}
}

@RunWith(KotestRunner::class)
abstract class WordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractWordSpec(body) {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher

  @org.junit.Test
  @org.junit.Ignore
  fun primer() {}
}
