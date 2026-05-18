package io.kotest.extensions.robolectric

import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.internal.bytecode.InstrumentationConfiguration
import java.lang.reflect.Method

/**
 * A thin subclass of [RobolectricTestRunner] whose only purpose is to expose
 * the protected per-method lifecycle hooks of the runner so that they can be
 * driven from outside JUnit 4's [org.junit.runner.Runner] machinery.
 *
 * ## Why we need this
 *
 * [RobolectricTestRunner] is built to be invoked by JUnit 4. JUnit 4 picks the
 * runner up via `@RunWith`, hands it the test class, and the runner walks every
 * `@Test` method, building a "FrameworkMethod" for each, instantiating the test,
 * then running the JUnit 4 lifecycle for the method (`@Before`, the body, `@After`).
 *
 * Kotest specs are discovered by the Kotest engine. There is no `@RunWith`
 * involved and no `@Test`-annotated methods to enumerate. We therefore cannot
 * just hand a spec class to a real [RobolectricTestRunner] and call `run` on it.
 *
 * What we *can* do is construct a [RobolectricTestRunner] against a tiny
 * placeholder class that has a single `@Test` method. The runner is happy —
 * it sees a class it can run — and we get a working [FrameworkMethod] and
 * sandbox out of it. Then, instead of running the placeholder, we call the
 * runner's protected lifecycle hooks ourselves at the points in the Kotest
 * lifecycle where Robolectric's setup and teardown belong.
 *
 * The lifecycle hooks we delegate to:
 *
 * - [getSandbox] — builds (or retrieves from cache) an [org.robolectric.internal.AndroidSandbox]
 *   configured for the SDK chosen for the current method. The sandbox owns the
 *   instrumented classloader.
 * - [configureSandbox] — wires SDK-specific modes (SQLite, Looper, Graphics, …)
 *   into the sandbox.
 * - [beforeTest] — initialises Robolectric's [android.app.Application], the
 *   [android.os.Looper] for the test thread, and installs all shadow classes.
 * - [afterTest] — tears down the application and runs the per-test [org.robolectric.TestLifecycle] teardown.
 * - [finallyAfterTest] — resets static Android state so the next test starts
 *   from a known baseline (Robolectric calls this in the equivalent of a
 *   finally block).
 *
 * ## Class loader configuration
 *
 * Robolectric instruments classes by loading them through its own
 * [org.robolectric.internal.bytecode.SandboxClassLoader]. By default that
 * classloader acquires everything it can find on the classpath. For our
 * purposes we *must* exclude classes from `io.kotest.*` from that acquisition:
 * if both the system classloader and the Robolectric classloader define the
 * same Kotest class, the engine will treat the spec returned from the sandbox
 * as a different type and the `is Spec` checks the engine performs against it
 * will fail. [createClassLoaderConfig] is overridden below to set that up.
 */
