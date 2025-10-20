package io.kotest.engine.coroutines

import io.kotest.core.test.TestScope
import kotlinx.coroutines.CoroutineScope

/**
 * Returns the kotlin.test [CoroutineScope] background scope associated with this Kotest test.
 *
 * This element is available when coroutineTestScope is set to true.
 */
val TestScope.backgroundScope: CoroutineScope
   get() {
      return coroutineTestScope.backgroundScope
   }
