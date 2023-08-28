package io.kotest.engine.errors

import io.kotest.common.KotestInternal
import io.kotest.engine.extensions.ExtensionException

/**
 * Given a callback exception, will return an appropriate test name for a placeholder error, as
 * well as extracting the underlying error.
 *
 * These placeholder tests are used to inject information about failed callbacks on platforms
 * that don't allow failed parent tests.
 *
 * For example, on intellij, a "TestSuite" cannot have a state attached, so if an 'afterSpec'
 * callback was to fail, we cannot display the failure. The workaround is to add a dummy test
 * to the test suite, with some name like 'afterSpecFailure' and attach the exception there.
 */
@KotestInternal
object ExtensionExceptionExtractor {

   fun resolve(t: Throwable): Pair<String, Throwable> {
      val cause = t.cause ?: t
      val name = when (t) {
         is ExtensionException -> when (t) {
            is ExtensionException.BeforeProjectException -> "Before Project Error"
            is ExtensionException.AfterProjectException -> "After Project Error"
            is ExtensionException.BeforeEachException -> "Before Each Error"
            is ExtensionException.AfterEachException -> "After Each Error"
            is ExtensionException.BeforeContainerException -> "Before Container Error"
            is ExtensionException.AfterContainerException -> "After Container Error"
            is ExtensionException.BeforeAnyException -> "Before Any Error"
            is ExtensionException.AfterAnyException -> "After Any Error"
            is ExtensionException.BeforeInvocationException -> "Before Invocation Error"
            is ExtensionException.AfterInvocationException -> "After Invocation Error"
            is ExtensionException.BeforeSpecException -> "Before Spec Error"
            is ExtensionException.AfterSpecException -> "After Spec Error"
            is ExtensionException.PrepareSpecException -> "Prepare Spec Error"
            is ExtensionException.FinalizeSpecException -> "Finalize Spec Error"
            is ExtensionException.IgnoredSpecException -> "Ignored Spec Error"
         }
         else -> t::class.simpleName ?: "<error>"
      }
      return Pair(name, cause)
   }
}
