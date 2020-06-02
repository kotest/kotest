package com.sksamuel.kotest.listeners

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

private val accum: StringBuilder = StringBuilder()

open class TestListenerAfterParentTest : FunSpec() {
   init {
      afterSpec {
         accum.append("f")
      }

      afterTest {
         accum.append("d")
      }

      listener(
         object : TestListener {
            override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
               accum.append("b")
            }
         }
      )
   }
}

class TestListenerAfterChildTest : TestListenerAfterParentTest() {
   init {
      afterSpec {
         accum.append("e")
      }

      afterTest {
         accum.append("c")
      }

      listener(
         object : TestListener {
            override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
               accum.append("a")
            }
         }
      )

      afterProject {
         accum.toString() shouldBe "abcdef"
      }

      test("order of after* callbacks execution should be from child to parent") { }
   }
}
