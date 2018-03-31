package io.kotlintest.specs

import io.kotlintest.Matcher
import io.kotlintest.should as shouldMatch

interface IntelliTestMarker {
  @org.junit.jupiter.api.Test
  fun foo() {
  }
}

abstract class AnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractAnnotationSpec(body), IntelliTestMarker
abstract class BehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractBehaviorSpec(body), IntelliTestMarker
abstract class DescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractDescribeSpec(body), IntelliTestMarker
abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body), IntelliTestMarker
abstract class FeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractFeatureSpec(body), IntelliTestMarker
abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body), IntelliTestMarker
abstract class FunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractFunSpec(body), IntelliTestMarker
abstract class ShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractShouldSpec(body), IntelliTestMarker {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}

abstract class StringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractStringSpec(body), IntelliTestMarker
abstract class WordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractWordSpec(body), IntelliTestMarker {
  // need to overload this so that when doing "string" should haveLength(5) in a word spec, we don't
  // clash with the other should method
  infix fun String.should(matcher: Matcher<String>) = this shouldMatch matcher
}
