package io.kotlintest.specs

import io.kotlintest.Matcher
import io.kotlintest.should as shouldMatch

interface IntellijTestMarker

abstract class AnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractAnnotationSpec(body), IntellijTestMarker
abstract class BehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractBehaviorSpec(body), IntellijTestMarker
abstract class DescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractDescribeSpec(body), IntellijTestMarker
abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body), IntellijTestMarker
abstract class FeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractFeatureSpec(body), IntellijTestMarker
abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body), IntellijTestMarker
abstract class FunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractFunSpec(body), IntellijTestMarker
abstract class ShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractShouldSpec(body), IntellijTestMarker {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}

abstract class StringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractStringSpec(body), IntellijTestMarker
abstract class WordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractWordSpec(body), IntellijTestMarker {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}
