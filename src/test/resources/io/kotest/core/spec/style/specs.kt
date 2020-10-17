package io.kotest.core.spec.style

abstract class FunSpec(body: FunSpec.() -> Unit = {})

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {})

abstract class StringSpec(body: DescribeSpec.() -> Unit = {})

abstract class FreeSpec(body: DescribeSpec.() -> Unit = {})

abstract class BehaviorSpec(body: DescribeSpec.() -> Unit = {})

abstract class WordSpec(body: DescribeSpec.() -> Unit = {})

abstract class ExpectSpec(body: DescribeSpec.() -> Unit = {})

abstract class FeatureSpec(body: DescribeSpec.() -> Unit = {})

abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {})

abstract class AnnotationSpec()
