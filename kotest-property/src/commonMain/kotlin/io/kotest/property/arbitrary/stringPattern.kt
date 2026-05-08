package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Generate strings that match the given regex pattern.
 *
 * Backed by:
 *  - JVM: [com.github.curious-odd-man:rgxgen](https://github.com/curious-odd-man/RgxGen)
 *  - All other targets except Android Native:
 *    [community.flock.kotlinx.rgxgen:kotlin-rgxgen](https://github.com/flock-community/kotlin-rgxgen)
 *  - androidNativeX86, androidNativeX64, androidNativeArm64: not supported —
 *    calling this function throws [UnsupportedOperationException]. `kotlin-rgxgen`
 *    0.0.3 does not publish a binary for those targets.
 *
 * Both backing libraries support a restricted subset of regular expression
 * constructs.
 */
expect fun Arb.Companion.stringPattern(pattern: String): Arb<String>