internal class ContainedRobolectricRunner :
   RobolectricTestRunner(PlaceholderTest::class.java) {

   /**
    * The placeholder `@Test` method. We never actually execute its body; we
    * only need a [FrameworkMethod] handle so we can pass it to the protected
    * lifecycle hooks inherited from [RobolectricTestRunner].
    *
    * `children` is populated by JUnit 4 when the runner is constructed; index
    * 0 is always our single placeholder method.
    */
   private val placeholderMethod: FrameworkMethod by lazy { children[0] }

   /**
    * The same placeholder method, but resolved through the *sandbox* classloader.
    *
    * Robolectric's [beforeTest] hook is given two views of the same method:
    * the original [FrameworkMethod] (which lives in the system classloader)
    * and a [Method] resolved against the bootstrapped class (which lives in
    * the sandbox classloader). The two are required because the test
    * environment configures itself by reading annotations and reflection
    * information from the bootstrapped copy.
    */
   private val bootstrappedMethod: Method by lazy {
      sdkEnvironment
         .bootstrappedClass<Any>(testClass.javaClass)
         .getMethod(placeholderMethod.name)
   }

   /**
    * Lazily-built sandbox, cached for the lifetime of this runner instance.
    *
    * Sandbox construction is **expensive** — it scans, downloads, and
    * instruments the entire Android framework jar for the chosen SDK level —
    * so we want to do it exactly once per [ContainedRobolectricRunner]. The
    * extension wires that up by holding a single [ContainedRobolectricRunner]
    * per spec instance.
    *
    * Calling [configureSandbox] immediately after [getSandbox] mirrors what
    * [RobolectricTestRunner.run] does internally: SDK-specific modes need to
    * be applied to the sandbox before any test code runs.
    */
   val sdkEnvironment by lazy {
      getSandbox(placeholderMethod).also { sandbox ->
         configureSandbox(sandbox, placeholderMethod)
      }
   }

   /**
    * Runs Robolectric's pre-test setup against the cached sandbox.
    *
    * This is the equivalent of what [RobolectricTestRunner.beforeTest] is
    * called for in JUnit 4 — it bootstraps the [android.app.Application],
    * sets up the main Looper, wires shadow classes, etc.
    *
    * The caller is expected to already be on the sandbox's main thread:
    * Robolectric's hooks touch the main Looper and Choreographer, both of
    * which assert they're being invoked from the thread the sandbox owns.
    * The extension arranges this via `sdkEnvironment.runOnMainThread { ... }`.
    */
   fun containedBefore() {
      // beforeTest is protected on RobolectricTestRunner; subclassing is the
      // only way to call it without reflection.
      super.beforeTest(sdkEnvironment, placeholderMethod, bootstrappedMethod)
   }

   /**
    * Runs Robolectric's post-test teardown.
    *
    * This is split into two phases by the parent class:
    *
    * 1. [afterTest] - tears down the application, runs the per-test
    *    `TestLifecycle.afterTest` hook.
    * 2. [finallyAfterTest] - resets static state so the next test gets a
    *    clean slate. The parent class is careful to invoke it from a
    *    `finally` block, and we mirror that pattern here.
    */
   fun containedAfter() {
      try {
         super.afterTest(placeholderMethod, bootstrappedMethod)
      } finally {
         super.finallyAfterTest(placeholderMethod)
      }
   }

   /**
    * Loads [clazz] through the sandbox's instrumented classloader.
    *
    * The returned [Class] is bytecode-identical to the original but lives in
    * a classloader where references to `android.*` resolve to Robolectric's
    * shadowed implementations rather than the stub `android.jar` shipped
    * with the host JDK.
    *
    * Used by [RobolectricExtension] to bootstrap the spec class so that
    * Activity/View references in its body resolve correctly.
    */
   fun <T : Any> bootstrap(clazz: Class<T>): Class<T> {
      @Suppress("UNCHECKED_CAST")
      return sdkEnvironment.bootstrappedClass<Any>(clazz) as Class<T>
   }

   /**
    * Extends the inherited classloader configuration to exclude the
    * `io.kotest` package from sandbox acquisition.
    *
    * Without this, the sandbox classloader would load its own copy of
    * `io.kotest.core.spec.Spec`, and the [Spec][io.kotest.core.spec.Spec]
    * instance returned to the engine from [RobolectricExtension.instantiate]
    * would not be assignment-compatible with the engine's own `Spec`
    * symbol — they would be two distinct `Class<*>` objects with the same
    * name but loaded by different classloaders.
    *
    * By calling [InstrumentationConfiguration.Builder.doNotAcquirePackage],
    * we delegate `io.kotest.*` lookups to the parent classloader, so the
    * engine, matchers, and our extension classes share identity across the
    * sandbox boundary.
    */
   override fun createClassLoaderConfig(method: FrameworkMethod?): InstrumentationConfiguration {
      return InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method))
         .doNotAcquirePackage("io.kotest")
         .build()
   }

   /**
    * The minimal class that satisfies [RobolectricTestRunner]'s requirement
    * that it be constructed against a class with at least one `@Test`-annotated
    * method.
    *
    * The body of [placeholder] is never executed: the runner is constructed
    * solely so we can borrow its per-method lifecycle hooks. JUnit 4 needs
    * to see *something* in `children`, so we give it one no-op method.
    */
   class PlaceholderTest {
      @org.junit.Test
      fun placeholder() = Unit
   }
}
