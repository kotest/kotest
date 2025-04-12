package com.sksamuel.kotest.equals

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual

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
   }
}
