package io.kotest.plugin.intellij.run

import com.intellij.openapi.components.Service
/**
 * Project-level service that temporarily holds state needed to pass information
 * from a gutter action to the run configuration producer.
 *
 * The [pendingInvocationCount], when non-null, signals that the next run configuration
 * should include the KOTEST_INVOCATION_COUNT environment variable. It is cleared immediately
 * after being consumed by whichever producer handles the run.
 */
@Service(Service.Level.PROJECT)
class KotestRunState {
   var pendingInvocationCount: Int? = null
   fun clearPendingInvocationCount() {
      pendingInvocationCount = null
   }
}
