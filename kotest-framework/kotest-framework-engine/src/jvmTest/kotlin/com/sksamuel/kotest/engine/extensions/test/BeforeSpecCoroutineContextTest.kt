package com.sksamuel.kotest.engine.extensions.test

import io.kotest.common.TestNameContextElement
import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

@Description("Tests that a before spec listener is executed in the correct coroutine context")
class BeforeSpecCoroutineContextTest : FunSpec() {
   init {
      var name = ""
      beforeSpec {
         name = coroutineContext[CoroutineName]!!.name
         coroutineContext[TestNameContextElement] shouldBe null
      }
      test("before spec listener should be executed in the correct coroutine context") {
         name shouldBe "before-spec"
      }
   }
}

@Description("Tests that an after spec listener is executed in the correct coroutine context")
class AfterSpecCoroutineContextTest : FunSpec() {
   init {
      var name = ""
      afterSpec {
         name = coroutineContext[CoroutineName]!!.name
         coroutineContext[TestNameContextElement] shouldBe null
      }
      afterProject {
         name shouldBe "after-spec"
      }
      test("placeholder") {
      }
   }
}

@Description("Tests that coroutines launched in the beforeSpec listeners are isolated from the test coroutines")
class BeforeSpecJobIsolationTest : FunSpec() {

   private lateinit var job: Job

   init {
      beforeSpec {
         job = CoroutineScope(coroutineContext).launch { delay(Duration.INFINITE) }
      }

      afterSpec {
         job.cancel()
      }

      test("always pass") {
      }
   }
}
