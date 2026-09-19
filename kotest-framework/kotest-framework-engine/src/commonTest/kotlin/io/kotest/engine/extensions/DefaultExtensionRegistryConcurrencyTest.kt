package io.kotest.engine.extensions

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

private class NoopExtension : Extension

// DefaultExtensionRegistry.add() is invoked whenever a spec is inflated (SpecRefInflator.inflate),
// which can happen concurrently across real threads under SpecExecutionMode.Concurrent (JVM) or
// TestExecutionMode.Concurrent (any platform, not JVM-gated) -- see kotest#6188.
class DefaultExtensionRegistryConcurrencyTest : FunSpec({

   test("concurrent add() calls should not lose entries") {
      val registry = DefaultExtensionRegistry()
      val count = 500

      coroutineScope {
         (1..count).map {
            async(Dispatchers.Default) {
               registry.add(NoopExtension())
            }
         }.awaitAll()
      }

      registry.all() shouldHaveSize count
   }
})
