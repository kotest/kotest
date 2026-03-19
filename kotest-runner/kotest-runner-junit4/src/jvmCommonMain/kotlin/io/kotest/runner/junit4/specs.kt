package io.kotest.runner.junit4

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
abstract class FunSpec(body: FunSpec.() -> Unit = {}) :
   FunSpec(body)

@RunWith(KotestTestRunner::class)
abstract class StringSpec(body: StringSpec.() -> Unit = {}) :
   StringSpec(body)

@RunWith(KotestTestRunner::class)
abstract class ShouldSpec(body: ShouldSpec.() -> Unit = {}) :
   ShouldSpec(body)

@RunWith(KotestTestRunner::class)
abstract class DescribeSpec(body: DescribeSpec.() -> Unit = {}) :
   DescribeSpec(body)

@RunWith(KotestTestRunner::class)
abstract class ExpectSpec(body: ExpectSpec.() -> Unit = {}) :
   ExpectSpec(body)

@RunWith(KotestTestRunner::class)
abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) :
   FeatureSpec(body)

@RunWith(KotestTestRunner::class)
abstract class FreeSpec(body: FreeSpec.() -> Unit = {}) :
   FreeSpec(body)

@RunWith(KotestTestRunner::class)
abstract class WordSpec(body: WordSpec.() -> Unit = {}) :
   WordSpec(body)

@RunWith(KotestTestRunner::class)
abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) :
   BehaviorSpec(body)


