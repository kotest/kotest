package io.kotest.core.coroutines

import io.kotest.common.KotestInternal
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext

@KotestInternal
class TestScopeContainer(val testScope: TestScope) : CoroutineContext.Element {
   companion object : CoroutineContext.Key<TestScopeContainer>

   override val key: CoroutineContext.Key<*>
      get() = TestScopeContainer
}

val io.kotest.core.test.TestScope.coroutineTestScope: TestScope
   get() = this.coroutineContext[TestScopeContainer]?.testScope
      ?: error("kotlinx.coroutines.test.TestScope is not installed. Set coroutineTestScope = true to enable")
