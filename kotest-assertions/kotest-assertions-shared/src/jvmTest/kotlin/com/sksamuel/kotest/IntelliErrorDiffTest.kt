package com.sksamuel.kotest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// checks that errors are in the intelli format for showing a diff link
@EnabledIf(LinuxOnlyGithubCondition::class)
class IntelliErrorDiffTest : FunSpec({
   test("shouldBe should output in intelli format") {
      shouldThrow<AssertionError> {
         "a" shouldBe "b"
      }.message shouldBe """expected:<"b"> but was:<"a">"""
   }
   test("diff format should distinguish between numbers and strings") {
      shouldThrow<AssertionError> {
         "1" shouldBe 1
      }.message shouldBe """expected:kotlin.Int<1> but was:kotlin.String<"1">"""
   }
})
