package com.sksamuel.kotest.equals

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldBeEqualTest : FunSpec() {
   init {
      test("two values should be equal if their equals agrees") {
         class Foo {
            override fun equals(other: Any?): Boolean {
               return true
            }
         }
         Foo() shouldBeEqual Foo()
      }
      test("two values should not be equal if their equals disagree") {
         class Foo {
            override fun equals(other: Any?): Boolean {
               return false
            }
         }
         Foo() shouldNotBeEqual Foo()
      }
      test("should generate diff") {
         val listA = listOf(1, 2, 3, 4)
         val listB = listOf(1, 2, 3)
         shouldThrow<AssertionError> { listA shouldBeEqual listB }.message shouldBe """[1, 2, 3, 4] should be equal to [1, 2, 3]
expected:<[1, 2, 3]> but was:<[1, 2, 3]>"""
      }
   }
}
