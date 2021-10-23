package io.kotest.engine.errors

import io.kotest.engine.project.AfterProjectException
import io.kotest.engine.project.BeforeProjectException
import io.kotest.engine.spec.AfterSpecException
import io.kotest.engine.spec.BeforeSpecException
import io.kotest.engine.test.AfterAnyException
import io.kotest.engine.test.AfterContainerException
import io.kotest.engine.test.AfterEachException
import io.kotest.engine.test.AfterInvocationException
import io.kotest.engine.test.AfterTestException
import io.kotest.engine.test.BeforeAnyException
import io.kotest.engine.test.BeforeContainerException
import io.kotest.engine.test.BeforeEachException
import io.kotest.engine.test.BeforeInvocationException
import io.kotest.engine.test.BeforeTestException

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
object ExceptionPlaceholderNameResolver {

   fun resolve(t: Throwable): Pair<String, Throwable> {
      val cause = t.cause ?: t
      val name = when (t) {
         is BeforeProjectException -> "Before Project Error"
         is AfterProjectException -> "After Project Error"
         is BeforeTestException -> "Before Test Error"
         is AfterTestException -> "After Test Error"
         is BeforeEachException -> "Before Project Error"
         is AfterEachException -> "After Each Error"
         is BeforeContainerException -> "Before Container Error"
         is AfterContainerException -> "After Container Error"
         is BeforeAnyException -> "Before Any Error"
         is AfterAnyException -> "After Any Error"
         is BeforeInvocationException -> "Before Invocation Error"
         is AfterInvocationException -> "After Invocation Error"
         is BeforeSpecException -> "Before Spec Error"
         is AfterSpecException -> "After Spec Error"
         else -> t::class.simpleName ?: "<error>"
      }
      return Pair(name, cause)
   }
}
