package com.sksamuel.kotest.engine.test.output

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.CurrentTestCase
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.concurrent.thread

@OptIn(ExperimentalKotest::class)
class CurrentTestCaseTest : FunSpec() {
   init {

      test("is the executing test") {
         CurrentTestCase.get()?.name?.name shouldBe "is the executing test"
      }

      test("follows withContext onto another dispatcher") {
         withContext(Dispatchers.Default) {
            CurrentTestCase.get()?.name?.name shouldBe "follows withContext onto another dispatcher"
         }
      }

      test("is inherited by child coroutines") {
         val names = List(10) { async(Dispatchers.IO) { CurrentTestCase.get()?.name?.name } }.map { it.await() }
         names.toSet() shouldBe setOf("is inherited by child coroutines")
         launch(Dispatchers.Default) {
            CurrentTestCase.get()?.name?.name shouldBe "is inherited by child coroutines"
         }.join()
      }

      context("a container") {
         CurrentTestCase.get()?.name?.name shouldBe "a container"
         test("a nested test") {
            CurrentTestCase.get()?.name?.name shouldBe "a nested test"
         }
         CurrentTestCase.get()?.name?.name shouldBe "a container"
      }

      test("is not set on a thread no test coroutine runs on") {
         var name: String? = "unset"
         thread { name = CurrentTestCase.get()?.name?.name }.join()
         name.shouldBeNull()
      }

      test("is carried onto an executor by wrap") {
         val executor = Executors.newSingleThreadExecutor()
         try {
            var name: String? = null
            executor.submit(CurrentTestCase.wrap { name = CurrentTestCase.get()?.name?.name }).get()
            name shouldBe "is carried onto an executor by wrap"
            // and the executor thread is left without a current test afterwards
            executor.submit<String?> { CurrentTestCase.get()?.name?.name }.get().shouldBeNull()
         } finally {
            executor.shutdown()
         }
      }

      test("is carried into a coroutine of a scope the test does not own by asContextElement") {
         val scope = CoroutineScope(Dispatchers.Default)
         scope.async { CurrentTestCase.get() }.await().shouldBeNull()
         scope.async(CurrentTestCase.asContextElement()) { CurrentTestCase.get()?.name?.name }.await() shouldBe
            "is carried into a coroutine of a scope the test does not own by asContextElement"
      }

      afterSpec {
         CurrentTestCase.get().shouldBeNull()
      }
   }
}
