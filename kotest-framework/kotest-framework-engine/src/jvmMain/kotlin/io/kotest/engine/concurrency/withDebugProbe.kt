package io.kotest.engine.concurrency

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes

@ExperimentalCoroutinesApi
internal actual inline fun <T> withDebugProbe(f: () -> T): T {
   return if (!DebugProbes.isInstalled) {
      DebugProbes.enableCreationStackTraces = true
      DebugProbes.sanitizeStackTraces = true
      DebugProbes.install()
      try {
         val t = f()
         DebugProbes.dumpCoroutines()
         t
      } catch (t: Throwable) {
         DebugProbes.dumpCoroutines()
         throw t
      } finally {
         DebugProbes.uninstall()
      }
   } else {
      f()
   }
}
