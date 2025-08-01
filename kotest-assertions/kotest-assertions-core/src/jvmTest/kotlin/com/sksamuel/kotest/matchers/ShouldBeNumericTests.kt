package com.sksamuel.kotest.matchers

import com.sksamuel.kotest.JavaFoo
import io.kotest.assertions.shouldFail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.lang.Long
import java.math.BigInteger
import kotlin.Int

@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldBeNumericTests : WordSpec({
   "should be" should {
      "support combinations of numerics" {

         val v1: Int = JavaFoo.JavaLong.toInt()
         val v2 = Integer.valueOf(42)
         val v3 = JavaFoo.JavaLong
         val v4 = Long.valueOf(42)
         val v5 = JavaFoo.JavaLong
         val v6 = 42.0
         val v7 = JavaFoo.JavaDouble

         v1 shouldBe 42
         v2 shouldBe 42
         v3 shouldBe 42L
         v4 shouldBe 42L
         v5 shouldBe 42L
         42 shouldBe v1
         42 shouldBe v2
         42 shouldBe v3
         42 shouldBe v4
         42 shouldBe v5
         v6 shouldBe 42F
         v7 shouldBe 42F
         42F shouldBe v6
         42F shouldBe v7
      }

      "Prints type information on mismatching types" {
         shouldFail {
            1 shouldBe BigInteger.ONE
         }.message shouldBe "expected:java.math.BigInteger<1> but was:kotlin.Int<1>"
      }

      "handle equal numbers with different hashcodes" {
         0.0 shouldBe -0.0
         -0.0 shouldBe 0.0
         0 shouldBe -0
         -0 shouldBe 0
         0.0f shouldBe -0.0f
         -0.0f shouldBe 0.0f
         0L shouldBe -0L
         -0L shouldBe 0L
         0.toShort() shouldBe (-0).toShort()
         (-0).toShort() shouldBe 0.toShort()
         0.toByte() shouldBe (-0).toByte()
         (-0).toByte() shouldBe 0.toByte()
      }
   }
})
