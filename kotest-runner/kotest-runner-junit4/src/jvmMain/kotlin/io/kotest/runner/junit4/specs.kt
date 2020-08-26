package io.kotest.runner.junit4

import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
abstract class FunSpec(body: io.kotest.core.spec.style.FunSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.FunSpec(body)

@RunWith(KotestTestRunner::class)
abstract class StringSpec(body: io.kotest.core.spec.style.StringSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.StringSpec(body)

@RunWith(KotestTestRunner::class)
abstract class ShouldSpec(body: io.kotest.core.spec.style.ShouldSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.ShouldSpec(body)

@RunWith(KotestTestRunner::class)
abstract class DescribeSpec(body: io.kotest.core.spec.style.DescribeSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.DescribeSpec(body)

@RunWith(KotestTestRunner::class)
abstract class ExpectSpec(body: io.kotest.core.spec.style.ExpectSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.ExpectSpec(body)

@RunWith(KotestTestRunner::class)
abstract class FeatureSpec(body: io.kotest.core.spec.style.FeatureSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.FeatureSpec(body)

@RunWith(KotestTestRunner::class)
abstract class FreeSpec(body: io.kotest.core.spec.style.FreeSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.FreeSpec(body)

@RunWith(KotestTestRunner::class)
abstract class WordSpec(body: io.kotest.core.spec.style.WordSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.WordSpec(body)

@RunWith(KotestTestRunner::class)
abstract class BehaviorSpec(body: io.kotest.core.spec.style.BehaviorSpec.() -> Unit = {}) :
   io.kotest.core.spec.style.BehaviorSpec(body)


