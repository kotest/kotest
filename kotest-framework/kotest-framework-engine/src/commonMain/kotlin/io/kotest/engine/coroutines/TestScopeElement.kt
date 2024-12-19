package io.kotest.engine.coroutines

import io.kotest.common.KotestInternal
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext

@KotestInternal
class TestScopeElement(val testScope: TestScope) : CoroutineContext.Element {
   companion object : CoroutineContext.Key<TestScopeElement>

   override val key: CoroutineContext.Key<*>
      get() = TestScopeElement
}

val io.kotest.core.test.TestScope.coroutineTestScope: TestScope
   get() = this.coroutineContext[TestScopeElement]?.testScope
      ?: error("kotlinx.coroutines.test.TestScope is not installed. Set coroutineTestScope = true to enable")
