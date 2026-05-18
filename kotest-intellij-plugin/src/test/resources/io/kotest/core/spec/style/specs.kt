package io.kotest.core.spec.style

abstract class FunSpec(body: FunSpec.() -> Unit = {})

abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {})

abstract class StringSpec(body: StringSpec.() -> Unit = {})

abstract class FreeSpec(body: FreeSpec.() -> Unit = {})

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {})

abstract class WordSpec(body: WordSpec.() -> Unit = {})

abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {})

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {})

abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {})

abstract class CustomSpec

abstract class AnnotationSpec() {
   annotation class Test
   annotation class BeforeEach
   annotation class Before
   annotation class BeforeAll
   annotation class BeforeClass
   annotation class AfterEach
   annotation class After
   annotation class AfterAll
   annotation class AfterClass
   annotation class Ignore
   annotation class Nested
}
