package io.kotest.extensions.robolectric

import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import kotlin.reflect.KClass

/**
 * Drives Robolectric's per-method lifecycle around a Kotest spec.
 *
 * Implements two extension points:
 *
 * - [ConstructorExtension] - bootstraps the spec class through Robolectric's sandbox
 *   classloader so any Android API references in its body resolve against Robolectric's
 *   instrumented Android types instead of the stub `android.jar`.
 * - [TestCaseExtension] - wraps each test invocation with Robolectric's `before`/`after`
 *   hooks so that things like `RuntimeEnvironment.getApplication()` and the activity
 *   lifecycle are properly initialized.
 *
 * The sandbox is created once per extension instance and reused for every test. This is
 * an explicit trade-off: a fresh sandbox per test would cost seconds of Android class
 * instrumentation for every leaf test, which is impractical. State leakage between tests
 * is mitigated by Robolectric's `before`/`after` reset.
 */
class RobolectricExtension : ConstructorExtension, TestCaseExtension {

   private val sandbox = KotestRobolectricSandbox()

   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      // Load the spec class via the sandbox classloader so that everything the spec body
      // references (Activities, system services, etc.) resolves against Robolectric's
      // instrumented Android stack rather than the stub jar on the regular classpath.
      val bootstrapped = sandbox.bootstrap(clazz.java)
      // Robolectric's bootstrapped class is in a different classloader, so it's not
      // assignment-compatible with the original `Spec` symbol the engine holds. We rely on
      // the io.kotest.* packages being shared (via doNotAcquirePackage) so the loaded class
      // implements the same Spec interface instance the engine sees.
      return bootstrapped.getDeclaredConstructor().newInstance() as Spec
   }

   override suspend fun intercept(
      testCase: TestCase,
      execute: suspend (TestCase) -> TestResult,
   ): TestResult {
      // Hop the entire test - setup, body, teardown - onto Robolectric's sandbox main thread.
      // Robolectric's lifecycle hooks and many of its public APIs (Robolectric.buildActivity,
      // ShadowLooper, etc.) assert they're running on the main thread it manages.
      //
      // The spec body is a `suspend` lambda, but it almost never genuinely suspends in
      // Robolectric tests - the Android lifecycle is synchronous. We bridge to it with
      // `runBlocking` on the main thread; the outer Kotest coroutine waits while the main
      // thread executes.
      return sandbox.sdkEnvironment.runOnMainThread(
         Callable<TestResult> {
            val previousLoader = Thread.currentThread().contextClassLoader
            Thread.currentThread().contextClassLoader = sandbox.sdkEnvironment.robolectricClassLoader
            sandbox.before()
            try {
               runBlocking { execute(testCase) }
            } finally {
               try {
                  sandbox.after()
               } finally {
                  Thread.currentThread().contextClassLoader = previousLoader
               }
            }
         }
      )
   }
}
