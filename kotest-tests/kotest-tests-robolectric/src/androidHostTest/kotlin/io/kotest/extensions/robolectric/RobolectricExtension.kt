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
 * Kotest extension that gives specs the same execution environment that
 * [`@RunWith(RobolectricTestRunner::class)`][org.robolectric.RobolectricTestRunner]
 * gives JUnit 4 tests.
 *
 * ## What "the same environment" actually means
 *
 * [org.robolectric.RobolectricTestRunner] is more than just a lifecycle wrapper.
 * For each test method it:
 *
 * 1. Resolves a per-method [`Configuration`][org.robolectric.pluginapi.config.ConfigurationStrategy.Configuration]
 *    (SDK level, manifest, looper mode, etc.) — derived from `@Config`,
 *    `@LooperMode`, `@GraphicsMode` and friends.
 * 2. Builds (or fetches from cache) an [`AndroidSandbox`][org.robolectric.internal.AndroidSandbox]
 *    for that configuration. The sandbox owns a special classloader that
 *    instruments every Android framework class on first reference, swapping
 *    `android.*` implementations for Robolectric's "shadow" classes.
 * 3. Re-loads the test class through that sandbox classloader so that
 *    Activity / View / Service references inside the test body actually
 *    pick up the shadowed Android types instead of the empty `android.jar`
 *    stub on the regular classpath.
 * 4. Hops onto the sandbox's main thread (Robolectric runs the test body
 *    on a dedicated `main` thread that owns Android's [android.os.Looper]).
 * 5. Calls `beforeTest`, which calls
 *    [TestEnvironment.setUpApplicationState][org.robolectric.internal.TestEnvironment.setUpApplicationState]
 *    — this is what bootstraps the `Application`, picks up the manifest,
 *    creates resource tables, etc.
 * 6. Runs the test body.
 * 7. Calls `afterTest`, which calls
 *    [tearDownApplication][org.robolectric.internal.TestEnvironment.tearDownApplication]
 *    and the per-test [TestLifecycle][org.robolectric.TestLifecycle] hook.
 * 8. Calls `finallyAfterTest`, which calls
 *    [resetState][org.robolectric.internal.TestEnvironment.resetState] to wipe
 *    static state for the next test.
 *
 * The implementation here reproduces (1)–(8) for a Kotest spec by piggybacking
 * on [ContainedRobolectricRunner] — a [RobolectricTestRunner] subclass that
 * exposes those protected hooks. We can't *exactly* match the JUnit 4 runner —
 * for instance, we don't support running the same test against multiple SDKs
 * the way Robolectric's `SdkPicker` does — but for the typical case of a
 * single SDK and a single configuration it is functionally equivalent.
 *
 * ## How Kotest plugs it together
 *
 * Two extension points are implemented:
 *
 * - [ConstructorExtension.instantiate] — called by the engine instead of
 *   `KClass.createInstance()` when a spec needs to be constructed. We use it
 *   to load the spec class through the sandbox classloader so that
 *   references in the spec body (Activities, Views, system services) bind
 *   against Robolectric's instrumented Android types rather than the stub
 *   `android.jar` on the regular classpath. Returning `null` from this method
 *   tells the engine "we have no opinion on this spec" — the next
 *   `ConstructorExtension` (or the default reflection-based path) takes over.
 *
 * - [TestCaseExtension.intercept] — called by the engine around every test
 *   execution. We use it to (a) make sure we're on the sandbox's main
 *   thread, (b) install the sandbox classloader as the thread context
 *   classloader, (c) run Robolectric's before/after lifecycle around the
 *   test body. Specs without [RobolectricTest] are passed straight through
 *   to [execute].
 *
 * ## Opt-in via [RobolectricTest]
 *
 * The extension is conservative: only specs annotated with [RobolectricTest]
 * get the sandbox treatment. This mirrors how JUnit 4 users opt in with
 * `@RunWith(RobolectricTestRunner::class)`, and — critically — means that
 * registering this extension globally does not punish specs that don't need
 * Android instrumentation. Sandbox construction takes seconds; we don't
 * want to pay that cost on every spec in a mixed project.
 *
 * ## Sandbox lifetime
 *
 * Building an [`AndroidSandbox`][org.robolectric.internal.AndroidSandbox]
 * is one of the most expensive things Robolectric does. The
 * [ContainedRobolectricRunner] held in [runner] caches its sandbox lazily
 * and reuses it across every test invocation. Reset between tests is
 * handled by Robolectric's own `afterTest` / `finallyAfterTest` hooks, which
 * we faithfully invoke in the `finally` of [intercept].
 */
class RobolectricExtension : ConstructorExtension, TestCaseExtension {

   /**
    * A single contained runner is held for the lifetime of this extension
    * instance. The runner's [ContainedRobolectricRunner.sdkEnvironment]
    * sandbox is what we load spec classes through and what owns the main
    * thread we hop tests onto.
    *
    * It is safe to share across specs because Robolectric resets all
    * per-test state in `afterTest` / `finallyAfterTest`.
    */
   private val runner = ContainedRobolectricRunner()

   /**
    * Returns true iff [this] spec class is annotated with [RobolectricTest].
    *
    * Uses plain Java reflection (`java.lang.Class.isAnnotationPresent`) rather
    * than `kotlin.reflect.full.findAnnotation` so the extension does not need
    * to pull `kotlin-reflect` onto the classpath - that library is large and
    * not something you want forced into an Android host-test runtime.
    *
    * [RobolectricTest] is `@Retention(RUNTIME)` and targets classes, so the
    * Java reflection path is sufficient.
    */
   private fun <T : Spec> KClass<T>.isRobolectricSpec(): Boolean =
      java.isAnnotationPresent(RobolectricTest::class.java)

