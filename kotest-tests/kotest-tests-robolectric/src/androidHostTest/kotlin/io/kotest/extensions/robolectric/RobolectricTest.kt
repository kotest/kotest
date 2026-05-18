package io.kotest.extensions.robolectric

/**
 * Marker annotation that opts a Kotest [io.kotest.core.spec.Spec] into the
 * [RobolectricExtension] lifecycle.
 *
 * In a plain JUnit 4 world, a test class declares its desire to run inside Robolectric's
 * Android sandbox by writing `@RunWith(RobolectricTestRunner::class)`. Kotest specs are
 * discovered by the Kotest engine and not by JUnit 4's runner machinery, so the
 * `@RunWith` mechanism is not available to us. This annotation is the equivalent opt-in
 * for Kotest specs.
 *
 * Usage:
 *
 * ```kotlin
 * @RobolectricTest
 * class MyActivityTest : FunSpec({
 *    test("activity should start") {
 *       Robolectric.buildActivity(MyActivity::class.java).use { controller ->
 *          // ...
 *       }
 *    }
 * })
 * ```
 *
 * The annotation is read by [RobolectricExtension] when it is asked to instantiate a
 * spec or to intercept a test. Specs without this annotation are left untouched, even
 * when the extension is registered globally — this lets a single project mix Kotest
 * specs that need Robolectric with ones that do not, without paying the (expensive)
 * sandbox bootstrap cost for the latter.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RobolectricTest
