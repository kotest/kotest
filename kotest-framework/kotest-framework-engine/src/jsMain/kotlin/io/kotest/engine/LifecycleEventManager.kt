package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.fp.Try
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual class LifecycleEventManager {

   @DelicateCoroutinesApi
   private fun execute(name: String, f: suspend () -> Unit) {
      describe(name) {
         it(name) { done ->
            GlobalScope.promise {
               Try { f() }.fold({ done(it) }, { done(null) })
            }
            // we don't want to return a promise here as the js frameworks will use that for test resolution
            // instead of the done callback, and we prefer the callback as it allows for custom timeouts
            Unit
         }
      }
   }

   actual fun beforeProject(listeners: List<BeforeProjectListener>) {
      if (listeners.isNotEmpty())
         execute("beforeProject") {
            listeners.forEach { it.beforeProject() }
         }
   }

   actual fun afterProject(listeners: List<AfterProjectListener>) {
      if (listeners.isNotEmpty())
         execute("afterProject") {
            listeners.forEach { it.afterProject() }
         }
   }
}
