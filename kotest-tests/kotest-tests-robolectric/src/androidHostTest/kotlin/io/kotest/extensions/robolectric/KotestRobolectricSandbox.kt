package io.kotest.extensions.robolectric

import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.internal.bytecode.InstrumentationConfiguration

/**
 * A [RobolectricTestRunner] that exposes Robolectric's per-method lifecycle hooks for use
 * outside of a JUnit 4 runner. Kotest-managed specs cannot be discovered by JUnit 4's
 * `@RunWith` machinery, so we run a placeholder method through this runner just to get
 * access to its `before` / `after` hooks.
 *
 * The instrumentation configuration is extended to *not* acquire any class under
 * `io.kotest.` - those classes must be loaded by the system class loader so that the test
 * engine, matchers, and our own extension classes share identity across the spec body.
 */
internal class KotestRobolectricSandbox : RobolectricTestRunner(PlaceholderTest::class.java) {

   private val placeholderMethod: FrameworkMethod by lazy { children.first() }

   /**
    * The bootstrapped class corresponding to [placeholderMethod] - lazily resolved through
    * the sandbox classloader. Robolectric's superclass uses this to drive its per-method
    * lifecycle.
    */
   private val bootstrappedMethod: java.lang.reflect.Method by lazy {
      sdkEnvironment.bootstrappedClass<Any>(testClass.javaClass).getMethod(placeholderMethod.name)
   }

   /**
    * Lazily-built sandbox environment. Creating a sandbox is expensive (it instruments
    * every Android class), so we want to do it once per [KotestRobolectricSandbox] instance.
    */
   val sdkEnvironment by lazy {
      getSandbox(placeholderMethod).also { configureSandbox(it, placeholderMethod) }
   }

   /**
    * Run Robolectric's pre-test setup: initialize the [android.app.Application], wire up
    * shadows, etc. Must be called before any Android API is used inside a Kotest test.
    *
    * The caller is expected to already be on Robolectric's sandbox main thread (the
    * superclass hook touches the main Looper, Choreographer, etc. and asserts that). The
    * extension that drives this sandbox arranges that via [sdkEnvironment]'s
    * [org.robolectric.internal.bytecode.Sandbox.runOnMainThread] before invoking us.
    */
   fun before() {
      super.beforeTest(sdkEnvironment, placeholderMethod, bootstrappedMethod)
   }

   /**
    * Run Robolectric's post-test teardown: reset state and run finally blocks. Same
    * main-thread caveat as [before].
    */
   fun after() {
      try {
         super.afterTest(placeholderMethod, bootstrappedMethod)
      } finally {
         super.finallyAfterTest(placeholderMethod)
      }
   }

   /**
    * Bootstraps the supplied class through the sandbox classloader. The returned [Class]
    * is loaded with Robolectric's instrumented Android stack on its classpath, so reflective
    * calls to its methods pick up shadowed Android types instead of the empty `android.jar`.
    */
   fun <T : Any> bootstrap(clazz: Class<T>): Class<T> {
      @Suppress("UNCHECKED_CAST")
      return sdkEnvironment.bootstrappedClass<Any>(clazz) as Class<T>
   }

   override fun createClassLoaderConfig(method: FrameworkMethod?): InstrumentationConfiguration {
      return InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method))
         .doNotAcquirePackage("io.kotest")
         .build()
   }

   /**
    * Robolectric's [RobolectricTestRunner] requires a real test class to bootstrap a sandbox
    * against. The class itself is never executed - only its lifecycle hooks are.
    */
   class PlaceholderTest {
      @org.junit.Test
      fun placeholder() = Unit
   }
}
