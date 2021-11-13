package io.kotest.engine.concurrency

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes

@ExperimentalCoroutinesApi
internal actual inline fun <T> withDebugProbe(f: () -> T): T {
   DebugProbes.enableCreationStackTraces = false
   DebugProbes.sanitizeStackTraces = true
   return if (!DebugProbes.isInstalled) {
      DebugProbes.install()
      try {
         f()
      } finally {
         DebugProbes.uninstall()
      }
   } else {
      f()
   }
}
