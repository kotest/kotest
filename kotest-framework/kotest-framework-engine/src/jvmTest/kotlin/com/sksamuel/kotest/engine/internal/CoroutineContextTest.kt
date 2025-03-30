package com.sksamuel.kotest.engine.internal

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class FooContext(val value: Any) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<FooContext>
}

/** Gets the contextual foo value from a test */
suspend fun foo(): Any? = coroutineContext[FooContext]?.value

// the coroutine context used by a test case should inherit the context from a test case extension
@EnabledIf(LinuxOnlyGithubCondition::class)
class CoroutineContextTest : FunSpec() {
   init {
      aroundTest { (testCase, execute) ->
         val fooValue = 42
         withContext(FooContext(fooValue)) {
            execute(testCase)
         }
      }

      test("should have contextual value") {
         println("test context $coroutineContext")
         foo().shouldNotBeNull()
      }
   }
}
