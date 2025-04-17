package com.sksamuel.kotest.engine.spec.style

import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.Paths
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class FunSpecTest : FunSpec() {

   var count = 0

   init {

      test("test without config") {
         "hello world".shouldStartWith("hello")
      }

      test("test with config").config(enabled = true) {
         assertSoftly {
            val path = Paths.get(".password")
            path.shouldNotExist()
         }
      }

      test("test with timeout").config(timeout = 1234.milliseconds) {
         count += 1
      }

      xtest("a disabled test") {
         error("boom")
      }

      context("a context can hold tests") {
         test("foo") {
            "a".shouldNotBeBlank()
         }
         xtest("a disabled test inside the context") {
            error("boom")
         }
         xtest("a disabled test with config").config(timeout = 1231.hours) {
            error("boom")
         }
         context("and even other contexts!") {
            test("wibble") {
               "hello".shouldHaveLength(5)
            }
         }
      }

      context("a context with coroutine in fun spec context scope") {
         launch { delay(1) }
         test("a dummy test") {

         }
      }

      xcontext("a disabled context!") {
         error("boom")
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      count.shouldBe(1)
   }
}
