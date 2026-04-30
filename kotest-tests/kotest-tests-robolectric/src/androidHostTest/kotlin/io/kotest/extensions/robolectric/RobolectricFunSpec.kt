package io.kotest.extensions.robolectric

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec

/**
 * A [FunSpec] base class that runs every test inside a Robolectric sandbox.
 *
 * Use it exactly like a [FunSpec]:
 *
 * ```
 * class MyActivityTest : RobolectricFunSpec({
 *    test("activity should start") {
 *       Robolectric.buildActivity(MyActivity::class.java).use { ... }
 *    }
 * })
 * ```
 *
 * Subclasses are loaded by Robolectric's sandbox classloader so any Android API calls in
 * the test body see Robolectric's instrumented Android implementation rather than the
 * stub `android.jar`. Each test is wrapped with Robolectric's per-test setup and teardown
 * (Application initialization, shadow reset, etc.).
 *
 * Subclasses **must** live outside the `io.kotest.*` package, since that package is
 * intentionally excluded from Robolectric's classloader (so the engine, matchers, and
 * extension classes share identity across the boundary).
 */
@ApplyExtension(RobolectricExtension::class)
abstract class RobolectricFunSpec(body: RobolectricFunSpec.() -> Unit = {}) : FunSpec() {
   init {
      body()
   }
}
