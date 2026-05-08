package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Generate strings that match the given regex pattern.
 *
 * Backed by:
 *  - JVM: [com.github.curious-odd-man:rgxgen](https://github.com/curious-odd-man/RgxGen)
 *  - Every other target: [community.flock.kotlinx.rgxgen:kotlin-rgxgen](https://github.com/flock-community/kotlin-rgxgen)
 *
 * Both backing libraries support a restricted subset of regular expression
 * constructs.
 */
expect fun Arb.Companion.stringPattern(pattern: String): Arb<String>
