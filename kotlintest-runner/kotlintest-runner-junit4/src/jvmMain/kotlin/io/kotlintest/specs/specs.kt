package io.kotlintest.specs

import io.kotlintest.Matcher
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith
import io.kotlintest.should as shouldMatch

@RunWith(KotlinTestRunner::class)
abstract class AnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractAnnotationSpec(body)

@RunWith(KotlinTestRunner::class)
abstract class BehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractBehaviorSpec(body)

@RunWith(KotlinTestRunner::class)
abstract class DescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractDescribeSpec(body)

@RunWith(KotlinTestRunner::class)
abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body)

@RunWith(KotlinTestRunner::class)
abstract class FeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractFeatureSpec(body)

@RunWith(KotlinTestRunner::class)
abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body)

@RunWith(KotlinTestRunner::class)
abstract class FunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractFunSpec(body) {
  @org.junit.Test
  @org.junit.Ignore
  fun primer() {}
}

@RunWith(KotlinTestRunner::class)
abstract class ShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractShouldSpec(body) {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}

@RunWith(KotlinTestRunner::class)
abstract class StringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractStringSpec(body) {
  @org.junit.Test
  @org.junit.Ignore
  fun primer() {}
}

@RunWith(KotlinTestRunner::class)
abstract class WordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractWordSpec(body) {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher

  @org.junit.Test
  @org.junit.Ignore
  fun primer() {}
}
