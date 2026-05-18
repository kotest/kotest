package com.sksamuel.kotest.robolectric

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.robolectric.RobolectricExtension
import io.kotest.matchers.string.shouldContain
import org.robolectric.RuntimeEnvironment

/**
 * Smoke test for `RuntimeEnvironment.getQualifiers()` under the Kotest
 * [RobolectricExtension].
 *
 * `getQualifiers()` returns the Android resource qualifier string for the
 * device the sandbox is simulating — a dash-separated combination of
 * locale, screen density, orientation, SDK level, etc., e.g.:
 *
 *     en-rUS-ldltr-sw411dp-w411dp-h659dp-normal-notlong-notround-nowidecg-lowdr-port-notnight-mdpi-finger-keysexposed-nokeys-navhidden-nonav-v33
 *
 * Calling it at all confirms that:
 *  - the Application has been bootstrapped (otherwise `Resources.getSystem()`
 *    would not be available),
 *  - the test is genuinely running on Robolectric's sandbox main thread
 *    (`getQualifiers()` reaches into ShadowDisplayManager which asserts the
 *    main-thread invariant),
 *  - and the spec is opted into Robolectric via `@ApplyExtension` (without
 *    the extension this call would land on the stub `android.jar` and fail
 *    with an `UnsupportedOperationException`).
 *
 * We assert on a few known-stable substrings — the default density
 * (`mdpi`), the default orientation (`port`), and the default locale
 * (`en-rUS`) — rather than the full string, which includes screen sizes
 * and other details that drift across Robolectric releases.
 */
@ApplyExtension(RobolectricExtension::class)
class RuntimeEnvironmentQualifiersTest : FunSpec({

   test("getQualifiers should describe the simulated device") {
      val qualifiers = RuntimeEnvironment.getQualifiers()
      // mdpi is Robolectric's default screen-density qualifier.
      qualifiers shouldContain "mdpi"
      // The default simulated device is in portrait orientation.
      qualifiers shouldContain "port"
      // Robolectric defaults to US English locale.
      qualifiers shouldContain "en-rUS"
   }
})
