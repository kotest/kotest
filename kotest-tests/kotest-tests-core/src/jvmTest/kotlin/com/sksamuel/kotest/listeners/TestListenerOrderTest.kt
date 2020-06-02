package com.sksamuel.kotest.listeners

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

private val accum: StringBuilder = StringBuilder()

open class TestListenerOrderParentTest : FunSpec() {
   init {
      beforeSpec {
         accum.append("a")
      }

      beforeTest {
         accum.append("c")
      }

      afterTest {
         accum.append("j")
      }

      afterSpec {
         accum.append("l")
      }


      listener(
         object : TestListener {
            override suspend fun beforeInvocation(testCase: TestCase, iteration: Int) {
               accum.append("e")
            }

            override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
               accum.append("h")
            }
         }
      )
   }
}

class TestListenerOrderChildTest : TestListenerOrderParentTest() {
   init {
      beforeSpec {
         accum.append("b")
      }

      beforeTest {
         accum.append("d")
      }

      afterTest {
         accum.append("i")
      }

      afterSpec {
         accum.append("k")
      }

      listener(
         object : TestListener {
            override suspend fun beforeInvocation(testCase: TestCase, iteration: Int) {
               accum.append("f")
            }

            override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
               accum.append("g")
            }
         }
      )

      afterProject {
         accum.toString() shouldBe "abcdefghijkl"
      }

      test("order of after* callbacks execution should be from child to parent") { }
   }
}
