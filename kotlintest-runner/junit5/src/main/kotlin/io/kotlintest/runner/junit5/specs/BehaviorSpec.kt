package io.kotlintest.runner.junit5.specs

import io.kotlintest.specs.AbstractBehaviorSpec
import io.kotlintest.specs.AbstractDescribeSpec
import io.kotlintest.specs.AbstractExpectSpec
import io.kotlintest.specs.AbstractFeatureSpec
import io.kotlintest.specs.AbstractFreeSpec
import io.kotlintest.specs.AbstractFunSpec
import io.kotlintest.specs.AbstractShouldSpec
import io.kotlintest.specs.AbstractStringSpec
import io.kotlintest.specs.AbstractWordSpec
import org.junit.jupiter.api.Test

interface IntelliTestMarker {
  @Test
  fun foo() {
  }
}

abstract class BehaviorSpec(body: AbstractBehaviorSpec.() -> Unit = {}) : AbstractBehaviorSpec(body), IntelliTestMarker
abstract class DescribeSpec(body: AbstractDescribeSpec.() -> Unit = {}) : AbstractDescribeSpec(body), IntelliTestMarker
abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body), IntelliTestMarker
abstract class FeatureSpec(body: AbstractFeatureSpec.() -> Unit = {}) : AbstractFeatureSpec(body), IntelliTestMarker
abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body), IntelliTestMarker
abstract class FunSpec(body: AbstractFunSpec.() -> Unit = {}) : AbstractFunSpec(body), IntelliTestMarker
abstract class ShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractShouldSpec(body), IntelliTestMarker
abstract class StringSpec(body: AbstractStringSpec.() -> Unit = {}) : AbstractStringSpec(body), IntelliTestMarker
abstract class WordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractWordSpec(body), IntelliTestMarker