   /**
    * Bootstraps a [RobolectricTest]-marked spec class through Robolectric's
    * instrumented classloader and instantiates it.
    *
    * Non-Robolectric specs return `null`, which is the engine-defined "I
    * have nothing to say" sentinel for [ConstructorExtension]s. The engine
    * will then either consult another [ConstructorExtension] or fall back to
    * its default reflection-based instantiation.
    *
    * Note that the returned [Spec] instance lives in a *different*
    * classloader than the engine's `Spec` type. The two only line up
    * because [ContainedRobolectricRunner.createClassLoaderConfig] excludes
    * `io.kotest.*` from sandbox acquisition — the engine and the bootstrapped
    * spec therefore share a single `Spec` symbol. If that exclusion is ever
    * removed, the `as Spec` cast below will fail at runtime.
    */
   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      if (!clazz.isRobolectricSpec()) return null

      // Load the spec class through the Robolectric sandbox classloader. The
      // returned Class<*> has the same fully-qualified name as the original
      // but lives in the sandbox classloader, so any Activity/View references
      // in its body resolve against Robolectric's shadowed Android stack.
      val bootstrapped = runner.bootstrap(clazz.java)

      // No-arg constructor. Specs always have one (DslDrivenSpec subclasses
      // typically take a configuration lambda with a default value, which the
      // compiler turns into a no-arg constructor as well).
      return bootstrapped.getDeclaredConstructor().newInstance() as Spec
   }

   /**
    * Wraps each [TestCase] in Robolectric's per-test lifecycle.
    *
    * Specs without [RobolectricTest] are passed straight through to
    * [execute] with no overhead. Annotated specs have their body executed:
    *
    *  1. ... on Robolectric's sandbox main thread (via [org.robolectric.internal.bytecode.Sandbox.runOnMainThread])
    *  2. ... with the thread's context classloader switched to the sandbox
    *         classloader (so Class.forName lookups inside the body see
    *         instrumented Android types)
    *  3. ... bracketed by [ContainedRobolectricRunner.containedBefore] and
    *         [ContainedRobolectricRunner.containedAfter] (which translate to
    *         Robolectric's [beforeTest]/[afterTest]/[finallyAfterTest])
    *
    * ### Thread / coroutine bridging
    *
    * The Kotest engine invokes this method from a coroutine on whatever
    * dispatcher the engine is using. Robolectric's [Sandbox.runOnMainThread]
    * is a blocking [Callable]-taking method: there is no suspending variant.
    * To bridge the two we call [runOnMainThread] with a [Callable] that
    * [runBlocking]s into [execute].
    *
    * For the typical Robolectric test (synchronous Android lifecycle code)
    * this is harmless: there is nothing on the calling coroutine context
    * that needs to keep running while the test body executes. If your test
    * body genuinely suspends on a real dispatcher, that dispatcher must be
    * one the sandbox main thread can reach — `Dispatchers.Default` is fine,
    * `Dispatchers.Main` (the real Android one) is not.
    *
    * ### Why we restore the context classloader
    *
    * Setting `Thread.currentThread().contextClassLoader` is observable
    * outside this method — ServiceLoader, Class.forName, JNDI, and a
    * surprising amount of test infrastructure all consult it. We restore
    * the previous value in a `finally` so we don't leave the sandbox
    * classloader installed on a thread that the engine reuses for a
    * non-Robolectric spec later.
    */
   override suspend fun intercept(
      testCase: TestCase,
      execute: suspend (TestCase) -> TestResult,
   ): TestResult {

      // Cheap rejection for specs that are not opting in. Without this the
      // extension would be a no-op tax on every test in the project.
      if (!testCase.spec::class.isRobolectricSpec()) {
         return execute(testCase)
      }

      // Hand control to the sandbox's main thread. Robolectric's lifecycle
      // hooks and the majority of its public APIs (Robolectric.buildActivity,
      // ShadowLooper.idle*, etc.) assert they're running on the thread that
      // owns the main Looper, which is the one runOnMainThread dispatches to.
      return runner.sdkEnvironment.runOnMainThread(
         Callable<TestResult> {

            // Install the sandbox classloader as the *context* classloader on
            // the main thread. This is what RobolectricTestRunner does itself
            // (see SandboxTestRunner.inSandboxThread) and is needed for any
            // code that reaches for Thread.currentThread().contextClassLoader
            // when looking up classes — ServiceLoader, reflection-based
            // factories, and so on.
            val previousLoader = Thread.currentThread().contextClassLoader
            Thread.currentThread().contextClassLoader = runner.sdkEnvironment.robolectricClassLoader

            // beforeTest sets up the Application, main Looper, shadow registry,
            // etc. This must happen *after* the context classloader is set and
            // *before* the test body runs.
            runner.containedBefore()
            try {
               // Bridge the suspending `execute` lambda to the blocking world
               // of Sandbox.runOnMainThread. `runBlocking` runs `execute` to
               // completion on the current (main) thread, which is what we
               // want — the Android lifecycle is synchronous and expects to
               // observe its state changes from the same thread that drives it.
               runBlocking { execute(testCase) }
            } finally {
               try {
                  // Even if the test threw, tear down so the next test starts
                  // from a clean state. afterTest + finallyAfterTest together
                  // are what RobolectricTestRunner runs in its own finally.
                  runner.containedAfter()
               } finally {
                  // Always restore the original context classloader, even if
                  // teardown threw, so we don't leave the sandbox classloader
                  // leaking out to whoever runs on this thread next.
                  Thread.currentThread().contextClassLoader = previousLoader
               }
            }
         }
      )
   }
}
