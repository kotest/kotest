package io.kotlintest.specs

import io.kotlintest.Matcher
import io.kotlintest.should as shouldMatch

abstract class AnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractAnnotationSpec(body)
abstract class BehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractBehaviorSpec(body)
abstract class DescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractDescribeSpec(body)
abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body)
abstract class FeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractFeatureSpec(body)
abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body)
abstract class FunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractFunSpec(body)
abstract class ShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractShouldSpec(body) {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}

abstract class StringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractStringSpec(body)
abstract class WordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractWordSpec(body) {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}
